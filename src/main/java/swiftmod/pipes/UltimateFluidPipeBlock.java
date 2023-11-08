package swiftmod.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import swiftmod.common.SwiftTileEntities;

public class UltimateFluidPipeBlock extends FluidPipeBlock
{
    public UltimateFluidPipeBlock()
    {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new UltimateFluidPipeTileEntity(pos, state);
    }

	@Override
	protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, SwiftTileEntities.s_ultimateFluidPipeTileEntityType.get(), UltimateFluidPipeTileEntity::serverTick);
	}
}
