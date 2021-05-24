package swiftmod.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

public class Raytracer
{
    public static Optional<Integer> raytrace(IndexedVoxelShape[] shapes, PlayerEntity player, BlockPos pos)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Vector3d start = player.getEyePosition(1.0f);
        Vector3d end = start.add(player.getViewVector(1.0f).scale(minecraft.gameMode.getPickRange() * 2));
        return raytrace(shapes, start, end, pos);
    }

    public static Optional<Integer> raytrace(List<IndexedVoxelShape> shapes, PlayerEntity player, BlockPos pos)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Vector3d start = player.getEyePosition(1.0f);
        Vector3d end = start.add(player.getViewVector(1.0f).scale(minecraft.gameMode.getPickRange() * 2));
        return raytrace(shapes, start, end, pos);
    }

    public static int raytrace(VoxelShape[] shapes, PlayerEntity player, BlockPos pos)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Vector3d start = player.getEyePosition(1.0f);
        Vector3d end = start.add(player.getViewVector(1.0f).scale(minecraft.gameMode.getPickRange() * 2));
        return raytrace(shapes, start, end, pos);
    }

    public static Optional<Integer> raytrace(IndexedVoxelShape[] shapes, Vector3d start, Vector3d end, BlockPos pos)
    {
        Optional<Integer> index = Optional.empty();
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.length; ++i)
        {
            BlockRayTraceResult hit = shapes[i].shape.clip(start, end, pos);
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

    public static Optional<Integer> raytrace(List<IndexedVoxelShape> shapes, Vector3d start, Vector3d end, BlockPos pos)
    {
        Optional<Integer> index = Optional.empty();
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.size(); ++i)
        {
            BlockRayTraceResult hit = shapes.get(i).shape.clip(start, end, pos);
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

    public static int raytrace(VoxelShape[] shapes, Vector3d start, Vector3d end, BlockPos pos)
    {
        int index = -1;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < shapes.length; ++i)
        {
            BlockRayTraceResult hit = shapes[i].clip(start, end, pos);
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

    public static double distanceBetween(BlockRayTraceResult raytrace, Vector3d position)
    {
        Vector3d location = raytrace.getLocation();
        return position.distanceTo(location);
    }

    public static double distanceBetweenSqr(BlockRayTraceResult raytrace, Vector3d position)
    {
        Vector3d location = raytrace.getLocation();
        return position.distanceToSqr(location);
    }
}
