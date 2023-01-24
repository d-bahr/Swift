package swiftmod.common;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.common.world.ForgeChunkManager.TicketHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Swift.MOD_NAME)
public class Swift
{
    public static final String MOD_NAME = "swift";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final CustomItemGroup ITEM_GROUP = new CustomItemGroup(Swift.MOD_NAME,
            () -> SwiftItems.s_advancedItemPipeBlockItem);

    private static long s_lastScrollTime = -1;
    private static double s_scrollDelta = 0.0;

    public Swift()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientInit);
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerTick);

        ForgeChunkManager.setForcedChunkLoadingCallback(MOD_NAME, this::onForgeChunksLoaded);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        SwiftNetwork.registerPackets();
    }

    private void doClientInit(final FMLClientSetupEvent event)
    {
        SwiftBlocks.registerRenderTypes();
        SwiftContainers.registerScreenTypes();
        SwiftNetwork.registerPackets();
    }

    private void onForgeChunksLoaded(ServerLevel world, TicketHelper ticketHelper)
    {
        Map<BlockPos, Pair<LongSet, LongSet>> blockTickets = ticketHelper.getBlockTickets();
        for (BlockPos pos : blockTickets.keySet())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof IChunkLoadable)
            {
                if (!((IChunkLoadable) blockEntity).isChunkLoaded())
                    ticketHelper.removeAllTickets(pos);
            }
        }
    }
    
    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null)
        {
            if (minecraft.level.getGameTime() - s_lastScrollTime > 20)
                s_scrollDelta = 0.0;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onMouseEvent(MouseScrollEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.isShiftKeyDown())
        {
            double scroll = event.getScrollDelta();

            s_lastScrollTime = minecraft.level.getGameTime();
            s_scrollDelta += scroll;
            int s = (int) s_scrollDelta;
            s_scrollDelta %= 1;

            if (s != 0)
            {
                ItemStack mainHandItem = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!mainHandItem.isEmpty() && mainHandItem.getItem() == SwiftItems.s_copyPastaItem)
                {
                    if (s > 0)
                    {
                        CopyPastaItem.incrementCopyType(mainHandItem);
                        CopyPastaItem.messageCurrentType(minecraft.player);
                    }
                    else
                    {
                        CopyPastaItem.decrementCopyType(mainHandItem);
                        CopyPastaItem.messageCurrentType(minecraft.player);
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    /*
     * private void onServerTick(TickEvent.ServerTickEvent event) { }
     */

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

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is
    // subscribing to the MOD Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
        {
            SwiftBlocks.registerBlocks(event);
        }

        @SubscribeEvent
        public static void onTileEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> event)
        {
            SwiftTileEntities.registerTileEntities(event);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
        {
            SwiftItems.registerItems(event);
        }

        @SubscribeEvent
        public static void onContainersRegistry(final RegistryEvent.Register<MenuType<?>> event)
        {
            SwiftContainers.registerContainers(event);
        }
    }
}
