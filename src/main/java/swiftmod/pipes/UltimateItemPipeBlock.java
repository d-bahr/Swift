package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class UltimateItemPipeBlock extends ItemPipeBlock
{
    public UltimateItemPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new UltimateItemPipeTileEntity();
    }
}
