package swiftmod.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class AdvancedItemPipeBlock extends ItemPipeBlock
{
    public AdvancedItemPipeBlock()
    {
        super();
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new AdvancedItemPipeTileEntity();
    }
}
