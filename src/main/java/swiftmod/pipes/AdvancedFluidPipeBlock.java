package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class AdvancedFluidPipeBlock extends FluidPipeBlock
{
    public AdvancedFluidPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new AdvancedFluidPipeTileEntity();
    }
}
