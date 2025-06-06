package swiftmod.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class Raytracer
{
	public static class Result
	{
		public Result()
		{
			index = Optional.empty();
			direction = null;
			distance = Double.MAX_VALUE;
			pos = null;
		}
		
		public boolean hasHit()
		{
			return index.isPresent();
		}
		
		public Optional<Integer> index;
		public Direction direction;
		public double distance;
		public BlockPos pos;
	}
	
    public static Result raytrace(IndexedVoxelShape[] shapes, Player player, BlockPos pos)
    {
        Vec3 start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vec3 end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static Result raytrace(List<IndexedVoxelShape> shapes, Player player, BlockPos pos)
    {
    	Vec3 start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vec3 end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static int raytrace(VoxelShape[] shapes, Player player, BlockPos pos)
    {
    	Vec3 start = player.getEyePosition(1.0f);
        double raycastLength = start.distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) * 2;
        Vec3 end = start.add(player.getViewVector(1.0f).scale(raycastLength));
        return raytrace(shapes, start, end, pos);
    }

    public static Result raytrace(IndexedVoxelShape[] shapes, Vec3 start, Vec3 end, BlockPos pos)
    {
    	Result r = new Result();
        for (int i = 0; i < shapes.length; ++i)
        {
            BlockHitResult hit = shapes[i].shape.clip(start, end, pos);
            if (hit != null)
            {
                double hitDistance = distanceBetweenSqr(hit, start);
                if (hitDistance < r.distance)
                {
                    r.distance = hitDistance;
                    r.index = Optional.of(shapes[i].index);
                    r.direction = hit.getDirection();
                    r.pos = hit.getBlockPos();
                }
            }
        }

        return r;
    }

    public static Result raytrace(List<IndexedVoxelShape> shapes, Vec3 start, Vec3 end, BlockPos pos)
    {
    	Result r = new Result();
        for (int i = 0; i < shapes.size(); ++i)
        {
            BlockHitResult hit = shapes.get(i).shape.clip(start, end, pos);
            if (hit != null)
            {
                double hitDistance = distanceBetweenSqr(hit, start);
                if (hitDistance < r.distance)
                {
                    r.distance = hitDistance;
                    r.index = Optional.of(shapes.get(i).index);
                    r.direction = hit.getDirection();
                    r.pos = hit.getBlockPos();
                }
            }
        }

        return r;
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
