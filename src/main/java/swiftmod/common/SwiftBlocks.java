package swiftmod.common;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import swiftmod.pipes.*;

public class SwiftBlocks
{
    public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        s_basicItemPipeBlock = createBlock("basic_item_pipe", new BasicItemPipeBlock());
        registry.register(s_basicItemPipeBlock);

        s_advancedItemPipeBlock = createBlock("advanced_item_pipe", new AdvancedItemPipeBlock());
        registry.register(s_advancedItemPipeBlock);

        s_ultimateItemPipeBlock = createBlock("ultimate_item_pipe", new UltimateItemPipeBlock());
        registry.register(s_ultimateItemPipeBlock);

        s_basicFluidPipeBlock = createBlock("basic_fluid_pipe", new BasicFluidPipeBlock());
        registry.register(s_basicFluidPipeBlock);

        s_advancedFluidPipeBlock = createBlock("advanced_fluid_pipe", new AdvancedFluidPipeBlock());
        registry.register(s_advancedFluidPipeBlock);

        s_ultimateFluidPipeBlock = createBlock("ultimate_fluid_pipe", new UltimateFluidPipeBlock());
        registry.register(s_ultimateFluidPipeBlock);

        s_tankBlock = createBlock("tank", new TankBlock());
        registry.register(s_tankBlock);
    }

    public static void registerRenderTypes()
    {
        RenderTypeLookup.setRenderLayer(s_basicItemPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_advancedItemPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_ultimateItemPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_basicFluidPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_advancedFluidPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_ultimateFluidPipeBlock, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(s_tankBlock, RenderType.solid());
    }

    private static <T extends Block> T createBlock(String registryName, T t)
    {
        t.setRegistryName(Swift.MOD_NAME, registryName);
        return t;
    }

    public static BasicItemPipeBlock s_basicItemPipeBlock;
    public static AdvancedItemPipeBlock s_advancedItemPipeBlock;
    public static UltimateItemPipeBlock s_ultimateItemPipeBlock;
    public static BasicFluidPipeBlock s_basicFluidPipeBlock;
    public static AdvancedFluidPipeBlock s_advancedFluidPipeBlock;
    public static UltimateFluidPipeBlock s_ultimateFluidPipeBlock;
    public static TankBlock s_tankBlock;
}
