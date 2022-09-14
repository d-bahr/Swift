package swiftmod.common;

import net.minecraft.world.phys.shapes.VoxelShape;

public class IndexedVoxelShape
{
    public IndexedVoxelShape()
    {
        shape = null;
        index = -1;
    }

    public IndexedVoxelShape(VoxelShape voxelShape)
    {
        shape = voxelShape;
        index = -1;
    }

    public IndexedVoxelShape(VoxelShape voxelShape, int i)
    {
        shape = voxelShape;
        index = i;
    }

    public VoxelShape shape;
    public int index;
}
