package swiftmod.pipes;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.NetworkHooks;
import swiftmod.common.SwiftUtils;

public abstract class FluidPipeBlock extends PipeBlock
{
    public FluidPipeBlock()
    {
        super();
    }

    protected void openGui(ServerPlayerEntity player, INamedContainerProvider namedContainerProvider, PipeTileEntity<?,?,?> tileEntity)
    {
        if (tileEntity instanceof FluidPipeTileEntity)
        {
            FluidPipeTileEntity fluidPipeTE = (FluidPipeTileEntity) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, namedContainerProvider, (packetBuffer) ->
            {
                fluidPipeTE.serializeBufferForContainer(packetBuffer, player);
            });
        }
    }

    public boolean canConnect(TileEntity tileEntity, Direction direction)
    {
        return canConnectTo(tileEntity, direction);
    }

    public static boolean canConnectTo(TileEntity tileEntity, Direction direction)
    {
        if (tileEntity != null)
            return SwiftUtils.isFluidHandler(tileEntity, direction);
        return false;
    }
}
