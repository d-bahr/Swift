package swiftmod.pipes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import swiftmod.common.SwiftUtils;

public abstract class EnergyPipeBlock extends PipeBlock
{
    public EnergyPipeBlock()
    {
        super();
    }

    @Override
    protected void openScreen(ServerPlayer player, MenuProvider menuProvider,
            PipeTileEntity blockEntity, Direction startingDir)
    {
        if (blockEntity instanceof EnergyPipeTileEntity)
        {
        	EnergyPipeTileEntity fluidPipeTE = (EnergyPipeTileEntity) blockEntity;
        	player.openMenu(menuProvider, (FriendlyByteBuf) ->
            {
                fluidPipeTE.serializeBufferForContainer(FriendlyByteBuf, player, startingDir);
            });
        }
    }

    public boolean canConnect(Level level, BlockPos pos, Direction direction)
    {
        return canConnectTo(level, pos, direction);
    }

    public static boolean canConnectTo(Level level, BlockPos pos, Direction direction)
    {
    	return SwiftUtils.isEnergyHandler(level, pos, direction);
    }

    public static boolean isConnectableNeighbor(Level level, BlockPos pos, Direction direction)
    {
    	BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof WormholeTileEntity || SwiftUtils.isEnergyHandler(level, pos, direction);
    }
    
    @Override
    protected boolean matchesPipe(BlockEntity blockEntity)
    {
    	if (blockEntity instanceof PipeTileEntity)
    		return ((PipeTileEntity)blockEntity).isPipeType(PipeType.Energy);
    	else
    		return blockEntity instanceof WormholeTileEntity && PipeType.Energy.canConvertToChannel();
    }
}
