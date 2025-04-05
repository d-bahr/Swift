package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.TileEntityBase;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.Channel;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelType;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.pipes.networks.PipeChannelNetwork;
import swiftmod.pipes.networks.PipeNetworks;

public class WormholeTileEntity extends TileEntityBase implements MenuProvider
{
	public WormholeTileEntity(BlockPos pos, BlockState state)
	{
        this(SwiftTileEntities.s_wormholeTileEntityType.get(), pos, state, new ChannelConfigurationDataCache());
	}

	public WormholeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
        this(type, pos, state, new ChannelConfigurationDataCache());
	}

	public WormholeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ChannelConfigurationDataCache cache)
	{
        super(type, pos, state);
        m_cache = cache;
        m_init = true;
        m_isRemoving = false;
	}
	
	public ChannelConfigurationDataCache getCache()
	{
		return m_cache;
	}

    public void onChannelChanged(swiftmod.common.client.ChannelConfigurationPacket.Type type, Channel<ChannelData> channel)
    {
        switch (type)
        {
        case Add:
            {
                BaseChannelManager manager = BaseChannelManager.getManager();
                PipeChannelNetwork channelNetwork = new PipeChannelNetwork(channel.spec.type.toPipeType());
                boolean isNewChannel = manager.putIfAbsent(channel.spec, channelNetwork);
                manager.save();
                if (isNewChannel)
                	PipeNetworks.addChannelNetwork(channelNetwork);
            }
            break;
        case Delete:
            {
                BaseChannelManager manager = BaseChannelManager.getManager();
                PipeChannelNetwork channelNetwork = manager.delete(channel.spec);
                manager.save();
                if (channelNetwork != null)
                	PipeNetworks.removeChannelNetwork(channelNetwork);
            }
            break;
        case Set:
	        {
	        	PipeType pipeType = channel.spec.type.toPipeType();
	        	PipeNetworks.removeWormhole(this, pipeType); // Note: this will check for validity of the underlying channel.
	        	m_cache.setChannel(channel.spec);
	        	PipeNetworks.addWormhole(this, pipeType); // Note: this will check for validity of the underlying channel.
	        	setChanged();
	        }
            break;
        case Unset:
	        {
	        	PipeType pipeType = channel.spec.type.toPipeType();
	        	PipeNetworks.removeWormhole(this, pipeType); // Note: this will check for validity of the underlying channel.
	        	m_cache.clearChannel(channel.spec.type);
	        	setChanged();
	        }
            break;
        }
    }
    
    public List<PipeChannelNetwork> getAllChannelNetworks()
    {
    	List<PipeChannelNetwork> networks = new ArrayList<PipeChannelNetwork>(ChannelType.NUM_TYPES);
    	for (ChannelType type : ChannelType.TYPES)
    	{
    		PipeChannelNetwork network = getChannelNetwork(type);
    		if (network != null)
    			networks.add(network);
    	}
    	return networks;
    }
    
    public PipeChannelNetwork getChannelNetwork(ChannelType type)
    {
    	PipeChannelNetwork network = BaseChannelManager.getManager().get(m_cache.getChannel(type));
    	// A bit of a weird hack here, but if we ever try to get a network
    	// and the channel disappeared out from underneath, then it probably was deleted
    	// via another wormhole, so update the cache accordingly. It's probably more
    	// efficient to do it here than in the tick function since we can hook into
    	// code that will already be running elsewhere.
    	// TODO: Ideally this happens via a callback that comes from the channel manager.
    	// this would be the most performant option.
    	if (network == null)
    		m_cache.clearChannel(type);
    	return network;
    }

    public static String getRegistryName()
    {
        return "wormhole";
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WormholeTileEntity tileEntity)
    {
    	tileEntity.serverTick(level);
    }
    
    public void serverTick(Level level)
    {
    	if (m_init && !m_isRemoving)
    	{
    		PipeNetworks.addWormhole(this);
    		m_init = false;
    	}
    }

    /* TODO: Create an item and save NBT to the item itself, so as to retain
     * the actual channel settings.
     */
    /*public void readFromItem(ItemStack stack)
    {
    	if (stack.hasTag())
    		getCache().read(stack.getTag().getCompound(TankItem.NBT_TAG));
    }

    public ItemStack writeToItem()
    {
        CompoundTag nbt = new CompoundTag();
        ItemStack stack = new ItemStack(SwiftBlocks.s_tankBlock.get(), 1);
        getCache().write(nbt);
        stack.setTag(new CompoundTag());
        stack.getTag().put(TankItem.NBT_TAG, nbt);
        return stack;
    }

    public static final String NBT_TAG = SwiftUtils.tagName("wormhole");
    */

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.read(provider, nbt);
        m_cache.read(nbt);
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.write(provider, nbt);
        m_cache.write(nbt);
    }
    
    public void onRemove(Level world, BlockPos blockPos)
    {
    	m_isRemoving = true;
		PipeNetworks.removeWormhole(this);
    }

	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity)
	{
        return WormholeContainer.createContainerServerSide(windowID, m_cache, this::onChannelChanged);
	}

	@Override
	public Component getDisplayName()
	{
        return Component.translatable(DISPLAY_NAME);
	}
	
	private ChannelConfigurationDataCache m_cache;
	private boolean m_init;
	private boolean m_isRemoving;
	
    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
