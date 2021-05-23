package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BasicFluidPipeBlock extends FluidPipeBlock
{
    public BasicFluidPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new BasicFluidPipeTileEntity();
    }
}
