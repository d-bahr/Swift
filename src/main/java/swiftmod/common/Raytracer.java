package swiftmod.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;

public class Raytracer
{
    public static Optional<Integer> raytrace(IndexedVoxelShape[] shapes, Player player, BlockPos pos)
    {
        Vector3d start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vector3d end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static Optional<Integer> raytrace(List<IndexedVoxelShape> shapes, Player player, BlockPos pos)
    {
        Vector3d start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vector3d end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static int raytrace(VoxelShape[] shapes, Player player, BlockPos pos)
    {
        Vector3d start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vector3d end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static Optional<Integer> raytrace(IndexedVoxelShape[] shapes, Vec3 start, Vec3 end, BlockPos pos)
    {
        Optional<Integer> index = Optional.empty();
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.length; ++i)
        {
            BlockHitResult hit = shapes[i].shape.clip(start, end, pos);
            if (hit != null)
            {
                double hitDistance = distanceBetweenSqr(hit, start);
                if (hitDistance < dist)
                {
                    dist = hitDistance;
                    index = Optional.of(shapes[i].index);
                }
            }
        }

        return index;
    }

    public static Optional<Integer> raytrace(List<IndexedVoxelShape> shapes, Vec3 start, Vec3 end, BlockPos pos)
    {
        Optional<Integer> index = Optional.empty();
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.size(); ++i)
        {
            BlockHitResult hit = shapes.get(i).shape.clip(start, end, pos);
            if (hit != null)
            {
                double hitDistance = distanceBetweenSqr(hit, start);
                if (hitDistance < dist)
                {
                    dist = hitDistance;
                    index = Optional.of(shapes.get(i).index);
                }
            }
        }

        return index;
    }

    public static int raytrace(VoxelShape[] shapes, Vec3 start, Vec3 end, BlockPos pos)
    {
        int index = -1;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.length; ++i)
        {
            BlockHitResult hit = shapes[i].clip(start, end, pos);
            if (hit != null)
            {
                double hitDistance = distanceBetweenSqr(hit, start);
                if (hitDistance < dist)
                {
                    dist = hitDistance;
                    index = i;
                }
            }
        }

        return index;
    }

    public static double distanceBetween(BlockHitResult raytrace, Vec3 position)
    {
        Vec3 location = raytrace.getLocation();
        return position.distanceTo(location);
    }

    public static double distanceBetweenSqr(BlockHitResult raytrace, Vec3 position)
    {
        Vec3 location = raytrace.getLocation();
        return position.distanceToSqr(location);
    }
}
