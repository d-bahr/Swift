package swiftmod.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import swiftmod.pipes.*;

public class SwiftTileEntities
{
    public static void registerTileEntities(final RegistryEvent.Register<BlockEntityType<?>> event)
    {
        IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

        s_basicItemPipeTileEntityType = createTileEntityType(
                BasicItemPipeTileEntity.getRegistryName(), BasicItemPipeTileEntity::new, SwiftBlocks.s_basicItemPipeBlock);
        registry.register(s_basicItemPipeTileEntityType);

        s_advancedItemPipeTileEntityType = createTileEntityType(
                AdvancedItemPipeTileEntity.getRegistryName(), AdvancedItemPipeTileEntity::new, SwiftBlocks.s_advancedItemPipeBlock);
        registry.register(s_advancedItemPipeTileEntityType);

        s_ultimateItemPipeTileEntityType = createTileEntityType(
                UltimateItemPipeTileEntity.getRegistryName(), UltimateItemPipeTileEntity::new, SwiftBlocks.s_ultimateItemPipeBlock);
        registry.register(s_ultimateItemPipeTileEntityType);

        s_basicFluidPipeTileEntityType = createTileEntityType(
                BasicFluidPipeTileEntity.getRegistryName(), BasicFluidPipeTileEntity::new, SwiftBlocks.s_basicFluidPipeBlock);
        registry.register(s_basicFluidPipeTileEntityType);

        s_advancedFluidPipeTileEntityType = createTileEntityType(
                AdvancedFluidPipeTileEntity.getRegistryName(), AdvancedFluidPipeTileEntity::new, SwiftBlocks.s_advancedFluidPipeBlock);
        registry.register(s_advancedFluidPipeTileEntityType);

        s_ultimateFluidPipeTileEntityType = createTileEntityType(
                UltimateFluidPipeTileEntity.getRegistryName(), UltimateFluidPipeTileEntity::new, SwiftBlocks.s_ultimateFluidPipeBlock);
        registry.register(s_ultimateFluidPipeTileEntityType);

        s_tankTileEntityType = createTileEntityType(
                TankTileEntity.getRegistryName(), TankTileEntity::new, SwiftBlocks.s_tankBlock);
        registry.register(s_tankTileEntityType);
    }

    private static <T extends BlockEntity> BlockEntityType<T> createTileEntityType(String registryName, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks)
    {
        BlockEntityType<T> type = BlockEntityType.Builder.of(factory, blocks).build(null);
        type.setRegistryName(Swift.MOD_NAME, registryName);
        return type;
    }

    public static BlockEntityType<BasicItemPipeTileEntity> s_basicItemPipeTileEntityType;
    public static BlockEntityType<AdvancedItemPipeTileEntity> s_advancedItemPipeTileEntityType;
    public static BlockEntityType<UltimateItemPipeTileEntity> s_ultimateItemPipeTileEntityType;
    public static BlockEntityType<BasicFluidPipeTileEntity> s_basicFluidPipeTileEntityType;
    public static BlockEntityType<AdvancedFluidPipeTileEntity> s_advancedFluidPipeTileEntityType;
    public static BlockEntityType<UltimateFluidPipeTileEntity> s_ultimateFluidPipeTileEntityType;
    public static BlockEntityType<TankTileEntity> s_tankTileEntityType;
}
