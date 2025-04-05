package swiftmod.pipes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import swiftmod.common.SwiftUtils;

public abstract class ItemPipeBlock extends PipeBlock
{
    public ItemPipeBlock()
    {
        super();
    }

    @Override
    protected void openScreen(ServerPlayer player, MenuProvider menuProvider,
            PipeTileEntity blockEntity, Direction startingDir)
    {
        if (blockEntity instanceof ItemPipeTileEntity)
        {
            ItemPipeTileEntity itemPipeTE = (ItemPipeTileEntity) blockEntity;
            player.openMenu(menuProvider, (FriendlyByteBuf) ->
            {
                itemPipeTE.serializeBufferForContainer(FriendlyByteBuf, player, startingDir);
            });
        }
    }

    public boolean canConnect(Level level, BlockPos pos, Direction direction)
    {
        return canConnectTo(level, pos, direction);
    }

    public static boolean canConnectTo(Level level, BlockPos pos, Direction direction)
    {
    	return SwiftUtils.isItemHandler(level, pos, direction);
    }

    public static boolean isConnectableNeighbor(Level level, BlockPos pos, Direction direction)
    {
    	BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof WormholeTileEntity || SwiftUtils.isItemHandler(level, pos, direction);
    }
    
    @Override
    protected boolean matchesPipe(BlockEntity blockEntity)
    {
    	if (blockEntity instanceof PipeTileEntity)
    		return ((PipeTileEntity)blockEntity).isPipeType(PipeType.Item);
    	else
    		return blockEntity instanceof WormholeTileEntity && PipeType.Item.canConvertToChannel();
    }
}
