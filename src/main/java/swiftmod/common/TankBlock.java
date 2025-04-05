package swiftmod.common;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class TankBlock extends AbstractBlock
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

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult)
    {
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS; // on client side, don't do anything

        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, traceResult.getDirection()))
            return ItemInteractionResult.SUCCESS;
        else
        	return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level world, BlockPos pos, Player player, BlockHitResult traceResult)
    {
        if (world.isClientSide)
            return InteractionResult.SUCCESS; // on client side, don't do anything

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TankTileEntity)
        {
            FluidStack stack = ((TankTileEntity) blockEntity).getCache().getFluid();
            if (stack != null && !stack.isEmpty())
            {
                String fluidName = Integer.toString(stack.getAmount()) + " mb "
                        + stack.getHoverName().getString();
                player.sendSystemMessage(Component.literal(fluidName));
                return InteractionResult.SUCCESS;
            }
        }

        player.sendSystemMessage(Component.literal("Empty"));
        return InteractionResult.SUCCESS;
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

    private static final Properties PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 0.5F);
}
