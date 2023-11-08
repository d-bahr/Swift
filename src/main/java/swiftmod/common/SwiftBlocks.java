package swiftmod.common;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import swiftmod.pipes.*;

public class SwiftBlocks
{
    public static void registerBlocks()
    {
    	s_basicItemPipeBlock     = s_blocks.register("basic_item_pipe", () -> new BasicItemPipeBlock());
    	s_advancedItemPipeBlock  = s_blocks.register("advanced_item_pipe", () -> new AdvancedItemPipeBlock());
    	s_ultimateItemPipeBlock  = s_blocks.register("ultimate_item_pipe", () -> new UltimateItemPipeBlock());
    	s_basicFluidPipeBlock    = s_blocks.register("basic_fluid_pipe", () -> new BasicFluidPipeBlock());
    	s_advancedFluidPipeBlock = s_blocks.register("advanced_fluid_pipe", () -> new AdvancedFluidPipeBlock());
    	s_ultimateFluidPipeBlock = s_blocks.register("ultimate_fluid_pipe", () -> new UltimateFluidPipeBlock());
    	s_tankBlock              = s_blocks.register("tank", () -> new TankBlock());

    	s_blocks.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    private static DeferredRegister<Block> s_blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, Swift.MOD_NAME);

    public static RegistryObject<BasicItemPipeBlock> s_basicItemPipeBlock;
    public static RegistryObject<AdvancedItemPipeBlock> s_advancedItemPipeBlock;
    public static RegistryObject<UltimateItemPipeBlock> s_ultimateItemPipeBlock;
    public static RegistryObject<BasicFluidPipeBlock> s_basicFluidPipeBlock;
    public static RegistryObject<AdvancedFluidPipeBlock> s_advancedFluidPipeBlock;
    public static RegistryObject<UltimateFluidPipeBlock> s_ultimateFluidPipeBlock;
    public static RegistryObject<TankBlock> s_tankBlock;
}
