package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import swiftmod.common.CopyPastaItem;
import swiftmod.common.IndexedVoxelShape;
import swiftmod.common.Raytracer;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;

@SuppressWarnings("deprecation")
public abstract class PipeBlock extends BaseEntityBlock
{
    public PipeBlock()
    {
        super(PROPERTIES);

        BlockState defaultState = getStateDefinition().any().setValue(UP, false).setValue(DOWN, false)
                .setValue(EAST, false).setValue(WEST, false).setValue(NORTH, false).setValue(SOUTH, false);
        registerDefaultState(defaultState);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (world.isClientSide)
            return InteractionResult.SUCCESS; // on client side, don't do anything

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof PipeTileEntity)
        {
            PipeTileEntity<?, ?, ?> blockEntity = (PipeTileEntity<?, ?, ?>) te;
            Direction dir = null;

            int index = BlockStateToShapeIndex(state);
            Optional<Integer> r = Raytracer.raytrace(INDEXED_SHAPES[index], player, pos);

            if (r.isPresent())
            {
                int i = r.get();
                if (i >= 0)
                    dir = SwiftUtils.indexToDir(i);
            }

            ItemStack itemInHand = player.getItemInHand(hand);
            if (!itemInHand.isEmpty())
            {
                if (itemInHand.getItem() == SwiftItems.s_copyPastaItem)
                {
                    if (player.isShiftKeyDown())
                    {
                        if (CopyPastaItem.copyTileEntitySettings(itemInHand, blockEntity, dir))
                        {
                            player.displayClientMessage(new TextComponent("Copied"), true);
                            return InteractionResult.SUCCESS;
                        }
                        else
                        {
                            player.displayClientMessage(new TextComponent("Cannot copy"), true);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    else
                    {
                        if (CopyPastaItem.pasteTileEntitySettings(itemInHand, blockEntity, dir))
                        {
                            player.displayClientMessage(new TextComponent("Pasted"), true);
                            return InteractionResult.SUCCESS;
                        }
                        else
                        {
                            player.displayClientMessage(new TextComponent("Cannot paste"), true);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                else if (blockEntity.tryAddUpgrade(player.getInventory(), hand, dir))
                {
                    return InteractionResult.SUCCESS;
                }
            }

            if (player.isShiftKeyDown())
                return InteractionResult.SUCCESS; // do nothing if crouching (shift)

            MenuProvider menuProvider = getMenuProvider(state, world, pos);
            if (menuProvider != null)
            {
                if (!(player instanceof ServerPlayer))
                    return InteractionResult.FAIL; // should always be true, but just in case...

                openGui((ServerPlayer) player, menuProvider, blockEntity, dir);
            }

            return InteractionResult.SUCCESS;
        }
        else
        {
            return InteractionResult.PASS;
        }
    }

    @Nullable
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
    	if (level.isClientSide)
    		return null;
    	else
    		return createTicker(state, type);
    }

    protected abstract <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockState state, BlockEntityType<T> type);

    protected abstract void openGui(ServerPlayer player, MenuProvider menuProvider,
            PipeTileEntity<?, ?, ?> blockEntity, Direction startingDir);

    @Override
    public void onRemove(BlockState state, Level world, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof PipeTileEntity)
            {
                ((PipeTileEntity<?, ?, ?>) blockEntity).dropAllContents(world, blockPos);
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN, WEST, EAST, NORTH, SOUTH);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext)
    {
        Level level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();

        BlockState blockState = getStateDefinition().any().setValue(UP, canConnect(level, blockPos, Direction.UP))
                .setValue(DOWN, canConnect(level, blockPos, Direction.DOWN))
                .setValue(EAST, canConnect(level, blockPos, Direction.EAST))
                .setValue(WEST, canConnect(level, blockPos, Direction.WEST))
                .setValue(NORTH, canConnect(level, blockPos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, blockPos, Direction.SOUTH));
        return blockState;
    }

    @Override
    public BlockState updateShape(BlockState thisState, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos thisPos, BlockPos neighborPos)
    {
        return updateStateOnNeighborChange(level, thisState, direction, thisPos);
    }

    public BlockState updateStateOnNeighborChange(BlockGetter blockGetter, BlockState state, Direction direction,
            BlockPos pos)
    {
        boolean connect = canConnect(blockGetter, pos, direction);
        switch (direction)
        {
        case UP:
            state = state.setValue(UP, connect);
            break;
        case DOWN:
            state = state.setValue(DOWN, connect);
            break;
        case EAST:
            state = state.setValue(EAST, connect);
            break;
        case WEST:
            state = state.setValue(WEST, connect);
            break;
        case NORTH:
            state = state.setValue(NORTH, connect);
            break;
        case SOUTH:
            state = state.setValue(SOUTH, connect);
            break;
        }
        return state;
    }

    // TODO: Move to tile entity onNeighborChange, possibly
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos neighbor, boolean isMoving)
    {
        Direction dir = SwiftUtils.getDirectionBetweenBlocks(pos, neighbor);
        if (dir != null)
        {
            updateStateOnNeighborChange(level, state, dir, pos);
        }

        super.neighborChanged(state, level, pos, blockIn, neighbor, isMoving);
    }

    private static VoxelShape[] populateShapes()
    {
        VoxelShape[] shapes = new VoxelShape[1 << 6];
        for (int i = 0; i < shapes.length; ++i)
        {
            VoxelShape shape = BASE_SHAPE;
            if ((i & 0x01) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_UP_SHAPE), ATTACHMENT_UP_SHAPE);
            if ((i & 0x02) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_DOWN_SHAPE), ATTACHMENT_DOWN_SHAPE);
            if ((i & 0x04) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_WEST_SHAPE), ATTACHMENT_WEST_SHAPE);
            if ((i & 0x08) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_EAST_SHAPE), ATTACHMENT_EAST_SHAPE);
            if ((i & 0x10) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_NORTH_SHAPE), ATTACHMENT_NORTH_SHAPE);
            if ((i & 0x20) != 0)
                shape = Shapes.or(Shapes.or(shape, LINK_SOUTH_SHAPE), ATTACHMENT_SOUTH_SHAPE);
            shapes[i] = shape;
        }
        return shapes;
    }

    private static List<IndexedVoxelShape>[] populateIndexedShapes()
    {
        @SuppressWarnings("unchecked")
        List<IndexedVoxelShape>[] shapes = new List[1 << 6];
        for (int i = 0; i < shapes.length; ++i)
        {
            List<IndexedVoxelShape> s = new ArrayList<IndexedVoxelShape>();
            s.add(new IndexedVoxelShape(BASE_SHAPE));
            if ((i & 0x01) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
                s.add(new IndexedVoxelShape(ATTACHMENT_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
            }
            if ((i & 0x02) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
                s.add(new IndexedVoxelShape(ATTACHMENT_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
            }
            if ((i & 0x04) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
                s.add(new IndexedVoxelShape(ATTACHMENT_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
            }
            if ((i & 0x08) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
                s.add(new IndexedVoxelShape(ATTACHMENT_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
            }
            if ((i & 0x10) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
                s.add(new IndexedVoxelShape(ATTACHMENT_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
            }
            if ((i & 0x20) != 0)
            {
                s.add(new IndexedVoxelShape(LINK_SOUTH_SHAPE, SwiftUtils.dirToIndex(Direction.SOUTH)));
                s.add(new IndexedVoxelShape(ATTACHMENT_SOUTH_SHAPE, SwiftUtils.dirToIndex(Direction.SOUTH)));
            }
            shapes[i] = s;
        }
        return shapes;
    }

    protected static int BlockStateToShapeIndex(BlockState state)
    {
        int index = 0;
        if (state.getValue(UP).booleanValue())
            index |= 0x01;
        if (state.getValue(DOWN).booleanValue())
            index |= 0x02;
        if (state.getValue(WEST).booleanValue())
            index |= 0x04;
        if (state.getValue(EAST).booleanValue())
            index |= 0x08;
        if (state.getValue(NORTH).booleanValue())
            index |= 0x10;
        if (state.getValue(SOUTH).booleanValue())
            index |= 0x20;
        return index;
    }

    public boolean canConnect(BlockGetter blockGetter, BlockPos position, Direction direction)
    {
        BlockPos neighborPos = position.relative(direction);
        BlockEntity blockEntity = blockGetter.getBlockEntity(neighborPos);
        return canConnect(blockEntity, direction.getOpposite());
    }

    public abstract boolean canConnect(BlockEntity blockEntity, Direction direction);

    private static final Properties PROPERTIES = Block.Properties.of(Material.STONE).strength(0.2f, 0.2f);

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;

    private static final double FULL_LENGTH = 16.0;
    private static final double BASE_MARGIN = 5.0;
    private static final double LINK_MARGIN = 6.0;
    private static final double ATTACHMENT_MARGIN = 2.0;
    private static final double ATTACHMENT_WIDTH = 0.5;
    private static final Vec3 MIN_BASE_CORNER = new Vec3(BASE_MARGIN, BASE_MARGIN, BASE_MARGIN);
    private static final Vec3 MAX_BASE_CORNER = new Vec3(FULL_LENGTH - BASE_MARGIN, FULL_LENGTH - BASE_MARGIN,
            FULL_LENGTH - BASE_MARGIN);

    private static final VoxelShape BASE_SHAPE = Block.box(MIN_BASE_CORNER.x, MIN_BASE_CORNER.y, MIN_BASE_CORNER.z,
            MAX_BASE_CORNER.x, MAX_BASE_CORNER.y, MAX_BASE_CORNER.z);

    private static final VoxelShape LINK_UP_SHAPE = Block.box(LINK_MARGIN, MAX_BASE_CORNER.y, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - ATTACHMENT_WIDTH, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape LINK_DOWN_SHAPE = Block.box(LINK_MARGIN, ATTACHMENT_WIDTH, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, MIN_BASE_CORNER.y, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape LINK_WEST_SHAPE = Block.box(ATTACHMENT_WIDTH, LINK_MARGIN, LINK_MARGIN, MIN_BASE_CORNER.x,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape LINK_EAST_SHAPE = Block.box(MAX_BASE_CORNER.x, LINK_MARGIN, LINK_MARGIN,
            FULL_LENGTH - ATTACHMENT_WIDTH, FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape LINK_NORTH_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, ATTACHMENT_WIDTH,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN, MIN_BASE_CORNER.z);
    private static final VoxelShape LINK_SOUTH_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, MAX_BASE_CORNER.z,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - ATTACHMENT_WIDTH);

    private static final VoxelShape ATTACHMENT_UP_SHAPE = Block.box(ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_WIDTH,
            ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_MARGIN, FULL_LENGTH, FULL_LENGTH - ATTACHMENT_MARGIN);
    private static final VoxelShape ATTACHMENT_DOWN_SHAPE = Block.box(ATTACHMENT_MARGIN, 0.0, ATTACHMENT_MARGIN,
            FULL_LENGTH - ATTACHMENT_MARGIN, ATTACHMENT_WIDTH, FULL_LENGTH - ATTACHMENT_MARGIN);
    private static final VoxelShape ATTACHMENT_WEST_SHAPE = Block.box(0.0, ATTACHMENT_MARGIN, ATTACHMENT_MARGIN,
            ATTACHMENT_WIDTH, FULL_LENGTH - ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_MARGIN);
    private static final VoxelShape ATTACHMENT_EAST_SHAPE = Block.box(FULL_LENGTH - ATTACHMENT_WIDTH, ATTACHMENT_MARGIN,
            ATTACHMENT_MARGIN, FULL_LENGTH, FULL_LENGTH - ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_MARGIN);
    private static final VoxelShape ATTACHMENT_NORTH_SHAPE = Block.box(ATTACHMENT_MARGIN, ATTACHMENT_MARGIN, 0.0,
            FULL_LENGTH - ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_MARGIN, ATTACHMENT_WIDTH);
    private static final VoxelShape ATTACHMENT_SOUTH_SHAPE = Block.box(ATTACHMENT_MARGIN, ATTACHMENT_MARGIN,
            FULL_LENGTH - ATTACHMENT_WIDTH, FULL_LENGTH - ATTACHMENT_MARGIN, FULL_LENGTH - ATTACHMENT_MARGIN,
            FULL_LENGTH);

    protected static final VoxelShape[] SHAPES = populateShapes();
    protected static final List<IndexedVoxelShape>[] INDEXED_SHAPES = populateIndexedShapes();
}
