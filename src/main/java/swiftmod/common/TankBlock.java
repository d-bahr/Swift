package swiftmod.common;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class TankBlock extends BaseEntityBlock
{
    public TankBlock()
    {
        super(PROPERTIES);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TankTileEntity(pos, state);
    }

    // render using a BakedModel (mbe01_block_simple.json --> mbe01_block_simple_model.json)
    // not strictly required because the default (super method) is MODEL.
    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult)
    {
        if (world.isClientSide)
            return InteractionResult.SUCCESS; // on client side, don't do anything

        if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, traceResult.getDirection()))
        {
            return InteractionResult.SUCCESS;
        }
        else
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null && blockEntity instanceof TankTileEntity)
            {
                FluidStack stack = ((TankTileEntity) blockEntity).getCache().getFluid();
                if (stack != null && !stack.isEmpty())
                {
                    String fluidName = Integer.toString(stack.getAmount()) + " mb "
                            + stack.getDisplayName().getString();
                    player.sendMessage(new TextComponent(fluidName), Util.NIL_UUID);
                    return InteractionResult.SUCCESS;
                }
            }

            player.sendMessage(new TextComponent("Empty"), Util.NIL_UUID);
            return InteractionResult.SUCCESS;
        }
    }
    
    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack stack)
    {
    	TankTileEntity blockEntity = (TankTileEntity) tile;

        float xOffset = world.random.nextFloat() * 0.8F + 0.1F;
        float yOffset = world.random.nextFloat() * 0.8F + 0.1F;
        float zOffset = world.random.nextFloat() * 0.8F + 0.1F;

        ItemStack droppedStack = blockEntity.writeToItem();

        ItemEntity entityitem = new ItemEntity(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, droppedStack);
        world.addFreshEntity(entityitem);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        super.setPlacedBy(world, pos, state, entity, stack);
        TankTileEntity blockEntity = (TankTileEntity) world.getBlockEntity(pos);
        blockEntity.readFromItem(stack);
        world.sendBlockUpdated(pos, state, state, 3);
    }

    private static final Properties PROPERTIES = Block.Properties.of(Material.STONE).strength(0.5f, 0.5f);
}
