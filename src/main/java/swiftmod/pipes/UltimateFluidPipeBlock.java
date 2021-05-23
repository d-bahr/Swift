package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class UltimateFluidPipeBlock extends FluidPipeBlock
{
    public UltimateFluidPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new UltimateFluidPipeTileEntity();
    }
}
