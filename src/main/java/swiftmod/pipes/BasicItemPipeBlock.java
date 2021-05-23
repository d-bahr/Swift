package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BasicItemPipeBlock extends ItemPipeBlock
{
    public BasicItemPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new BasicItemPipeTileEntity();
    }
}
