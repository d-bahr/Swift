package swiftmod.common.channels;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ChannelAttachment
{
    public ChannelAttachment()
    {
        world = null;
        pos = null;
    }

    public ChannelAttachment(Level w, BlockPos p)
    {
        world = w;
        pos = p;
    }

    public BlockEntity getTileEntity()
    {
        return world.getBlockEntity(pos);
    }

    public boolean isRedstonePowered()
    {
        return world.hasNeighborSignal(pos);
    }

    public BlockEntity getNeighbor(Direction dir)
    {
        return world.getBlockEntity(pos.relative(dir));
    }

    @Override
    public int hashCode()
    {
        ResourceKey<Level> key = world.dimension();
        return key.registry().hashCode() ^ key.location().hashCode() ^ pos.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != ChannelAttachment.class))
            return false;
        ChannelAttachment other = (ChannelAttachment)obj;
        return world.dimension().compareTo(other.world.dimension()) == 0 && pos.equals(other.pos);
    }

    public Level world;
    public BlockPos pos;
}
