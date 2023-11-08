package swiftmod.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import swiftmod.pipes.*;

public class SwiftTileEntities
{
    public static void registerTileEntities()
    {
    	s_basicItemPipeTileEntityType = s_tileEntities.register(BasicItemPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicItemPipeTileEntity::new, SwiftBlocks.s_basicItemPipeBlock.get()));
    	s_advancedItemPipeTileEntityType = s_tileEntities.register(AdvancedItemPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedItemPipeTileEntity::new, SwiftBlocks.s_advancedItemPipeBlock.get()));
    	s_ultimateItemPipeTileEntityType = s_tileEntities.register(UltimateItemPipeTileEntity.getRegistryName(), () -> createTileEntityType(UltimateItemPipeTileEntity::new, SwiftBlocks.s_ultimateItemPipeBlock.get()));
    	s_basicFluidPipeTileEntityType = s_tileEntities.register(BasicFluidPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicFluidPipeTileEntity::new, SwiftBlocks.s_basicFluidPipeBlock.get()));
    	s_advancedFluidPipeTileEntityType = s_tileEntities.register(AdvancedFluidPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedFluidPipeTileEntity::new, SwiftBlocks.s_advancedFluidPipeBlock.get()));
    	s_ultimateFluidPipeTileEntityType = s_tileEntities.register(UltimateFluidPipeTileEntity.getRegistryName(), () -> createTileEntityType(UltimateFluidPipeTileEntity::new, SwiftBlocks.s_ultimateFluidPipeBlock.get()));
    	s_tankTileEntityType = s_tileEntities.register(TankTileEntity.getRegistryName(), () -> createTileEntityType(TankTileEntity::new, SwiftBlocks.s_tankBlock.get()));

        s_tileEntities.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static <T extends BlockEntity> BlockEntityType<T> createTileEntityType(BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks)
    {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
    
    private static DeferredRegister<BlockEntityType<?>> s_tileEntities = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Swift.MOD_NAME);

    public static RegistryObject<BlockEntityType<BasicItemPipeTileEntity>> s_basicItemPipeTileEntityType;
    public static RegistryObject<BlockEntityType<AdvancedItemPipeTileEntity>> s_advancedItemPipeTileEntityType;
    public static RegistryObject<BlockEntityType<UltimateItemPipeTileEntity>> s_ultimateItemPipeTileEntityType;
    public static RegistryObject<BlockEntityType<BasicFluidPipeTileEntity>> s_basicFluidPipeTileEntityType;
    public static RegistryObject<BlockEntityType<AdvancedFluidPipeTileEntity>> s_advancedFluidPipeTileEntityType;
    public static RegistryObject<BlockEntityType<UltimateFluidPipeTileEntity>> s_ultimateFluidPipeTileEntityType;
    public static RegistryObject<BlockEntityType<TankTileEntity>> s_tankTileEntityType;
}
