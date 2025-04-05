package swiftmod.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import swiftmod.common.SwiftTileEntities;

public class AdvancedEnergyPipeBlock extends EnergyPipeBlock
{
    public AdvancedEnergyPipeBlock()
    {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new AdvancedEnergyPipeTileEntity(pos, state);
    }

	@Override
	protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, SwiftTileEntities.s_advancedEnergyPipeTileEntityType.get(), AdvancedEnergyPipeTileEntity::serverTick);
	}
}
