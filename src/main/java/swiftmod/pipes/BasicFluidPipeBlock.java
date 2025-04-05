package swiftmod.pipes;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import swiftmod.common.SwiftTileEntities;
import net.minecraft.core.BlockPos;

public class BasicFluidPipeBlock extends FluidPipeBlock
{
    public BasicFluidPipeBlock()
    {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BasicFluidPipeTileEntity(pos, state);
    }

	@Override
	protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, SwiftTileEntities.s_basicFluidPipeTileEntityType.get(), BasicFluidPipeTileEntity::serverTick);
	}
}
