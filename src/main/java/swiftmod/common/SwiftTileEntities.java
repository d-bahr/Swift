package swiftmod.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import swiftmod.pipes.*;

public class SwiftTileEntities
{
    public static void registerTileEntities(IEventBus bus)
    {
    	s_basicItemPipeTileEntityType = s_tileEntities.register(BasicItemPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicItemPipeTileEntity::new, SwiftBlocks.s_basicItemPipeBlock.get()));
    	s_advancedItemPipeTileEntityType = s_tileEntities.register(AdvancedItemPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedItemPipeTileEntity::new, SwiftBlocks.s_advancedItemPipeBlock.get()));
    	s_basicFluidPipeTileEntityType = s_tileEntities.register(BasicFluidPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicFluidPipeTileEntity::new, SwiftBlocks.s_basicFluidPipeBlock.get()));
    	s_advancedFluidPipeTileEntityType = s_tileEntities.register(AdvancedFluidPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedFluidPipeTileEntity::new, SwiftBlocks.s_advancedFluidPipeBlock.get()));
    	s_basicEnergyPipeTileEntityType = s_tileEntities.register(BasicEnergyPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicEnergyPipeTileEntity::new, SwiftBlocks.s_basicEnergyPipeBlock.get()));
    	s_advancedEnergyPipeTileEntityType = s_tileEntities.register(AdvancedEnergyPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedEnergyPipeTileEntity::new, SwiftBlocks.s_advancedEnergyPipeBlock.get()));
    	s_basicOmniPipeTileEntityType = s_tileEntities.register(BasicOmniPipeTileEntity.getRegistryName(), () -> createTileEntityType(BasicOmniPipeTileEntity::new, SwiftBlocks.s_basicOmniPipeBlock.get()));
    	s_advancedOmniPipeTileEntityType = s_tileEntities.register(AdvancedOmniPipeTileEntity.getRegistryName(), () -> createTileEntityType(AdvancedOmniPipeTileEntity::new, SwiftBlocks.s_advancedOmniPipeBlock.get()));
    	s_tankTileEntityType = s_tileEntities.register(TankTileEntity.getRegistryName(), () -> createTileEntityType(TankTileEntity::new, SwiftBlocks.s_tankBlock.get()));
    	s_wormholeTileEntityType = s_tileEntities.register(WormholeTileEntity.getRegistryName(), () -> createTileEntityType(WormholeTileEntity::new, SwiftBlocks.s_wormholeBlock.get()));
    	
        s_tileEntities.register(bus);
    }

    private static <T extends BlockEntity> BlockEntityType<T> createTileEntityType(BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks)
    {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
    
    private static DeferredRegister<BlockEntityType<?>> s_tileEntities = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Swift.MOD_NAME);

    public static Supplier<BlockEntityType<BasicItemPipeTileEntity>> s_basicItemPipeTileEntityType;
    public static Supplier<BlockEntityType<AdvancedItemPipeTileEntity>> s_advancedItemPipeTileEntityType;
    public static Supplier<BlockEntityType<BasicFluidPipeTileEntity>> s_basicFluidPipeTileEntityType;
    public static Supplier<BlockEntityType<AdvancedFluidPipeTileEntity>> s_advancedFluidPipeTileEntityType;
    public static Supplier<BlockEntityType<BasicEnergyPipeTileEntity>> s_basicEnergyPipeTileEntityType;
    public static Supplier<BlockEntityType<AdvancedEnergyPipeTileEntity>> s_advancedEnergyPipeTileEntityType;
    public static Supplier<BlockEntityType<BasicOmniPipeTileEntity>> s_basicOmniPipeTileEntityType;
    public static Supplier<BlockEntityType<AdvancedOmniPipeTileEntity>> s_advancedOmniPipeTileEntityType;
    public static Supplier<BlockEntityType<TankTileEntity>> s_tankTileEntityType;
    public static Supplier<BlockEntityType<WormholeTileEntity>> s_wormholeTileEntityType;
}
