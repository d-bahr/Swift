package swiftmod.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import swiftmod.common.AbstractBlock;
import swiftmod.common.SwiftTileEntities;

public class WormholeBlock extends AbstractBlock
{
    public WormholeBlock()
    {
        super(PROPERTIES);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new WormholeTileEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult traceResult)
    {
        if (world.isClientSide)
            return InteractionResult.SUCCESS; // on client side, don't do anything

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof WormholeTileEntity))
            return InteractionResult.PASS;
        
        MenuProvider menuProvider = getMenuProvider(state, world, pos);
        if (menuProvider != null)
        {
            if (!(player instanceof ServerPlayer))
                return InteractionResult.FAIL; // should always be true, but just in case...

            WormholeTileEntity wormholeEntity = (WormholeTileEntity)te;
            player.openMenu(menuProvider, (FriendlyByteBuf) ->
            {
            	WormholeContainer.encode(wormholeEntity.getCache(), player, FriendlyByteBuf);
            });
        }

        return InteractionResult.SUCCESS;
    }
    
    @Override
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
    	if (level.isClientSide)
    		return null;
    	else
    		return createTickerHelper(type, SwiftTileEntities.s_wormholeTileEntityType.get(), WormholeTileEntity::serverTick);
    }

    /* TODO: Create an item and save NBT to the item itself, so as to retain
     * the actual channel settings. See also WormholeTileEntity.
    /*@Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack stack)
    {
    	WormholeTileEntity blockEntity = (WormholeTileEntity) tile;

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
        WormholeTileEntity blockEntity = (WormholeTileEntity) world.getBlockEntity(pos);
        blockEntity.readFromItem(stack);
        world.sendBlockUpdated(pos, state, state, 3);
    }*/

    private static final Properties PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 0.5F);
}
