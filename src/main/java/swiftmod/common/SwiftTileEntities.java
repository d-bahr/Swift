package swiftmod.common;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import swiftmod.pipes.*;

public class SwiftTileEntities
{
    public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

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

    private static <T extends TileEntity> TileEntityType<T> createTileEntityType(String registryName, Supplier<T> factory, Block... blocks)
    {
        TileEntityType<T> type = TileEntityType.Builder.of(factory, blocks).build(null);
        type.setRegistryName(Swift.MOD_NAME, registryName);
        return type;
    }

    public static TileEntityType<BasicItemPipeTileEntity> s_basicItemPipeTileEntityType;
    public static TileEntityType<AdvancedItemPipeTileEntity> s_advancedItemPipeTileEntityType;
    public static TileEntityType<UltimateItemPipeTileEntity> s_ultimateItemPipeTileEntityType;
    public static TileEntityType<BasicFluidPipeTileEntity> s_basicFluidPipeTileEntityType;
    public static TileEntityType<AdvancedFluidPipeTileEntity> s_advancedFluidPipeTileEntityType;
    public static TileEntityType<UltimateFluidPipeTileEntity> s_ultimateFluidPipeTileEntityType;
    public static TileEntityType<TankTileEntity> s_tankTileEntityType;
}
