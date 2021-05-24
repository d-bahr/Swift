package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import swiftmod.common.CopyPastaItem;
import swiftmod.common.IndexedVoxelShape;
import swiftmod.common.Raytracer;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;

@SuppressWarnings("deprecation")
public abstract class PipeBlock extends ContainerBlock implements ITileEntityProvider
{
    public PipeBlock()
    {
        super(PROPERTIES);

        BlockState defaultState = getStateDefinition().any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false);
        registerDefaultState(defaultState);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, BlockRayTraceResult hit)
    {
        if (world.isClientSide)
            return ActionResultType.SUCCESS; // on client side, don't do anything

        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PipeTileEntity)
        {
            PipeTileEntity<?,?,?> tileEntity = (PipeTileEntity<?,?,?>) te;

            ItemStack itemInHand = player.getItemInHand(hand);
            if (!itemInHand.isEmpty())
            {
                int index = BlockStateToShapeIndex(state);
                Optional<Integer> r = Raytracer.raytrace(INDEXED_SHAPES[index], player, pos);
    
                Direction dir = null;
                if (r.isPresent())
                {
                    int i = r.get();
                    if (i >= 0)
                        dir = SwiftUtils.indexToDir(i);
                }

                if (itemInHand.getItem() == SwiftItems.s_copyPastaItem)
                {
                    if (player.isCrouching())
                    {
                        if (CopyPastaItem.copyTileEntitySettings(itemInHand, tileEntity, dir))
                        {
                            player.displayClientMessage(new StringTextComponent("Copied"), true);
                            return ActionResultType.SUCCESS;
                        }
                        else
                        {
                            player.displayClientMessage(new StringTextComponent("Cannot copy"), true);
                            return ActionResultType.SUCCESS;
                        }
                    }
                    else
                    {
                        if (CopyPastaItem.pasteTileEntitySettings(itemInHand, tileEntity, dir))
                        {
                            player.displayClientMessage(new StringTextComponent("Pasted"), true);
                            return ActionResultType.SUCCESS;
                        }
                        else
                        {
                            player.displayClientMessage(new StringTextComponent("Cannot paste"), true);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
                else if (tileEntity.tryAddUpgrade(player.inventory, hand, dir))
                {
                    return ActionResultType.SUCCESS;
                }
            }

            if (player.isCrouching())
                return ActionResultType.SUCCESS; // do nothing if crouching (shift)

            INamedContainerProvider namedContainerProvider = getMenuProvider(state, world, pos);
            if (namedContainerProvider != null)
            {
                if (!(player instanceof ServerPlayerEntity))
                    return ActionResultType.FAIL; // should always be true, but just in case...

                openGui((ServerPlayerEntity)player, namedContainerProvider, tileEntity);
            }

            return ActionResultType.SUCCESS;
        }
        else
        {
            return ActionResultType.PASS;
        }
    }

    protected abstract void openGui(ServerPlayerEntity player, INamedContainerProvider namedContainerProvider, PipeTileEntity<?,?,?> tileEntity);

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return newBlockEntity(world);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity instanceof PipeTileEntity)
            {
                ((PipeTileEntity<?,?,?>) tileEntity).dropAllContents(world, blockPos);
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState blockState)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        int index = BlockStateToShapeIndex(state);
        return SHAPES[index];
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN, WEST, EAST, NORTH, SOUTH);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext)
    {
        World world = blockItemUseContext.getLevel();
        BlockPos blockPos = blockItemUseContext.getClickedPos();

        BlockState blockState = getStateDefinition().any()
                .setValue(UP, canConnect(world, blockPos, Direction.UP))
                .setValue(DOWN, canConnect(world, blockPos, Direction.DOWN))
                .setValue(EAST, canConnect(world, blockPos, Direction.EAST))
                .setValue(WEST, canConnect(world, blockPos, Direction.WEST))
                .setValue(NORTH, canConnect(world, blockPos, Direction.NORTH))
                .setValue(SOUTH, canConnect(world, blockPos, Direction.SOUTH));
        return blockState;
    }

    @Override
    public BlockState updateShape(BlockState thisState, Direction direction, BlockState neighborState,
            IWorld world, BlockPos thisPos, BlockPos neighborPos)
    {
        return updateStateOnNeighborChange(world, thisState, direction, thisPos);
    }

    private BlockState updateStateOnNeighborChange(IBlockReader world, BlockState state, Direction direction, BlockPos pos)
    {
        boolean connect = canConnect(world, pos, direction);
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

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
    {
        Direction dir = SwiftUtils.getDirectionBetweenBlocks(pos, neighbor);
        if (dir != null)
        {
            updateStateOnNeighborChange(world, state, dir, pos);
            //TileEntity tileEntity = world.getTileEntity(pos);
            //tileEntity.markDirty(); // Force an update to the client so it reads the block states again.
        }
    }

    private static VoxelShape[] populateShapes()
    {
        VoxelShape[] shapes = new VoxelShape[1 << 6];
        for (int i = 0; i < shapes.length; ++i)
        {
            VoxelShape shape = BASE_SHAPE;
            if ((i & 0x01) != 0)
                shape = VoxelShapes.or(shape, LINK_UP_SHAPE);
            if ((i & 0x02) != 0)
                shape = VoxelShapes.or(shape, LINK_DOWN_SHAPE);
            if ((i & 0x04) != 0)
                shape = VoxelShapes.or(shape, LINK_WEST_SHAPE);
            if ((i & 0x08) != 0)
                shape = VoxelShapes.or(shape, LINK_EAST_SHAPE);
            if ((i & 0x10) != 0)
                shape = VoxelShapes.or(shape, LINK_NORTH_SHAPE);
            if ((i & 0x20) != 0)
                shape = VoxelShapes.or(shape, LINK_SOUTH_SHAPE);
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
                s.add(new IndexedVoxelShape(LINK_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
            if ((i & 0x02) != 0)
                s.add(new IndexedVoxelShape(LINK_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
            if ((i & 0x04) != 0)
                s.add(new IndexedVoxelShape(LINK_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
            if ((i & 0x08) != 0)
                s.add(new IndexedVoxelShape(LINK_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
            if ((i & 0x10) != 0)
                s.add(new IndexedVoxelShape(LINK_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
            if ((i & 0x20) != 0)
                s.add(new IndexedVoxelShape(LINK_SOUTH_SHAPE, SwiftUtils.dirToIndex(Direction.SOUTH)));
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

    public boolean canConnect(IBlockReader blockReader, BlockPos position, Direction direction)
    {
        BlockPos neighborPos = position.relative(direction);
        TileEntity tileEntity = blockReader.getBlockEntity(neighborPos);
        return canConnect(tileEntity, direction.getOpposite());
    }

    public abstract boolean canConnect(TileEntity tileEntity, Direction direction);

    private static final Properties PROPERTIES = Block.Properties.of(Material.STONE).strength(0.2f, 0.2f);

    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;

    private static final double INNER_LENGTH = 4.0;
    private static final Vector3d MIN_BASE_CORNER = new Vector3d(INNER_LENGTH, INNER_LENGTH, INNER_LENGTH);
    private static final Vector3d MAX_BASE_CORNER = new Vector3d(16.0 - INNER_LENGTH, 16.0 - INNER_LENGTH, 16.0 - INNER_LENGTH);

    private static final VoxelShape BASE_SHAPE = Block.box(
            MIN_BASE_CORNER.x, MIN_BASE_CORNER.y, MIN_BASE_CORNER.z,
            MAX_BASE_CORNER.x, MAX_BASE_CORNER.y, MAX_BASE_CORNER.z);

    private static final VoxelShape LINK_UP_SHAPE = Block.box(
            MIN_BASE_CORNER.x, MAX_BASE_CORNER.y, MIN_BASE_CORNER.z,
            MAX_BASE_CORNER.x, 16.0,                   MAX_BASE_CORNER.z);
    private static final VoxelShape LINK_DOWN_SHAPE = Block.box(
            MIN_BASE_CORNER.x, 0.0,                    MIN_BASE_CORNER.z,
            MAX_BASE_CORNER.x, MIN_BASE_CORNER.y, MAX_BASE_CORNER.z);
    private static final VoxelShape LINK_WEST_SHAPE = Block.box(
            0.0,                    MIN_BASE_CORNER.y, MIN_BASE_CORNER.z,
            MIN_BASE_CORNER.x, MAX_BASE_CORNER.y, MAX_BASE_CORNER.z);
    private static final VoxelShape LINK_EAST_SHAPE = Block.box(
            MAX_BASE_CORNER.x, MIN_BASE_CORNER.y, MIN_BASE_CORNER.z,
            16.0,                   MAX_BASE_CORNER.y, MAX_BASE_CORNER.z);
    private static final VoxelShape LINK_NORTH_SHAPE = Block.box(
            MIN_BASE_CORNER.x, MIN_BASE_CORNER.y, 0.0,
            MAX_BASE_CORNER.x, MAX_BASE_CORNER.y, MIN_BASE_CORNER.z);
    private static final VoxelShape LINK_SOUTH_SHAPE = Block.box(
            MIN_BASE_CORNER.x, MIN_BASE_CORNER.y, MAX_BASE_CORNER.z,
            MAX_BASE_CORNER.x, MAX_BASE_CORNER.y, 16.0);

    protected static final VoxelShape[] SHAPES = populateShapes();
    protected static final List<IndexedVoxelShape>[] INDEXED_SHAPES = populateIndexedShapes();
}
