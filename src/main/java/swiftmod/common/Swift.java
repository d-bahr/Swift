package swiftmod.common;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.common.world.chunk.TicketSet;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import swiftmod.pipes.PipeBlock;
import swiftmod.pipes.networks.PipeNetworks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Swift.MOD_NAME)
public class Swift
{
    public static final String MOD_NAME = "swift";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public Swift(IEventBus bus)
    {
        // Register the setup method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientInit);
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerTick);

    	// TODO: Chunk loading
        //ForcedChunkManager.setForcedChunkLoadingCallback(MOD_NAME, this::onForgeChunksLoaded);

    	SwiftDataComponents.registerDataComponents(bus);
        SwiftBlocks.registerBlocks(bus);
        SwiftItems.registerItems(bus);
        SwiftTileEntities.registerTileEntities(bus);
        SwiftContainers.registerContainers(bus);
        CustomItemGroup.registerCreativeTabs(bus);
        
        bus.addListener(RegisterPayloadHandlersEvent.class, SwiftNetwork::registerPackets);
        
        PipeNetworks.init();

        // Register ourselves for server and other game events we are interested in
        //bus.register(ClientBusSubscriber.class);
        //bus.register(CommonBusSubscriber.class);
    }

    @EventBusSubscriber(modid = Swift.MOD_NAME)
    private static class ServerBusSubscriber
    {
    	static int counter = 0;
    	
        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event)
        {
    		long startTime = System.nanoTime();
    		PipeNetworks.tickNetworks();
    		long endTime = System.nanoTime();
    		double duration = (endTime - startTime) / 1000000.0;
    		//if (counter % 100 == 0)
    		//	LOGGER.info("Duration: " + duration + " milliseconds");
    		counter++;
        }
    }

    @EventBusSubscriber(modid = Swift.MOD_NAME, bus = EventBusSubscriber.Bus.MOD)
    private static class CommonBusSubscriber
    {
        @SubscribeEvent
        private static void registerScreens(final RegisterMenuScreensEvent event)
        {
            SwiftContainers.registerScreenTypes(event);
        }
        
        @SubscribeEvent
        private static void registerCapabilities(final RegisterCapabilitiesEvent event)
        {
        	TankTileEntity.registerCapabilities(event);
        }
        
        /*@SubscribeEvent
        private static void registerPackets(final RegisterPayloadHandlersEvent event)
        {
            SwiftNetwork.registerPackets(event);
        }*/

        /*private void onForgeChunksLoaded(ServerLevel world, TicketHelper ticketHelper)
        {
            Map<BlockPos, TicketSet> blockTickets = ticketHelper.getBlockTickets();
            for (BlockPos pos : blockTickets.keySet())
            {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof IChunkLoadable)
                {
                    if (!((IChunkLoadable) blockEntity).isChunkLoaded())
                        ticketHelper.removeAllTickets(pos);
                }
            }
        }*/
        
        /*@SubscribeEvent
        public void onLevelUnload(LevelEvent.Unload event)
        {
        	PipeNetworks.clear();
        }*/
        
        /*@SubscribeEvent
        public void onLevelTick(LevelTickEvent event)
        {
        	if (event.side == LogicalSide.CLIENT)
        		return;
        	
        	if (event.level.is)
        }*/
        
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Swift.MOD_NAME, bus = EventBusSubscriber.Bus.MOD)
    private static class ClientBusSubscriber
    {
        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event)
        {
        	event.register(PipeBlock::tintPipeBlocks,
        			SwiftBlocks.s_basicItemPipeBlock.get(),
        			SwiftBlocks.s_advancedItemPipeBlock.get(),
        			SwiftBlocks.s_basicFluidPipeBlock.get(),
        			SwiftBlocks.s_advancedFluidPipeBlock.get(),
        			SwiftBlocks.s_basicEnergyPipeBlock.get(),
        			SwiftBlocks.s_advancedEnergyPipeBlock.get(),
        			SwiftBlocks.s_basicOmniPipeBlock.get(),
        			SwiftBlocks.s_advancedOmniPipeBlock.get());
        }
    }
    
    public static void doAssert(boolean test, String message)
    {
    	if (!test)
    		LOGGER.warn(message);
    }

    /*
     * private void enqueueIMC(final InterModEnqueueEvent event) { // some example code to dispatch IMC
     * to another mod InterModComms.sendTo("examplemod", "helloworld", () -> {
     * LOGGER.info("Hello world from the MDK"); return "Hello world";}); }
     */

    /*
     * private void processIMC(final InterModProcessEvent event) { // some example code to receive and
     * process InterModComms from other mods LOGGER.info("Got IMC {}", event.getIMCStream().
     * map(m->m.getMessageSupplier().get()). collect(Collectors.toList())); }
     */
}
