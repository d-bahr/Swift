package swiftmod.pipes.networks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.Swift;
import swiftmod.pipes.PipeTileEntity;
import swiftmod.pipes.PipeType;
import swiftmod.pipes.WormholeTileEntity;
import swiftmod.pipes.PipeType.ChannelTypeConversion;

public class PipeNetworks
{
	private enum PipeUpdateType
	{
		Add,
		Remove,
		Disconnect,
		Reconnect
	}
	
	private record PipeUpdate(PipeUpdateType type, PipeTileEntity pipe, Direction dir)
	{
	}

	private enum WormholeUpdateType
	{
		Add,
		Remove,
		AddType,
		RemoveType
	}
	
	private record WormholeUpdate(WormholeUpdateType type, WormholeTileEntity wormhole, PipeType pipeType, PipeChannelNetwork channelNetwork)
	{
	}
	
	public static void init()
	{
		s_pipeUpdates = new LinkedList<PipeUpdate>();
		s_wormholeUpdates = new LinkedList<WormholeUpdate>();
		s_addedPipes = new HashSet<PipeTileEntity>();
		s_addedWormholes = new HashSet<WormholeTileEntity>();
		s_networks = new HashSet<PipeNetwork>();
		// TODO: It probably makes more sense to have the hyper networks stored with the channel manager
		// or change up how the whole thing works entirely to simplify things. Really a channel
		// can just be a single ChannelSpec and the network is determined on load. For hyper networks
		// there's not really any significant load to recreate things, so this shouldn't be too bad.
		s_hyperNetworks = new HashSet<PipeHyperNetwork>();
		s_tick = 0;
	}
	
	// So due to how the logic works, if wormholes and pipes are added in the same tick,
	// then multiple connections to the same channel will be created. The workaround for
	// this is to add cache all the updates and then do some trickery when we tick all
	// the networks to avoid duplicating work.
	// (Note: we can't really do clever tricks with using s_tick because chunks may be
	// loaded and unloaded at any time, and therefore many pipes and wormholes could be
	// loaded in a single tick too.)
	public static void addPipe(PipeTileEntity pipe)
	{
		s_pipeUpdates.push(new PipeUpdate(PipeUpdateType.Add, pipe, null));
		s_addedPipes.add(pipe);
	}
	
	public static void removePipe(PipeTileEntity pipe)
	{
		s_pipeUpdates.push(new PipeUpdate(PipeUpdateType.Remove, pipe, null));
	}
	
	public static void reconnectPipe(PipeTileEntity pipe, Direction dir)
	{
		s_pipeUpdates.push(new PipeUpdate(PipeUpdateType.Reconnect, pipe, dir));
	}
	
	public static void disconnectPipe(PipeTileEntity pipe, Direction dir)
	{
		s_pipeUpdates.push(new PipeUpdate(PipeUpdateType.Disconnect, pipe, dir));
	}
	
	public static void addWormhole(WormholeTileEntity wormhole)
	{
		s_wormholeUpdates.push(new WormholeUpdate(WormholeUpdateType.Add, wormhole, null, null));
		s_addedWormholes.add(wormhole);
	}
	
	public static void addWormhole(WormholeTileEntity wormhole, PipeType type)
	{
		s_wormholeUpdates.push(new WormholeUpdate(WormholeUpdateType.AddType, wormhole, type, null));
	}
	
	public static void removeWormhole(WormholeTileEntity wormhole)
	{
		s_wormholeUpdates.push(new WormholeUpdate(WormholeUpdateType.Remove, wormhole, null, null));
	}
	
	public static void removeWormhole(WormholeTileEntity wormhole, PipeType type)
	{
		// Sanity check... this would be programmer error.
    	ChannelTypeConversion conversion = type.tryGetChannelType();
    	if (!conversion.hasConversion)
    	{
    		Swift.LOGGER.warn("Attempted to update wormhole with invalid channel type.");
    		return;
    	}
    	
		s_wormholeUpdates.push(new WormholeUpdate(WormholeUpdateType.RemoveType, wormhole, type, wormhole.getChannelNetwork(conversion.type)));
	}
	
	private static void processAddPipe(PipeTileEntity pipe)
	{
        Direction[] dirs = Direction.values();
		EnumSet<PipeType> pipeTypes = pipe.getPipeType();
		for (PipeType pipeType : pipeTypes)
		{
	        PipeNetwork network = null;
	    	for (Direction dir : dirs)
	    	{
	        	if (!pipe.canConnectDirection(dir))
	        		continue; // Pipe cannot be connected in this direction, so ignore.
	        	
	    		BlockEntity neighbor = pipe.getNeighborEntity(dir);
	            if (neighbor instanceof PipeTileEntity)
	            {
		            PipeTileEntity neighborPipe = (PipeTileEntity)neighbor;
		            
		            if (!neighborPipe.isPipeType(pipeType))
		            	continue; // Wrong pipe type. Cannot be part of the same network.
		            
	            	// Pipe hasn't been processed yet. Skip it.
	            	if (s_addedPipes.contains(neighborPipe))
	            		continue;
		        	
					PipeNetwork otherNetwork = neighborPipe.getNetwork(pipeType);
					
					// Special case; this can happen when a world is first loaded and pipes are adjacent but
					// no networks have actually been created yet.
					if (otherNetwork == null)
						continue;
					
					if (network == null)
					{
		    			// Attach to the neighboring network.
						network = otherNetwork;
						
						// It is necessary to do this now, in case networks need to be joined later,
						// so that the hyper network doesn't need to be updated manually ex post facto (which gets messy).
				    	pipe.setNetwork(network);
			            PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
			            if (hyperNetwork != null)
			            	hyperNetwork.addHandlersFrom(pipe);
					}
					else if (network != otherNetwork)
					{
			            PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
			            PipeHyperNetwork otherHyperNetwork = otherNetwork.getHyperNetwork();
			            
			            // First, reconcile the hyper networks. We need to do this while everything
			            // is still logically separated.
			            if (hyperNetwork != null)
			            {
			            	if (otherHyperNetwork != null)
			            	{
			            		joinHyperNetworks(hyperNetwork, otherHyperNetwork);
			            	}
			            	else
			            	{
			            		// Other network does not have a hyper network, which means it had no channels.
			            		// Add the handlers to this hyper network.
			            		hyperNetwork.addHandlersFrom(otherNetwork);
			            	}
			            }
			            else
			            {
			            	if (otherHyperNetwork != null)
			            	{
			            		// This network does not have a hyper network, but the other one does (or rather, did,
			            		// since it was detached). Connect all this network's handlers to the hyper network.
			            		otherHyperNetwork.addHandlersFrom(network);
			            		
			            		// Remove the network from normal processing
					            //Swift.LOGGER.info("1 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
			            		Swift.doAssert(s_networks.remove(network), "Network not part of normal processing list.");
			            	}
			            }
			            
						// Now we join the local networks together.
			            reassignPipes(network, neighborPipe, null, null);
			            
			            // Move all channels over to the new network.
			            network.takeChannelsFrom(otherNetwork);
	
			            otherNetwork.clear();
			            //Swift.LOGGER.info("2 Removed network {}, {}, {}", otherNetwork.getType(), otherNetwork.numChannelNetworks(), otherNetwork.hashCode());
			            s_networks.remove(otherNetwork);
					}
	            }
	    	}

	    	if (network == null)
	    	{
	    		// New network.
	    		PipeNetwork newNetwork = new PipeNetwork(pipeType);
	    		network = newNetwork;
	        	pipe.setNetwork(network);
	            //Swift.LOGGER.info("3 Added network {}, {}, {}", newNetwork.getType(), newNetwork.numChannelNetworks(), newNetwork.hashCode());
	    		s_networks.add(newNetwork);
	    	}
		}
    	
    	// After attaching the network, now handle any newly connected channel networks (wormholes)
    	// We have to do this after the network is known and assigned (and all pipes in the network
    	// have been properly reassigned).
    	for (Direction dir : dirs)
    	{
        	if (!pipe.canConnectDirection(dir))
        		continue; // Pipe cannot be connected in this direction, so ignore.
        	
    		BlockEntity neighbor = pipe.getNeighborEntity(dir);
            if (neighbor instanceof WormholeTileEntity)
            {
            	WormholeTileEntity wormhole = ((WormholeTileEntity)neighbor);
            	// If the wormhole exists in world but hasn't been processed yet, then skip it.
            	if (s_addedWormholes.contains(wormhole))
            		continue;
            	
                for (PipeType pipeType : pipeTypes)
                {
                	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
                	if (!conversion.hasConversion)
                		continue;

    	        	PipeNetwork network = pipe.getNetwork(pipeType);
    	        	
                	// It might seem like we can create hyperNetwork outside the loop and reuse it,
                	// but joinHyperNetworks() might change it and we don't necessarily know which
                	// network it will choose, so just get it every loop iteration. Performance
                	// hit should be very minimal.
                    PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
	    			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
	    			if (channelNetwork != null)
	    			{
			            PipeHyperNetwork otherHyperNetwork = channelNetwork.getHyperNetwork();
		    			if (channelNetwork.addNetwork(network))
		    			{
		    				if (hyperNetwork != null)
		    				{
		    					joinHyperNetworks(hyperNetwork, otherHyperNetwork);
		    				}
		    				else
		    				{
		    					// Connect all this network's handlers to the hyper network.
			            		otherHyperNetwork.addHandlersFrom(network);
			            		
			            		// Remove the network from normal processing
					            //Swift.LOGGER.info("4 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
			            		s_networks.remove(network);
		    				}
		    			}
	    			}
                }
            }
    	}
    	
    	s_addedPipes.remove(pipe);
	}
	
	private static void processRemovePipe(PipeTileEntity pipe)
	{
        Direction[] dirs = Direction.values();
        List<PipeNetwork> networks = pipe.getNetworks();
        HashSet<PipeChannelNetwork> dirtyChannelNetworks = new HashSet<PipeChannelNetwork>();
        
        for (PipeNetwork network : networks)
        {
	        if (network == null)
	        	continue;
	        
	        PipeType pipeType = network.getType();
	    	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
	
	        // Get a list of all the adjacent pipes first.
	        
	        boolean maybeAttachedToChannels = conversion.hasConversion;
	        List<PipeTileEntity> adjacent = new LinkedList<PipeTileEntity>();
	        List<Direction> adjacentDirs = new LinkedList<Direction>();
	    	for (Direction dir : dirs)
	    	{
	        	if (!pipe.canConnectDirection(dir))
	        		continue; // Pipe cannot be connected in this direction, so ignore.
	        	
	            BlockEntity neighbor = pipe.getNeighborEntity(dir);
	            if (neighbor instanceof PipeTileEntity)
	            {
		            PipeTileEntity neighborPipe = (PipeTileEntity)neighbor;
		            if (!neighborPipe.isPipeType(pipeType))
		            	continue; // Wrong pipe type. Cannot be part of the same network.

	            	// Pipe hasn't been processed yet. Skip it.
	            	if (s_addedPipes.contains(neighborPipe))
	            		continue;
	
		            adjacent.add((PipeTileEntity) neighborPipe);
		            adjacentDirs.add(dir);
	            }
	            else if (conversion.hasConversion && neighbor instanceof WormholeTileEntity)
	            {
	            	WormholeTileEntity wormhole = ((WormholeTileEntity)neighbor);
	            	// If the wormhole exists in world but hasn't been processed yet, then skip it.
	            	if (s_addedWormholes.contains(wormhole))
	            		continue;
	            	
	            	// Decrement the counter for the channel network.
	    			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
	    			if (channelNetwork != null && channelNetwork.removeNetwork(network))
	    			{
	    				if (network.numChannelNetworks() == 0)
	    				{
	        				// The final reference to the network was removed.
	        				channelNetwork.getHyperNetwork().removeHandlersFrom(network);

		    				// Add network back to the normal list to iterate as a local network.
	        	            //Swift.LOGGER.info("5 Added network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
		            		Swift.doAssert(s_networks.add(network), "Network already existed in normal network processing list.");
		            		
		            		// Enable a minor optimization later since we aren't connected to any channels anymore.
		            		maybeAttachedToChannels = false;
	    				}
	    				else
	    				{
	    					// In this case the network might be completely disconnected
	    					// but might not be. The only way to tell is to fully traverse
	    					// the hyper network and rebuild it.
	    					// Note that dirtyChannelNetworks can be a list and not a hashset,
	    					// because it is guaranteed that all elements will be unique anyway,
	    					// because we are checking only adjacent wormholes, and we only
	    					// run this code when they are actually fully disconnected, which
	    					// can only happen once.
	    					dirtyChannelNetworks.add(channelNetwork);
	    				}
	    			}
	            }
	    	}
	    	
	    	// TODO: There's probably room for more optimization; when there's only one channel connected we
	    	
	    	if (adjacent.size() == 0)
	    	{
	    		Swift.doAssert(network.numChannelNetworks() == 0, "Failed cleanup of channel network(s) after isolated pipe removed.");
	    		
	    		// Pipe is all by itself; delete the network.
	            //Swift.LOGGER.info("6 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	    		Swift.doAssert(s_networks.remove(network), "Isolated pipe was not part of isolated networks list.");
	    		
	    		// We can slightly optimize this if the number of dirty channels is exactly one. In this case,
	    		// we know the network wasn't chaining through to somewhere else, so we can simply remove
	    		// the handlers for this pipe from the hyper network.
	    		if (dirtyChannelNetworks.size() == 1)
	    		{
	    			Iterator<PipeChannelNetwork> iter = dirtyChannelNetworks.iterator();
	    			iter.next().getHyperNetwork().removeHandlersFrom(network);
	    			// Do this after removing handlers above, since this call will remove them from
	    			// the underlying local network itself.
	    			pipe.clearNetwork(pipeType);
	        		continue;
	    		}
	    	}

			pipe.clearNetwork(pipeType);

	    	if (adjacent.size() > 1)
	    	{
	    		// Iterate through the adjacent pipes. Traverse the entire adjacent network and add it to a new network.
	    		for (int i = 0; i < adjacent.size(); ++i)
	    		{
	                PipeTileEntity neighbor = adjacent.get(i);

	        		// Another optimization: before checking an adjacent pipe, see if it is the original
	        		// network. If it isn't the original network, then that means it must be attached to
	        		// another adjacent pipe, so it doesn't need to be updated.
	                if (neighbor.getNetwork(pipeType) != network)
	                	continue;
	                
	                PipeNetwork newNetwork = new PipeNetwork(pipeType);
	                HashMap<PipeChannelNetwork, Integer> neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
	                reassignPipes(newNetwork, neighbor, pipe, maybeAttachedToChannels ? neighboringChannels : null);
	                
	                if (neighboringChannels.isEmpty())
	                {
	                	// There are no wormholes connected to the network, so add it to the normal processing list.
        	            //Swift.LOGGER.info("7 Added network {}, {}, {}", newNetwork.getType(), newNetwork.numChannelNetworks(), newNetwork.hashCode());
	                	s_networks.add(newNetwork);
	                }
	                else
	                {
	                	// Remove all references to the original network, and add references to the new one.
	                	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
	                	for (Entry<PipeChannelNetwork, Integer> entry : entries)
	                	{
	                		PipeChannelNetwork channelNetwork = entry.getKey();
	                		// Completely obliterate the old network.
	                		channelNetwork.purgeNetwork(network);
	                		// Attach the network to the channel, and be sure to use the precise number of connections.
	                		channelNetwork.addNetwork(newNetwork, entry.getValue());
	                		
	                		dirtyChannelNetworks.add(channelNetwork);
	                	}
	                }
	    		}
	    		
	    		// If the above logic is correct, there should be no more channels attached to this network.
	    		// It is orphaned and now and can be removed.
	    		Swift.doAssert(network.numChannelNetworks() == 0, "Abandoned network has channels still attacked.");
	    		s_networks.remove(network);
	    		
	    		// TODO: This was supposed to be a more efficient way of handling the above
	    		// except the last adjacent network isn't changed so that we don't need to
	    		// reassign all the handlers and all that stuff. Unfortunately I haven't yet
	    		// figured out how to iterate all the pipes in the remaining network (at
	    		// index zero) to get the full list of attached channel networks (wormholes).
	    		// At least, not without putting all the pipes into a hashset so we can
	    		// cross them off as we iterate, but that's no more efficient than just creating
	    		// a new channel in the first place.
	    		// TL;DR: revisit this at some point.
	    		/*if (adjacent.get(0).getNetwork(pipeType) != network)
	    		{
	    			// The first pipe was connected to some other adjacent pipe, which
	    			// means that there are no more pipes connected to the original network.
    	            Swift.LOGGER.info("8 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	    			s_networks.remove(network);
	    		}
	    		else
	    		{
	    			// Traverse the remaining network to find any connected wormholes and reconcile channel networks as needed.
	    			if (maybeAttachedToChannels)
	    			{
	                    HashMap<PipeChannelNetwork, Integer> neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
	    				findConnectedChannels(adjacent.get(0), pipeType, neighboringChannels);
	    				
	                    if (neighboringChannels.isEmpty())
	                    {
	                    	// There are no wormholes connected to the network, so add it to the normal processing list.
	                    	// Note that it may already be added in this case.
	        	            Swift.LOGGER.info("9 Added network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	                    	s_networks.add(network);
	                    }
	                    else
	                    {
	                    	// Remove all references to the original network, and add references to the new one.
	                    	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
	                    	for (Entry<PipeChannelNetwork, Integer> entry : entries)
	                    	{
	                    		PipeChannelNetwork channelNetwork = entry.getKey();
	                    		
	                    		// Reset the number of connections between the local network and the channel network.
	                    		channelNetwork.setNetworkCount(network, entry.getValue());
	                    		
	                    		dirtyChannelNetworks.add(channelNetwork);
	                    	}
	                    }
	    			}
	    			else
	    			{
	    	            Swift.LOGGER.info("10 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	    				Swift.doAssert(s_networks.contains(network), "Network unexpectedly not found in normal processing list.");
	    			}
	    		}*/
	    	}
	    	else
	    	{
	    		// If there was only one connection, then we don't need to update the network, but we may
	    		// still need to update the channels.
	    		if (maybeAttachedToChannels)
	    		{
	    			dirtyChannelNetworks.addAll(network.getChannelNetworks());
	    		}
	    	}
        }
    	
		updateDirtyChannelNetworks(dirtyChannelNetworks);
	}
	
	// TODO: Is it worth have a separate function rather than passing in neighboringChannels? It would avoid
	// an if statement in an inner loop, but not sure if that's a meaningful improvement.
	private static void reassignPipes(PipeNetwork newNetwork,
			PipeTileEntity rootPipe,
			PipeTileEntity skip,
			HashMap<PipeChannelNetwork, Integer> neighboringChannels)
	{
        Direction[] dirs = Direction.values();
        
		rootPipe.setNetwork(newNetwork);
		
		PipeType pipeType = newNetwork.getType();
    	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
		
		// This uses a list to keep track of nodes to traverse. The alternative is a recursive approach, but it
		// is possible to have hundreds or thousands of pipes, so stack overflow is a real possibility with
		// a recursive method.
		LinkedList<PipeTileEntity> checkList = new LinkedList<PipeTileEntity>();
		checkList.push(rootPipe);
		while (!checkList.isEmpty())
		{
			PipeTileEntity pipe = checkList.pop();
			
	        for (Direction dir : dirs)
	        {
	        	if (!pipe.canConnectDirection(dir))
	        		continue;
	            BlockEntity neighbor = pipe.getNeighborEntity(dir);
	            if (neighbor instanceof PipeTileEntity)
	            {
		            PipeTileEntity neighborPipe = (PipeTileEntity)neighbor;
		            if (!neighborPipe.isPipeType(pipeType) || neighborPipe.getNetwork(pipeType) == newNetwork || neighborPipe == skip)
		            	continue; // Wrong pipe type or already attached to new network or the pipe is the one that was just removed. Skip.

	            	// Pipe hasn't been processed yet. Skip it.
	            	if (s_addedPipes.contains(neighborPipe))
	            		continue;
	            	
		            // Set the network before adding to the reassign list. This is a significant optimization
		            // that should eliminate duplicates from ever getting into the reassign list (because
		            // they should be trapped in the above if-statement).
		            neighborPipe.setNetwork(newNetwork);
		            checkList.add(neighborPipe);
	            }
	            else if (neighboringChannels != null && conversion.hasConversion && neighbor instanceof WormholeTileEntity)
	            {
	            	WormholeTileEntity wormhole = (WormholeTileEntity)neighbor;
	            	// If the wormhole exists in world but hasn't been processed yet, then skip it.
	            	if (s_addedWormholes.contains(wormhole))
	            		continue;
                	
	    			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
	    			if (channelNetwork != null)
	    				neighboringChannels.compute(channelNetwork, (n, v) -> { return (v == null) ? 1 : v + 1; });
	            }
	        }
		}
	}
	
	private static void processReconnectPipe(PipeTileEntity pipe, Direction dir)
	{
		EnumSet<PipeType> pipeTypes = pipe.getPipeType();
        BlockEntity neighbor = pipe.getNeighborEntity(dir);
        if (neighbor instanceof PipeTileEntity)
        {
            PipeTileEntity neighborPipe = (PipeTileEntity)neighbor;

        	// Pipe hasn't been processed yet. Do nothing
        	if (s_addedPipes.contains(neighborPipe))
        		return;
        	
            HashSet<PipeChannelNetwork> dirtyChannelNetworks = new HashSet<PipeChannelNetwork>();
            
    		for (PipeType pipeType : pipeTypes)
    		{
	            if (!neighborPipe.isPipeType(pipeType))
	            	continue;
	            
	            PipeNetwork network = pipe.getNetwork(pipeType);
	            PipeNetwork otherNetwork = neighborPipe.getNetwork(pipeType);
	            
	            if (network != otherNetwork)
	            {
	            	PipeNetwork newNetwork = new PipeNetwork(pipeType);
	                HashMap<PipeChannelNetwork, Integer> neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
	                // Important: the skip pipe must be null in order to establish a crossing between the pipes.
	                reassignPipes(newNetwork, pipe, null, neighboringChannels);
	                
	                if (neighboringChannels.isEmpty())
	                {
	                	// There are no wormholes connected to the network, so add it to the normal processing list.
	                	s_networks.add(newNetwork);
	                }
	                else
	                {
	                	// Remove all references to the original network, and add references to the new one.
	                	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
	                	for (Entry<PipeChannelNetwork, Integer> entry : entries)
	                	{
	                		PipeChannelNetwork channelNetwork = entry.getKey();
	                		// Completely obliterate the old network.
	                		channelNetwork.purgeNetwork(network);
	                		channelNetwork.purgeNetwork(otherNetwork);
	                		// Attach the network to the channel, and be sure to use the precise number of connections.
	                		channelNetwork.addNetwork(newNetwork, entry.getValue());
	                		
	                		dirtyChannelNetworks.add(channelNetwork);
	                	}
	                }
	                
	                // Destroy both the old networks.
                	s_networks.remove(network);
                	s_networks.remove(otherNetwork);
                
	            	// There are some optimizations that can be had above. I kinda gave up for the sake of time,
	            	// and I know that this code below should work even if it is a bit inefficient. We should
	            	// be able to retain one of the local networks and merge everything over without needing
	            	// to rebuild both networks as one.
	            	// The code below here is a first attempt at an alternative implementation.
	            	
	                /*HashMap<PipeChannelNetwork, Integer> neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
	                TreeMap<Integer, HashSet<PipeTransferHandler<?>>> insertHandlers = null;
	                TreeMap<Integer, HashSet<PipeTransferHandler<?>>> extractHandlers = null;
	                // Optimize a bit so we update the fewest pipes possible.
	                PipeNetwork theNetwork;
	                PipeNetwork removedNetwork;
	                PipeTileEntity fromPipe;
	                PipeTileEntity toPipe;
	                if (existingNetwork.numPipes() >= otherNetwork.numPipes())
	                {
	                	theNetwork = existingNetwork;
	                	removedNetwork = otherNetwork;
	                	fromPipe = neighborPipe;
	                	toPipe = pipe;
	                }
	                else
	                {
	                	theNetwork = otherNetwork;
	                	removedNetwork = existingNetwork;
	                	fromPipe = pipe;
	                	toPipe = neighborPipe;
	                }
	                
	                // Grab a copy of the handlers so we can move them to a new hyper network later if needed.
                	PipeHyperNetwork hyperNetwork = theNetwork.getHyperNetwork();
	                if (hyperNetwork != null && hyperNetwork != removedNetwork.getHyperNetwork())
	                {
		                insertHandlers = removedNetwork.cloneInsertHandlers();
		                extractHandlers = removedNetwork.cloneExtractHandlers();
	                }
                	reassignPipes(theNetwork, fromPipe, toPipe, neighboringChannels);
	                
	                if (neighboringChannels.isEmpty())
	                {
	                	PipeHyperNetwork hyperNetwork = theNetwork.getHyperNetwork();
	                	if (hyperNetwork != null && hyperNetwork != removedNetwork.getHyperNetwork())
	                	{
	                		// Add all the moved inserters and extractors to the hyper network.
	                		// TODO:
	                		hyperNetwork.addExtractHandler(null);
	                	}
	                }
	                else
	                {
	                	// Remove all references to the original network, and add references to the new one.
	                	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
	                	for (Entry<PipeChannelNetwork, Integer> entry : entries)
	                	{
	                		PipeChannelNetwork channelNetwork = entry.getKey();
	                		// Completely obliterate the old network.
	                		int count = channelNetwork.purgeNetwork(removedNetwork);
	                		Swift.doAssert(entry.getValue() == count, "Calculated channel count did not equal cached amount");
	                		// Attach the network to the channel, and be sure to use the precise number of connections.
	                		channelNetwork.addNetwork(theNetwork, entry.getValue());
	                		
	                		dirtyChannelNetworks.add(channelNetwork);
	                	}
	                }*/
	            }
    		}
    		
    		updateDirtyChannelNetworks(dirtyChannelNetworks);
        }
        else if (neighbor instanceof WormholeTileEntity)
        {
        	WormholeTileEntity wormhole = ((WormholeTileEntity)neighbor);
        	// If the wormhole exists in world but hasn't been processed yet, then skip it.
        	if (s_addedWormholes.contains(wormhole))
        		return;
        	
    		for (PipeType pipeType : pipeTypes)
    		{
	        	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
	        	if (!conversion.hasConversion)
	        		continue;

	        	PipeNetwork network = pipe.getNetwork(pipeType);
	        	
	        	// Get this before the network is modified in addNetwork.
                PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
	        	
	        	// Decrement the counter for the channel network.
				PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
				if (channelNetwork != null && channelNetwork.addNetwork(network))
				{
		            PipeHyperNetwork wormholeHyperNetwork = channelNetwork.getHyperNetwork();
    				if (hyperNetwork != null)
    				{
    					joinHyperNetworks(hyperNetwork, wormholeHyperNetwork);
    				}
    				else
    				{
    					// Connect all this network's handlers to the hyper network.
    					wormholeHyperNetwork.addHandlersFrom(network);
	            		
	            		// Remove the network from normal processing
	            		s_networks.remove(network);
    				}
				}
    		}
        }
	}
	
	private static void processDisconnectPipe(PipeTileEntity pipe, Direction dir)
	{
        HashSet<PipeChannelNetwork> dirtyChannelNetworks = new HashSet<PipeChannelNetwork>();
		EnumSet<PipeType> pipeTypes = pipe.getPipeType();
        BlockEntity neighbor = pipe.getNeighborEntity(dir);
        if (neighbor instanceof PipeTileEntity)
        {
            PipeTileEntity neighborPipe = (PipeTileEntity)neighbor;

        	// Pipe hasn't been processed yet. Do nothing.
        	if (s_addedPipes.contains(neighborPipe))
        		return;
        	
    		for (PipeType pipeType : pipeTypes)
    		{
	            if (!neighborPipe.isPipeType(pipeType))
	            	continue;
	            
	            PipeNetwork existingNetwork = pipe.getNetwork(pipeType);

	    		// The easiest way to do this I think is to call reassignPipes on the adjacent pipe. I don't think anything is needed, actually.
	    		// It is possible that this causes the current pipe to also switch networks (because they are connected some other way)
	    		// but there's no way to determine that except by iterating everything.
	            PipeNetwork newNetwork = new PipeNetwork(pipeType);
                HashMap<PipeChannelNetwork, Integer> neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
                reassignPipes(newNetwork, neighborPipe, pipe, neighboringChannels);
                
                if (neighboringChannels.isEmpty())
                {
                	// There are no wormholes connected to the network, so add it to the normal processing list.
                	s_networks.add(newNetwork);
                }
                else
                {
                	// Remove all references to the original network, and add references to the new one.
                	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
                	for (Entry<PipeChannelNetwork, Integer> entry : entries)
                	{
                		PipeChannelNetwork channelNetwork = entry.getKey();
                		// Completely obliterate the old network.
                		channelNetwork.purgeNetwork(existingNetwork);
                		// Attach the network to the channel, and be sure to use the precise number of connections.
                		channelNetwork.addNetwork(newNetwork, entry.getValue());
                		
                		dirtyChannelNetworks.add(channelNetwork);
                	}
                }
                
                // Check if the original pipe is attached to the new network. This would indicate that the pipes
                // were actually connected via some other route, which means we wouldn't need to change anything.
                // But in the below case, they are different, which means they are disconnected and we need
                // to recalculate the channel networks like normal.
                if (pipe.getNetwork(pipeType) != newNetwork)
                {
                	// TODO: There's probably a more efficient way to do this, but this will work for now.
    	            newNetwork = new PipeNetwork(pipeType);
                    neighboringChannels = new HashMap<PipeChannelNetwork, Integer>();
                    reassignPipes(newNetwork, pipe, neighborPipe, neighboringChannels);

                    if (neighboringChannels.isEmpty())
                    {
                    	// There are no wormholes connected to the network, so add it to the normal processing list.
                    	s_networks.add(newNetwork);
                    }
                    else
                    {
                    	// Remove all references to the original network, and add references to the new one.
                    	Set<Entry<PipeChannelNetwork, Integer>> entries = neighboringChannels.entrySet();
                    	for (Entry<PipeChannelNetwork, Integer> entry : entries)
                    	{
                    		PipeChannelNetwork channelNetwork = entry.getKey();
                    		// Completely obliterate the old network.
                    		channelNetwork.purgeNetwork(existingNetwork);
                    		// Attach the network to the channel, and be sure to use the precise number of connections.
                    		channelNetwork.addNetwork(newNetwork, entry.getValue());
                    		
                    		dirtyChannelNetworks.add(channelNetwork);
                    	}
                    }
                }

            	Swift.doAssert(existingNetwork.isEmpty(), "Expected pipe network to be empty and wasn't");
	    		Swift.doAssert(existingNetwork.numChannelNetworks() == 0, "Abandoned network has channels still attacked.");
            	s_networks.remove(existingNetwork);
    		}
        }
        else if (neighbor instanceof WormholeTileEntity)
        {
        	WormholeTileEntity wormhole = (WormholeTileEntity)neighbor;
        	// If the wormhole exists in world but hasn't been processed yet, then skip it.
        	if (s_addedWormholes.contains(wormhole))
        		return;
        	
    		for (PipeType pipeType : pipeTypes)
    		{
	        	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
	        	if (!conversion.hasConversion)
	        		continue;
	        	
	        	PipeNetwork network = pipe.getNetwork(pipeType);
	        	
	        	// Decrement the counter for the channel network.
				PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
				if (channelNetwork != null && channelNetwork.removeNetwork(network))
				{
					if (network.numChannelNetworks() == 0)
					{
	    				// The final reference to the network was removed.
	    				channelNetwork.getHyperNetwork().removeHandlersFrom(network);
	
	    				// Add network back to the normal list to iterate as a local network.
	            		Swift.doAssert(s_networks.add(network), "Network already existed in normal network processing list.");
					}
					else
					{
						// In this case the network might be completely disconnected
						// but might not be. The only way to tell is to fully traverse
						// the hyper network and rebuild it.
						dirtyChannelNetworks.add(channelNetwork);
					}
				}
    		}
        }
		
		updateDirtyChannelNetworks(dirtyChannelNetworks);
	}

	/*private static void findConnectedChannels(PipeTileEntity rootPipe,
			PipeType pipeType, HashMap<PipeChannelNetwork, Integer> neighboringChannels)
	{
        Direction[] dirs = Direction.values();
    	ChannelTypeConversion conversion = pipeType.tryGetChannelType();
    	if (!conversion.hasConversion)
    		return;
		
		// This uses a list to keep track of nodes to traverse. The alternative is a recursive approach, but it
		// is possible to have hundreds or thousands of pipes, so stack overflow is a real possibility with
		// a recursive method.
		LinkedList<PipeTileEntity> checkList = new LinkedList<PipeTileEntity>();
		checkList.push(rootPipe);
		while (!checkList.isEmpty())
		{
			PipeTileEntity pipe = checkList.pop();
			
	        for (int i = 0; i < dirs.length; ++i)
	        {
	            BlockEntity neighbor = pipe.getNeighborEntity(dirs[i]);
	            if (neighbor instanceof WormholeTileEntity)
	            {
	            	WormholeTileEntity wormhole = (WormholeTileEntity)neighbor;
	                	
	    			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
	    			if (channelNetwork != null)
		    			neighboringChannels.compute(channelNetwork, (n, v) -> { return (v == null) ? 1 : v + 1; });
	            }
	        }
		}
	}*/
	
	private static void processAddWormhole(WormholeTileEntity wormhole)
	{
		HashMap<PipeType, LinkedList<PipeNetwork>> adjacentNetworks = new HashMap<PipeType, LinkedList<PipeNetwork>>();
        Direction[] dirs = Direction.values();
    	for (Direction dir : dirs)
    	{
    		BlockEntity neighbor = wormhole.getNeighborEntity(dir);
            if (!(neighbor instanceof PipeTileEntity))
            	continue;
            
            // TODO: Adjacent wormholes.

            PipeTileEntity pipe = (PipeTileEntity)neighbor;
        	// Pipe hasn't been processed yet. Skip it.
        	if (s_addedPipes.contains(pipe))
        		continue;
        	
        	if (!pipe.canConnectDirection(dir.getOpposite()))
        		continue; // Pipe is disconnected via wrench.
            EnumSet<PipeType> types = pipe.getPipeType();
            for (PipeType type : types)
            {
            	PipeNetwork network = pipe.getNetwork(type);
            	if (network != null) // Handle special case on world load where pipes may be present but not yet have networks.
            		adjacentNetworks.computeIfAbsent(type, k -> new LinkedList<PipeNetwork>()).add(network);
            }
    	}
    	
    	Set<Entry<PipeType, LinkedList<PipeNetwork>>> entries = adjacentNetworks.entrySet();
    	for (Entry<PipeType, LinkedList<PipeNetwork>> entry : entries)
    	{
    		PipeType type = entry.getKey();
    		
        	ChannelTypeConversion conversion = type.tryGetChannelType();
        	if (!conversion.hasConversion) // This check isn't really necessary since the list if populated from real pipes...
        		continue;

    		LinkedList<PipeNetwork> networks = entry.getValue();
    		if (networks.isEmpty())
    			continue;
    		
    		// For each type, link together all of the networks via a "channel network."
			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
			if (channelNetwork == null)
				continue;
    		
    		for (PipeNetwork network : networks)
    		{
    			// This check must happen beforehand because the network
    			// may already be attached to a channel network.
                PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
	            PipeHyperNetwork otherHyperNetwork = channelNetwork.getHyperNetwork();
    			if (channelNetwork.addNetwork(network))
    			{
    				if (hyperNetwork != null)
    				{
    					joinHyperNetworks(hyperNetwork, otherHyperNetwork);
    				}
    				else
    				{
    					// Connect all this network's handlers to the hyper network.
	            		otherHyperNetwork.addHandlersFrom(network);
	            		
	            		// Remove the network from normal processing
	    	            //Swift.LOGGER.info("11 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	            		Swift.doAssert(s_networks.remove(network), "Network not part of normal processing list.");
    				}
    			}
    		}
    	}
    	
    	// This is a bit of a hack.
    	// When a wormhole is loaded from world, it may reference some channels that are
    	// brand new and haven't been loaded into s_hyperNetworks yet. This is because
    	// the networks are lazily loaded. I don't know of a great way around this right
    	// now, other than to force new wormholes to attach their networks if present.
    	// This should be fine, if a bit inefficient, since s_hyperNetworks is a HashSet.
    	List<PipeChannelNetwork> channelNetworks = wormhole.getAllChannelNetworks();
    	for (PipeChannelNetwork channelNetwork : channelNetworks)
    		s_hyperNetworks.add(channelNetwork.getHyperNetwork());
	}
	
	private static void processAddWormhole(WormholeTileEntity wormhole, PipeType type)
	{
		// Sanity check... this would be programmer error.
    	ChannelTypeConversion conversion = type.tryGetChannelType();
    	if (!conversion.hasConversion)
    	{
    		Swift.LOGGER.warn("Attempted to update wormhole with invalid channel type.");
    		return;
    	}
    	
		// For each type, link together all of the networks via a "channel network."
		PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
		if (channelNetwork != null)
		{
			LinkedList<PipeNetwork> adjacentNetworks = new LinkedList<PipeNetwork>();
	        Direction[] dirs = Direction.values();
	    	for (Direction dir : dirs)
	    	{
	    		BlockEntity neighbor = wormhole.getNeighborEntity(dir);
	            if (!(neighbor instanceof PipeTileEntity))
	            	continue;
	            
	            // TODO: Adjacent wormholes.
	
	            PipeTileEntity pipe = (PipeTileEntity)neighbor;
            	// Pipe hasn't been processed yet. Skip it.
            	if (s_addedPipes.contains(pipe))
            		continue;
            	
	        	if (!pipe.canConnectDirection(dir.getOpposite()))
	        		continue;
	            if (pipe.isPipeType(type))
	            	adjacentNetworks.add(pipe.getNetwork(type));
	    	}
    		
    		for (PipeNetwork network : adjacentNetworks)
    		{
    			// This check must happen beforehand because the network
    			// may already be attached to a channel network.
                PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
	            PipeHyperNetwork otherHyperNetwork = channelNetwork.getHyperNetwork();
    			if (channelNetwork.addNetwork(network))
    			{
    				if (hyperNetwork != null)
    				{
    					joinHyperNetworks(hyperNetwork, otherHyperNetwork);
    				}
    				else
    				{
    					// Connect all this network's handlers to the hyper network.
	            		otherHyperNetwork.addHandlersFrom(network);
	            		
	            		// Remove the network from normal processing
	    	            //Swift.LOGGER.info("12 Removed network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	            		Swift.doAssert(s_networks.remove(network), "Network not part of normal processing list.");
    				}
    			}
    		}
    	}
	}
	
	// Helper function used when channel networks must be combined. In principle this should be faster than calling
	// updateDirtyChannelNetwork() because this retains one of the hyper networks.
	private static void joinHyperNetworks(PipeHyperNetwork a, PipeHyperNetwork b)
	{
		if (a == b)
			return; // Nothing to do here.

		// For efficiency, copy everything from the smaller hyper network to the larger one.
		PipeHyperNetwork hyperNetworkFrom;
		PipeHyperNetwork hyperNetworkTo;
		if (a.numNetworks() < b.numNetworks())
		{
			hyperNetworkFrom = a;
			hyperNetworkTo = b;
		}
		else
		{
			hyperNetworkFrom = b;
			hyperNetworkTo = a;
		}
		
		// This moves over all the channels and all the handlers in an efficient way
		// (at least, as efficient as we can reasonably get, since we don't do any
		// excess iteration over unchanged parts of the network).
		hyperNetworkFrom.moveTo(hyperNetworkTo);
		
		s_hyperNetworks.remove(hyperNetworkFrom);
	}
	
	private static void processRemoveWormhole(WormholeTileEntity wormhole)
	{
		HashMap<PipeType, LinkedList<PipeNetwork>> adjacentNetworks = new HashMap<PipeType, LinkedList<PipeNetwork>>();
        Direction[] dirs = Direction.values();
    	for (Direction dir : dirs)
    	{
    		BlockEntity neighbor = wormhole.getNeighborEntity(dir);
            if (!(neighbor instanceof PipeTileEntity))
            	continue;
            
            // TODO: Adjacent wormholes.

            PipeTileEntity pipe = (PipeTileEntity)neighbor;
        	// Pipe hasn't been processed yet. Skip it.
        	if (s_addedPipes.contains(pipe))
        		continue;
        	
        	if (!pipe.canConnectDirection(dir.getOpposite()))
        		continue; // Pipe cannot be connected, so ignore.
            EnumSet<PipeType> types = pipe.getPipeType();
            for (PipeType type : types)
            	adjacentNetworks.computeIfAbsent(type, k -> new LinkedList<PipeNetwork>()).add(pipe.getNetwork(type));
    	}

    	Set<Entry<PipeType, LinkedList<PipeNetwork>>> entries = adjacentNetworks.entrySet();
    	for (Entry<PipeType, LinkedList<PipeNetwork>> entry : entries)
    	{
    		PipeType type = entry.getKey();
    		
        	ChannelTypeConversion conversion = type.tryGetChannelType();
        	if (!conversion.hasConversion) // This check isn't really necessary since the list if populated from real pipes...
        		continue;

			PipeChannelNetwork channelNetwork = wormhole.getChannelNetwork(conversion.type);
			if (channelNetwork == null)
				continue;
        	
            List<PipeNetwork> dirtyNetworks = new LinkedList<PipeNetwork>();
				
    		LinkedList<PipeNetwork> networks = entry.getValue();
    		for (PipeNetwork network : networks)
    		{
    			if (channelNetwork.removeNetwork(network))
    			{
    				// Even though the channel was removed, the local network may still be
    				// connected to another channel, and thus still part of a hyper network.
    				// If it is obvious that the network can't be part of a hyper network
    				// anymore, then remove it entirely from the hyper network.
    				if (network.numChannelNetworks() == 0)
    				{
        				// The final reference to the network was removed.
        				channelNetwork.getHyperNetwork().removeHandlersFrom(network);

	    				// Add network back to the normal list to iterate as a local network.
        	            //Swift.LOGGER.info("13 Added network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	            		Swift.doAssert(s_networks.add(network), "Network already existed in normal network processing list.");
    				}
    				else
    				{
    					// In this case the network might be completely disconnected
    					// but might not be. The only way to tell is to fully traverse
    					// the hyper network and rebuild it.
    					dirtyNetworks.add(network);
    				}
    			}
    		}
    		
    		if (dirtyNetworks.size() > 0)
    		{
    			// Generate a list of all dirty channel networks. This takes a bit of work but will pay off with
    			// more than 1 channel network, especially if the networks have lots of handlers, because we can
    			// optimize and avoid calling refreshExtractAndInsertHandlers aHandleressary number of times.
    			HashSet<PipeChannelNetwork> dirtyChannelNetworks = new HashSet<PipeChannelNetwork>();
    			dirtyChannelNetworks.add(channelNetwork);
    			for (PipeNetwork dirtyNetwork : dirtyNetworks)
    				dirtyChannelNetworks.addAll(dirtyNetwork.getChannelNetworks());
    			updateDirtyChannelNetworks(dirtyChannelNetworks);
    		}
    	}
	}
	
	private static void processRemoveWormhole(WormholeTileEntity wormhole, PipeType type, PipeChannelNetwork channelNetwork)
	{
		// Sanity check... this would be programmer error.
    	ChannelTypeConversion conversion = type.tryGetChannelType();
    	if (!conversion.hasConversion)
    	{
    		Swift.LOGGER.warn("Attempted to update wormhole with invalid channel type.");
    		return;
    	}
    	
		// For each type, link together all of the networks via a "channel network."
		if (channelNetwork != null)
		{
			LinkedList<PipeNetwork> adjacentNetworks = new LinkedList<PipeNetwork>();
	        Direction[] dirs = Direction.values();
	    	for (Direction dir : dirs)
	    	{
	    		BlockEntity neighbor = wormhole.getNeighborEntity(dir);
	            if (!(neighbor instanceof PipeTileEntity))
	            	continue;
	            
	            // TODO: Adjacent wormholes.
	
	            PipeTileEntity pipe = (PipeTileEntity)neighbor;
            	// Pipe hasn't been processed yet. Skip it.
            	if (s_addedPipes.contains(pipe))
            		continue;
            	
	        	if (!pipe.canConnectDirection(dir.getOpposite()))
	        		continue; // Pipe cannot be connected, so ignore.
	            if (pipe.isPipeType(type))
	            	adjacentNetworks.add(pipe.getNetwork(type));
	    	}
	    	
            List<PipeNetwork> dirtyNetworks = new LinkedList<PipeNetwork>();

    		for (PipeNetwork network : adjacentNetworks)
    		{
    			if (channelNetwork.removeNetwork(network))
    			{
    				// Even though the channel was removed, the local network may still be
    				// connected to another channel, and thus still part of a hyper network.
    				// If it is obvious that the network can't be part of a hyper network
    				// anymore, then remove it entirely from the hyper network.
    				if (network.numChannelNetworks() == 0)
    				{
        				// The final reference to the network was removed.
        				channelNetwork.getHyperNetwork().removeHandlersFrom(network);

	    				// Add network back to the normal list to iterate as a local network.
        	            //Swift.LOGGER.info("14 Added network {}, {}, {}", network.getType(), network.numChannelNetworks(), network.hashCode());
	            		Swift.doAssert(s_networks.add(network), "Network already existed in normal network processing list.");
    				}
    				else
    				{
    					// In this case the network might be completely disconnected
    					// but might not be. The only way to tell is to fully traverse
    					// the hyper network and rebuild it.
    					dirtyNetworks.add(network);
    				}
    			}
    		}
    		
    		if (dirtyNetworks.size() > 0)
    		{
    			// Generate a list of all dirty channel networks. This takes a bit of work but will pay off with
    			// more than 1 channel network, especially if the networks have lots of handlers, because we can
    			// optimize and avoid calling refreshExtractAndInsertHandlers aHandleressary number of times.
    			HashSet<PipeChannelNetwork> dirtyChannelNetworks = new HashSet<PipeChannelNetwork>();
    			dirtyChannelNetworks.add(channelNetwork);
    			for (PipeNetwork dirtyNetwork : dirtyNetworks)
    				dirtyChannelNetworks.addAll(dirtyNetwork.getChannelNetworks());
    			updateDirtyChannelNetworks(dirtyChannelNetworks);
    		}
		}
	}
	
	public static void addChannelNetwork(PipeChannelNetwork network)
	{
		if (network != null)
			s_hyperNetworks.add(network.getHyperNetwork());
	}
	
	public static void removeChannelNetwork(PipeChannelNetwork network)
	{
		if (network != null)
		{
			// Rebuild all adjacent parts of the network.
			// Reset the channel's hyper network to a new network which
			// is not attached to the list of networks, so it doesn't tick.
			// This is just in case there's some lingering network artifacts
			// that don't update right away (and the rest of the logic in
			// this class doesn't really handle null hyper networks).
			PipeHyperNetwork hyperNetwork = network.getHyperNetwork();
			network.setHyperNetwork(new PipeHyperNetwork(network.getType()));
			if (hyperNetwork.isEmpty())
				s_hyperNetworks.remove(hyperNetwork); // Well, just so happened there was nothing attached anyway.
			else
				updateDirtyChannelNetworks(hyperNetwork.getNetworks());
		}
	}

	// Gets a full list of all channel networks connected to a given local network.
	// The returned list constitutes a hyper network.
	/*private static <Type> HashSet<PipeChannelNetwork>
		findAllConnectedChannelNetworks(PipeNetwork network)
	{
		HashSet<PipeChannelNetwork> connectedNetworks = new HashSet<PipeChannelNetwork>();
		findAllConnectedChannelNetworksRecurse(network, connectedNetworks);
		return connectedNetworks;
	}*/

	// Gets a full list of all channel networks connected to a given channel
	// network. Returns a set of said networks. Returned set always contains
	// the channel network passed as a parameter.
	// The returned list constitutes a hyper network.
	private static HashSet<PipeChannelNetwork> findAllConnectedChannelNetworks(PipeChannelNetwork channelNetwork)
	{
		HashSet<PipeChannelNetwork> connectedNetworks = new HashSet<PipeChannelNetwork>();
		connectedNetworks.add(channelNetwork);
		findAllConnectedChannelNetworksRecurse(channelNetwork, connectedNetworks);
		return connectedNetworks;
	}
	
	// Worker method; don't call directly.
	private static void findAllConnectedChannelNetworksRecurse(PipeChannelNetwork channelNetwork,
				HashSet<PipeChannelNetwork> connectedNetworks)
	{
		Set<PipeNetwork> adjacentNetworks = channelNetwork.getNetworks();
		for (PipeNetwork network : adjacentNetworks)
			findAllConnectedChannelNetworksRecurse(network, connectedNetworks);
	}

	// Worker method; don't call directly.
	private static void findAllConnectedChannelNetworksRecurse(PipeNetwork network,
				HashSet<PipeChannelNetwork> connectedNetworks)
	{
		Set<PipeChannelNetwork> adjacentChannelNetworks = network.getChannelNetworks();
		for (PipeChannelNetwork channelNetwork : adjacentChannelNetworks)
		{
			if (connectedNetworks.add(channelNetwork))
				findAllConnectedChannelNetworksRecurse(channelNetwork, connectedNetworks);
		}
	}
	
	// To be called from "remove" functions only due to assumptions made about the current state of the network.
	/*private static <Type> void updateDirtyChannelNetworks(PipeNetwork network)
	{
		PipeHyperNetwork oldHyperNetwork = network.getHyperNetwork();
		if (oldHyperNetwork == null)
			return; // No channels.
		
		PipeHyperNetwork hyperNetwork = new PipeHyperNetwork();
		HashSet<PipeChannelNetwork> connectedNetworks = findAllConnectedChannelNetworks(network);
		for (PipeChannelNetwork channelNetwork : connectedNetworks)
		{
			channelNetwork.setHyperNetwork(hyperNetwork);
		}

		// Check the old hyper network and remove it if it is empty. It is possible that it might
		// still be connected to something else (because a network was cut in two).
		if (oldHyperNetwork.isEmpty())
			s_hyperNetworks.remove(oldHyperNetwork);
        
        hyperNetwork.refreshExtractAndInsertHandlers();
		
		// Note that the old hyper network would need to be refreshed as well. We don't explicitly do that here
        // because the logic in removeWormhole and removePipe should iterate over all the segmented networks,
        // so doing it here is redundant and would be inefficient.
        
        // Add the new hyper network.
		s_hyperNetworks.add(hyperNetwork);
	}*/

	// To be called from "remove" functions only due to assumptions made about the current state of the network.
	private static void updateDirtyChannelNetwork(PipeChannelNetwork channelNetwork)
	{
		PipeHyperNetwork oldHyperNetwork = channelNetwork.getHyperNetwork();
		PipeHyperNetwork hyperNetwork = new PipeHyperNetwork(oldHyperNetwork.getType());
		HashSet<PipeChannelNetwork> connectedNetworks = findAllConnectedChannelNetworks(channelNetwork);
		for (PipeChannelNetwork network : connectedNetworks)
		{
			network.setHyperNetwork(hyperNetwork);
		}
		
		// Check the old hyper network and remove it if it is empty. It is possible that it might
		// still be connected to something else (because a network was cut in two).
		if (oldHyperNetwork.isEmpty())
			s_hyperNetworks.remove(oldHyperNetwork);
        
        hyperNetwork.refreshExtractAndInsertHandlers();
		
		// Note that the old hyper network would need to be refreshed as well. We don't explicitly do that here
        // because the logic in removeWormhole and removePipe should iterate over all the segmented networks,
        // so doing it here is redundant and would be inefficient.
        
        // Add the new hyper network.
		s_hyperNetworks.add(hyperNetwork);
	}

	// To be called from "remove" functions only due to assumptions made about the current state of the network.
	private static void updateDirtyChannelNetworks(List<PipeChannelNetwork> channelNetworks)
	{
		if (channelNetworks.isEmpty())
			return;

		// Special case of updating dirty channels. When we are given a list, we assume
		// that any of them could be connected to each other. Therefore, we keep track
		// of whether or not a channel has updated its hyper network while we are iterating,
		// so that we avoid updating channels that have already been updated.
		// This is a substantial optimization when we consider that calling
		// updateDirtyChannelNetwork() refreshes the entire list of insert/extract handlers,
		// which could be very expensive for a large network.
		ArrayList<PipeHyperNetwork> cachedHyperNetworks =
				new ArrayList<PipeHyperNetwork>(channelNetworks.size());
		
		for (PipeChannelNetwork channelNetwork : channelNetworks)
			cachedHyperNetworks.add(channelNetwork.getHyperNetwork());

		int i = 0;
		for (PipeChannelNetwork channelNetwork : channelNetworks)
    	{
    		if (channelNetwork.getHyperNetwork() == cachedHyperNetworks.get(i))
    			updateDirtyChannelNetwork(channelNetwork);
    		i++;
    	}
	}

	// To be called from "remove" functions only due to assumptions made about the current state of the network.
	private static void updateDirtyChannelNetworks(Set<PipeChannelNetwork> channelNetworks)
	{
		if (channelNetworks.isEmpty())
			return;

		// It is slightly inefficient to make a copy of the set to generate a list, but the general (almost definitely true)
		// assumption is that the number of channel networks is far smaller than the number of handlers (or that the
		// number of channels is simply too small to matter). refreshExtractAndInsertHandlers is so costly that doing
		// this step here is worth it.
		updateDirtyChannelNetworks(new LinkedList<PipeChannelNetwork>(channelNetworks));
	}
	
	public static void tickHyperNetworks()
	{
        Iterator<PipeHyperNetwork> iter = s_hyperNetworks.iterator();
        while (iter.hasNext())
        {
        	PipeHyperNetwork hyperNetwork = iter.next();
        	// An old hyper network could be left dangling after calling removeWormhole(),
        	// so check it and remove if necessary.
        	if (hyperNetwork.isEmpty())
        		iter.remove();
        	else
        		hyperNetwork.tick(s_tick);
        }
	}
	
	public static void tickLocalNetworks()
	{
        Iterator<PipeNetwork> iter = s_networks.iterator();
        while (iter.hasNext())
        {
        	PipeNetwork network = iter.next();
        	// I think the other logic should be correct so that this is unnecessary,
        	// but this is safe regardless.
        	if (network.isEmpty())
        		iter.remove();
        	else
        		network.tick(s_tick);
        }
	}
	
	private static void processPipeUpdates()
	{
		for (PipeUpdate update : s_pipeUpdates)
		{
			switch (update.type)
			{
			case Add:
				processAddPipe(update.pipe);
				break;
			case Remove:
				processRemovePipe(update.pipe);
				break;
			case Disconnect:
				processDisconnectPipe(update.pipe, update.dir);
				break;
			case Reconnect:
				processReconnectPipe(update.pipe, update.dir);
				break;
			default:
				Swift.LOGGER.warn("Invalid pipe update type.");
				break;
			}
		}
		s_pipeUpdates.clear();
		s_addedPipes.clear();
	}
	
	private static void processWormholeUpdates()
	{
		for (WormholeUpdate update : s_wormholeUpdates)
		{
			switch (update.type)
			{
			case Add:
				processAddWormhole(update.wormhole);
				break;
			case Remove:
				processRemoveWormhole(update.wormhole);
				break;
			case AddType:
				processAddWormhole(update.wormhole, update.pipeType);
				break;
			case RemoveType:
				processRemoveWormhole(update.wormhole, update.pipeType, update.channelNetwork);
				break;
			default:
				Swift.LOGGER.warn("Invalid pipe update type.");
				break;
			}
		}
		s_wormholeUpdates.clear();
		s_addedWormholes.clear();
	}
	
	public static void tickNetworks()
	{
		s_tick++;
		// Process pipe updates first, which will skip adjacent wormholes that haven't
		// yet been processed.
		processPipeUpdates();
		processWormholeUpdates();
		
		// Tick the networks after processing pipe updates.
		tickHyperNetworks();
		tickLocalNetworks();
	}
	
	public static void clear()
	{
		s_pipeUpdates.clear();
		s_wormholeUpdates.clear();
		s_addedPipes.clear();
		s_addedWormholes.clear();
		s_networks.clear();
		s_hyperNetworks.clear();
		s_tick = 0;
	}
	
	private static LinkedList<PipeUpdate> s_pipeUpdates;
	private static LinkedList<WormholeUpdate> s_wormholeUpdates;
	private static HashSet<PipeTileEntity> s_addedPipes;
	private static HashSet<WormholeTileEntity> s_addedWormholes;
	private static HashSet<PipeNetwork> s_networks;
	private static HashSet<PipeHyperNetwork> s_hyperNetworks;
	private static int s_tick;
}
