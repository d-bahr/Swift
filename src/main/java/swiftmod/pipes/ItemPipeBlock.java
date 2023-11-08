package swiftmod.pipes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
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
            PipeTileEntity<?, ?, ?> blockEntity, Direction startingDir)
    {
        if (blockEntity instanceof ItemPipeTileEntity)
        {
            ItemPipeTileEntity itemPipeTE = (ItemPipeTileEntity) blockEntity;
            NetworkHooks.openScreen((ServerPlayer) player, menuProvider, (FriendlyByteBuf) ->
            {
                itemPipeTE.serializeBufferForContainer(FriendlyByteBuf, player, startingDir);
            });
        }
    }

    public boolean canConnect(BlockEntity blockEntity, Direction direction)
    {
        return canConnectTo(blockEntity, direction);
    }

    public static boolean canConnectTo(BlockEntity blockEntity, Direction direction)
    {
        if (blockEntity != null)
            return SwiftUtils.isItemHandler(blockEntity, direction);
        return false;
    }
}
