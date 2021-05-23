package swiftmod.common.channels;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChannelAttachment
{
    public ChannelAttachment()
    {
        world = null;
        pos = null;
    }

    public ChannelAttachment(World w, BlockPos p)
    {
        world = w;
        pos = p;
    }

    public TileEntity getTileEntity()
    {
        return world.getBlockEntity(pos);
    }

    public boolean isRedstonePowered()
    {
        return world.hasNeighborSignal(pos);
    }

    public TileEntity getNeighbor(Direction dir)
    {
        return world.getBlockEntity(pos.relative(dir));
    }

    @Override
    public int hashCode()
    {
        RegistryKey<World> key = world.dimension();
        return key.getRegistryName().hashCode() ^ key.location().hashCode() ^ pos.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != ChannelAttachment.class))
            return false;
        ChannelAttachment other = (ChannelAttachment)obj;
        return world.dimension().compareTo(other.world.dimension()) == 0 && pos.equals(other.pos);
    }

    public World world;
    public BlockPos pos;
}
