package swiftmod.common;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

@SuppressWarnings("deprecation")
public class TankBlock extends Block implements ITileEntityProvider
{
    public TankBlock()
    {
        super(PROPERTIES);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return newBlockEntity(world);
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return new TankTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    // render using a BakedModel (mbe01_block_simple.json --> mbe01_block_simple_model.json)
    // not strictly required because the default (super method) is MODEL.
    @Override
    public BlockRenderType getRenderShape(BlockState blockState)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult)
    {
        if (world.isClientSide)
            return ActionResultType.SUCCESS; // on client side, don't do anything

        if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, traceResult.getDirection()))
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null && tileEntity instanceof TankTileEntity)
            {
                FluidStack stack = ((TankTileEntity) tileEntity).getCache().getFluid();
                if (stack != null && !stack.isEmpty())
                {
                    String fluidName = Integer.toString(stack.getAmount()) + " mb "
                            + stack.getDisplayName().getString();
                    player.sendMessage(new StringTextComponent(fluidName), Util.NIL_UUID);
                    return ActionResultType.SUCCESS;
                }
            }

            player.sendMessage(new StringTextComponent("Empty"), Util.NIL_UUID);
            return ActionResultType.SUCCESS;
        }
    }
    
    @Override
    public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tile, ItemStack stack)
    {
    	TankTileEntity tileEntity = (TankTileEntity) tile;

        float xOffset = world.random.nextFloat() * 0.8F + 0.1F;
        float yOffset = world.random.nextFloat() * 0.8F + 0.1F;
        float zOffset = world.random.nextFloat() * 0.8F + 0.1F;

        ItemStack droppedStack = tileEntity.writeToItem();

        ItemEntity entityitem = new ItemEntity(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, droppedStack);
        world.addFreshEntity(entityitem);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        super.setPlacedBy(world, pos, state, entity, stack);
        TankTileEntity tileEntity = (TankTileEntity) world.getBlockEntity(pos);
        tileEntity.readFromItem(stack);
        world.sendBlockUpdated(pos, state, state, 3);
    }

    private static final Properties PROPERTIES = Block.Properties.of(Material.STONE).strength(0.5f, 0.5f);
}
