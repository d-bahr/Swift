package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import swiftmod.common.AbstractBlock;
import swiftmod.common.Color;
import swiftmod.common.CopyPastaItem;
import swiftmod.common.IndexedVoxelShape;
import swiftmod.common.Raytracer;
import swiftmod.common.Swift;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;

public abstract class PipeBlock extends AbstractBlock
{
    public PipeBlock()
    {
        super(PROPERTIES);

        BlockState defaultState = getStateDefinition().any()
        		.setValue(UP, 0)
        		.setValue(DOWN, 0)
                .setValue(EAST, 0)
                .setValue(WEST, 0)
                .setValue(NORTH, 0)
                .setValue(SOUTH, 0);
        registerDefaultState(defaultState);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult)
    {
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS; // on client side, don't do anything

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof PipeTileEntity)
        {
            PipeTileEntity blockEntity = (PipeTileEntity) te;
            Direction dir = null;

            int index = BlockStateToShapeIndex(state);
            Raytracer.Result r = Raytracer.raytrace(INDEXED_SHAPES[index], player, pos);

            if (r.hasHit())
            {
                int i = r.index.get();
                if (i >= 0)
                    dir = SwiftUtils.indexToDir(i);
            }
            
	        if (stack.getItem() == SwiftItems.s_copyPastaItem.get())
	        {
	            if (player.isShiftKeyDown())
	            {
	                if (CopyPastaItem.copyTileEntitySettings(stack, blockEntity, dir))
	                {
	                    player.displayClientMessage(Component.literal("Copied"), true);
	                    return ItemInteractionResult.SUCCESS;
	                }
	                else
	                {
	                    player.displayClientMessage(Component.literal("Cannot copy"), true);
	                    return ItemInteractionResult.SUCCESS;
	                }
	            }
	            else
	            {
	                if (CopyPastaItem.pasteTileEntitySettings(stack, blockEntity, dir))
	                {
	                    player.displayClientMessage(Component.literal("Pasted"), true);
	                    return ItemInteractionResult.SUCCESS;
	                }
	                else
	                {
	                    player.displayClientMessage(Component.literal("Cannot paste"), true);
	                    return ItemInteractionResult.SUCCESS;
	                }
	            }
	        }
	        else if (stack.getItem() == SwiftItems.s_wrenchItem.get())
	        {
	        	if (player.isShiftKeyDown())
	        	{
	        		if (level instanceof ServerLevel)
	        		{
	            		Block.getDrops(state, (ServerLevel) level, pos, te, player, ItemStack.EMPTY).forEach(drop ->
	            		{
	            			if (player.addItem(drop))
	            			{
	                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP,
	                            		SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F);
	                        }
	            			else
	            			{
	                            player.drop(drop, false);
	                        }
	            		});
	        		}
	        		else
	        		{
	        			Block.dropResources(state, level, pos, te, player, ItemStack.EMPTY);
	        		}
	        		level.removeBlock(pos, false);
	                return ItemInteractionResult.SUCCESS;
	        	}
	        	
	        	if (r.hasHit())
	        	{
	        		int val;
	        		Direction updateDir;
	            	if (dir != null)
	            	{
	            		updateDir = dir;
	            		val = getCurrentPropertyValue(state, dir);
	            		Swift.doAssert(val == 0x1 || val == 0x2, "Raytrace hit pipe connection that should be invisible.");
	            		val = 3;
	            	}
	            	else 
	            	{
	            		updateDir = r.direction;
	            		// Hit pipe base. If the value of the side that was hit is 3,
	            		// then set it back to normal. Otherwise, set it to 3 (disable connection).
	            		val = getCurrentPropertyValue(state, r.direction);
	            		
	            		if (val == 0x3)
	            			val = getPropertyValue(level, pos, r.direction);
	            		else
	            			val = 0x3;
	            	}
            		state = setPropertyValue(state, updateDir, val);
            		boolean updateTileEntity = setAdjacentPipeConnection(level, blockEntity, pos, updateDir, val);
	            	// We handle neighbor updates automatically, so do not send bit 1.
	            	// See similar code in setAdjacentPipeConnection.
	        		level.setBlock(pos, state, 2);
	        		// Update the network after changing the block entities. This is required to be done
	        		// after; otherwise the pipe network handling code may use the wrong BlockStates.
	        		if (updateTileEntity)
	        			blockEntity.updateNetworkConnection(updateDir, val != 3);
	                return ItemInteractionResult.SUCCESS;
	        	}
	        }
	        else if (blockEntity.tryAddUpgrade(player.getInventory(), hand, dir))
	        {
	            return ItemInteractionResult.SUCCESS;
	        }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    
    private boolean setAdjacentPipeConnection(Level level, PipeTileEntity tileEntity, BlockPos pos, Direction dir, int value)
    {
        BlockPos neighborPos = pos.relative(dir);
    	BlockState neighborState = level.getBlockState(neighborPos);
    	if (neighborState != null && neighborState.getBlock() instanceof PipeBlock)
    	{
    		// Only change the behavior of the other pipe if it is able of connecting to begin with.
    		// Otherwise we might as well just skip it to save some recomputation of the networks.
    		PipeTileEntity neighborEntity = (PipeTileEntity)level.getBlockEntity(neighborPos);
    		if (neighborEntity.isSamePipeType(tileEntity))
    		{
	    		BlockState newNeighborState = setPropertyValue(neighborState, dir.getOpposite(), value);
	    		// Do not send bit flag 1. Bit 1 is used for notifying neighbors of updates via
	    		// neighborChanged, which is unnecessary in this case.
	    		if (newNeighborState != neighborState)
	    		{
	    			level.setBlock(neighborPos, newNeighborState, 2);
	    			return true;
	    		}
    		}
    	}
    	// If the neighbor is a wormhole, then we also may need to update network connections,
    	// so check for that and return true if the neighbor is a connectable wormhole.
    	return neighborState.getBlock() instanceof WormholeBlock && tileEntity.canConnectToWormhole();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (world.isClientSide)
            return InteractionResult.SUCCESS; // on client side, don't do anything

        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof PipeTileEntity)
        {
            PipeTileEntity blockEntity = (PipeTileEntity) te;
            Direction dir = null;

            int index = BlockStateToShapeIndex(state);
            Raytracer.Result r = Raytracer.raytrace(INDEXED_SHAPES[index], player, pos);

            if (r.hasHit())
            {
                int i = r.index.get();
                if (i >= 0)
                    dir = SwiftUtils.indexToDir(i);
            }

            // If the pipe has nothing connected, then don't open the screen, unless
            // shift is held.
            if (!isPipeConnectedAnywhere(index) && !player.isShiftKeyDown())
                return InteractionResult.PASS; // do nothing

            MenuProvider menuProvider = getMenuProvider(state, world, pos);
            if (menuProvider != null)
            {
                if (!(player instanceof ServerPlayer))
                    return InteractionResult.FAIL; // should always be true, but just in case...

                openScreen((ServerPlayer) player, menuProvider, blockEntity, dir);
            }

            return InteractionResult.SUCCESS;
        }
        else
        {
            return InteractionResult.PASS;
        }
    }
    
    // Returns true if the pipe is currently connected on any side to a matching inventory.
    private boolean isPipeConnectedAnywhere(int index)
    {
    	return	((index & 0x003) == 0x002) ||
    			((index & 0x00C) == 0x008) ||
    			((index & 0x030) == 0x020) ||
    			((index & 0x0C0) == 0x080) ||
    			((index & 0x300) == 0x200) ||
    			((index & 0xC00) == 0x800);
    }

    @Nullable
    @Override
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
    	if (level.isClientSide)
    		return null;
    	else
    		return createTicker(state, type);
    }

    protected abstract <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockState state, BlockEntityType<T> type);

    protected abstract void openScreen(ServerPlayer player, MenuProvider menuProvider,
            PipeTileEntity blockEntity, Direction startingDir);

    @Override
    public void onRemove(BlockState state, Level world, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof PipeTileEntity)
            {
                ((PipeTileEntity) blockEntity).onRemove(world, blockPos);
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
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
        
        BlockState blockState = getStateDefinition().any().setValue(UP, getPropertyValueForBlockPlacement(level, blockPos, Direction.UP))
                .setValue(DOWN, getPropertyValueForBlockPlacement(level, blockPos, Direction.DOWN))
                .setValue(EAST, getPropertyValueForBlockPlacement(level, blockPos, Direction.EAST))
                .setValue(WEST, getPropertyValueForBlockPlacement(level, blockPos, Direction.WEST))
                .setValue(NORTH, getPropertyValueForBlockPlacement(level, blockPos, Direction.NORTH))
                .setValue(SOUTH, getPropertyValueForBlockPlacement(level, blockPos, Direction.SOUTH));
        return blockState;
    }

    @Override
    public BlockState updateShape(BlockState thisState, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos thisPos, BlockPos neighborPos)
    {
    	if (level instanceof Level)
    		return updateStateOnNeighborChange((Level)level, thisState, direction, thisPos);
    	else
    		return thisState;
    }

    public BlockState updateStateOnNeighborChange(Level level, BlockState state, Direction direction, BlockPos pos)
    {
    	BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof PipeTileEntity || entity instanceof WormholeTileEntity)
        {
        	int curVal = getCurrentPropertyValue(state, direction);
        	if (curVal == 0x3)
        		return state; // Pipe has been wrenched; forcibly prevent connection.
	        int value = getPropertyValue(level, pos, direction);
	        state = setPropertyValue(state, direction, value);
        }
        return state;
    }

    // TODO: Move to tile entity onNeighborChange, possibly
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving)
    {
        Direction dir = SwiftUtils.getDirectionBetweenBlocks(pos, neighborPos);
        if (dir != null)
        {
        	state = updateStateOnNeighborChange(level, state, dir, pos);
        }

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }
    
    public static boolean stateAllowsConnections(BlockState state, Direction dir)
    {
    	return getCurrentPropertyValue(state, dir) != 3;
    }

    private static VoxelShape[] populateShapes()
    {
        VoxelShape[] shapes = new VoxelShape[1 << 12];
        for (int i = 0; i < shapes.length; ++i)
        {
        	// Each side has four possible states, representable via two bits:
        	// 0 = Not connected
        	// 1 = Connected to pipe
        	// 2 = Connected (with attachment) to inventory
        	// 3 = Could connect (either states 1 or 2) but is prevented from doing so via a wrench.
        	
            VoxelShape shape;
            
            if ((i & 0x0003) == 0x0002 ||
                (i & 0x000c) == 0x0008 ||
                (i & 0x0030) == 0x0020 ||
                (i & 0x00c0) == 0x0080 ||
                (i & 0x0300) == 0x0200 ||
                (i & 0x0c00) == 0x0800)
			{
				// Not connected to any inventories; use the smaller base shape.
            	shape = BASE_SHAPE_CONNECTED;
			}
            else
            {
            	// Connected to an inventory.
            	shape = BASE_SHAPE;
            }
            
            // Up (b000000000011)
            if ((i & 0x0003) == 0x0001)
            {
                shape = Shapes.or(shape, FULL_LINK_UP_SHAPE);
            }
            else if ((i & 0x0003) == 0x0002)
            {
                shape = Shapes.or(shape, LINK_UP_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_UP_SHAPE);
            }

            // Down (b000000001100)
            if ((i & 0x000c) == 0x0004)
            {
                shape = Shapes.or(shape, FULL_LINK_DOWN_SHAPE);
            }
            else if ((i & 0x000c) == 0x0008)
            {
                shape = Shapes.or(shape, LINK_DOWN_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_DOWN_SHAPE);
            }

            // West (b000000110000)
            if ((i & 0x0030) == 0x0010)
            {
                shape = Shapes.or(shape, FULL_LINK_WEST_SHAPE);
            }
            else if ((i & 0x0030) == 0x0020)
            {
                shape = Shapes.or(shape, LINK_WEST_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_WEST_SHAPE);
            }

            // East (b000011000000)
            if ((i & 0x00c0) == 0x0040)
            {
                shape = Shapes.or(shape, FULL_LINK_EAST_SHAPE);
            }
            else if ((i & 0x00c0) == 0x0080)
            {
                shape = Shapes.or(shape, LINK_EAST_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_EAST_SHAPE);
            }

            // North (b001100000000)
            if ((i & 0x0300) == 0x0100)
            {
                shape = Shapes.or(shape, FULL_LINK_NORTH_SHAPE);
            }
            else if ((i & 0x0300) == 0x0200)
            {
                shape = Shapes.or(shape, LINK_NORTH_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_NORTH_SHAPE);
            }

            // South (b110000000000)
            if ((i & 0x0c00) == 0x0400)
            {
                shape = Shapes.or(shape, FULL_LINK_SOUTH_SHAPE);
            }
            else if ((i & 0x0c00) == 0x0800)
            {
                shape = Shapes.or(shape, LINK_SOUTH_SHAPE);
                shape = Shapes.or(shape, ATTACHMENT_SOUTH_SHAPE);
            }
            
            shapes[i] = shape;
        }
        return shapes;
    }

    private static List<IndexedVoxelShape>[] populateIndexedShapes()
    {
        @SuppressWarnings("unchecked")
        List<IndexedVoxelShape>[] shapes = new List[1 << 12];
        for (int i = 0; i < shapes.length; ++i)
        {
        	// Each side has four possible states, representable via two bits:
        	// 0 = Not connected
        	// 1 = Connected to pipe
        	// 2 = Connected (with attachment) to inventory
        	// 3 = Could connect (either states 1 or 2) but is prevented from doing so via a wrench.
        	
            List<IndexedVoxelShape> s = new ArrayList<IndexedVoxelShape>();
        	
            if ((i & 0x0003) == 0x0002 ||
                (i & 0x000c) == 0x0008 ||
                (i & 0x0030) == 0x0020 ||
                (i & 0x00c0) == 0x0080 ||
                (i & 0x0300) == 0x0200 ||
                (i & 0x0c00) == 0x0800)
			{
            	// Connected to an inventory.
                s.add(new IndexedVoxelShape(BASE_SHAPE_CONNECTED));
			}
            else
            {
				// Not connected to any inventories; use the smaller base shape.
                s.add(new IndexedVoxelShape(BASE_SHAPE));
            }
            
            // Up (b000000000011)
            if ((i & 0x0003) == 0x0001)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
            }
            else if ((i & 0x0003) == 0x0002)
            {
                s.add(new IndexedVoxelShape(LINK_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
            	s.add(new IndexedVoxelShape(ATTACHMENT_UP_SHAPE, SwiftUtils.dirToIndex(Direction.UP)));
            }

            // Down (b000000001100)
            if ((i & 0x000c) == 0x0004)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
            }
            else if ((i & 0x000c) == 0x0008)
            {
                s.add(new IndexedVoxelShape(LINK_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
            	s.add(new IndexedVoxelShape(ATTACHMENT_DOWN_SHAPE, SwiftUtils.dirToIndex(Direction.DOWN)));
            }

            // West (b000000110000)
            if ((i & 0x0030) == 0x0010)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
            }
            else if ((i & 0x0030) == 0x0020)
            {
                s.add(new IndexedVoxelShape(LINK_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
            	s.add(new IndexedVoxelShape(ATTACHMENT_WEST_SHAPE, SwiftUtils.dirToIndex(Direction.WEST)));
            }

            // East (b000011000000)
            if ((i & 0x00c0) == 0x0040)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
            }
            else if ((i & 0x00c0) == 0x0080)
            {
                s.add(new IndexedVoxelShape(LINK_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
            	s.add(new IndexedVoxelShape(ATTACHMENT_EAST_SHAPE, SwiftUtils.dirToIndex(Direction.EAST)));
            }

            // North (b001100000000)
            if ((i & 0x0300) == 0x0100)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
            }
            else if ((i & 0x0300) == 0x0200)
            {
                s.add(new IndexedVoxelShape(LINK_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
            	s.add(new IndexedVoxelShape(ATTACHMENT_NORTH_SHAPE, SwiftUtils.dirToIndex(Direction.NORTH)));
            }

            // South (b110000000000)
            if ((i & 0x0c00) == 0x0400)
            {
                s.add(new IndexedVoxelShape(FULL_LINK_SOUTH_SHAPE, SwiftUtils.dirToIndex(Direction.SOUTH)));
            }
            else if ((i & 0x0c00) == 0x0800)
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
        int index =
    		(state.getValue(UP).intValue()) |
    		(state.getValue(DOWN).intValue() << 2) |
    		(state.getValue(WEST).intValue() << 4) |
    		(state.getValue(EAST).intValue() << 6) |
    		(state.getValue(NORTH).intValue() << 8) |
    		(state.getValue(SOUTH).intValue() << 10);
        return index;
    }
    
    private static BlockState setPropertyValue(BlockState state, Direction direction, int value)
    {
        switch (direction)
        {
        case UP:
        	return state.setValue(UP, value);
        case DOWN:
        	return state.setValue(DOWN, value);
        case EAST:
        	return state.setValue(EAST, value);
        case WEST:
        	return state.setValue(WEST, value);
        case NORTH:
        	return state.setValue(NORTH, value);
        case SOUTH:
        	return state.setValue(SOUTH, value);
        }
        return state;
    }
    
    private static int getCurrentPropertyValue(BlockState state, Direction dir)
    {
    	switch (dir)
    	{
    	case UP:
    		return state.getValue(UP).intValue();
    	case DOWN:
    		return state.getValue(DOWN).intValue();
    	case EAST:
    		return state.getValue(EAST).intValue();
    	case WEST:
    		return state.getValue(WEST).intValue();
    	case NORTH:
    		return state.getValue(NORTH).intValue();
    	case SOUTH:
    		return state.getValue(SOUTH).intValue();
    	default:
    		return 0;
    	}
    }

    public abstract boolean canConnect(Level level, BlockPos pos, Direction direction);

    protected abstract boolean matchesPipe(BlockEntity blockEntity);
    
    private int getPropertyValueForBlockPlacement(Level level, BlockPos position, Direction direction)
    {
        BlockPos neighborPos = position.relative(direction);
        BlockEntity entity = level.getBlockEntity(neighborPos);
        int state = 0;
    	if (matchesPipe(entity))
    	{
    		state = 1; // Default condition.
    		if (!(entity instanceof WormholeTileEntity))
			{
	        	BlockState neighborState = level.getBlockState(neighborPos);
	        	if (getCurrentPropertyValue(neighborState, direction.getOpposite()) == 3)
	        		state = 3; // If the neighbor is disconnected, then we should also be.
			}
    	}
    	else if (canConnect(level, neighborPos, direction.getOpposite()))
    		state = 2;
        return state;
    }
    
    private int getPropertyValue(Level level, BlockPos position, Direction direction)
    {
        BlockPos neighborPos = position.relative(direction);
        BlockEntity entity = level.getBlockEntity(neighborPos);
        int state = 0;
    	if (matchesPipe(entity))
			state = 1;
    	else if (canConnect(level, neighborPos, direction.getOpposite()))
    		state = 2;
        return state;
    }
    
    public static int tintPipeBlocks(BlockState state, BlockAndTintGetter getter, BlockPos pos, int tintIndex)
    {
    	Color color = Color.Transparent;
        BlockEntity blockEntity = getter.getBlockEntity(pos);
        if (blockEntity instanceof PipeTileEntity)
        {
        	PipeTileEntity tileEntity = (PipeTileEntity)blockEntity;
        	color = tileEntity.getRenderColorForSide(Direction.from3DDataValue(tintIndex));
        }
        return color.argb();
    }
    
    private static final Properties PROPERTIES = Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 0.5F);

    // Very succinct property names so that loading the block state is as quick as possible.
    public static final IntegerProperty UP = IntegerProperty.create("a", 0, 3);
    public static final IntegerProperty DOWN = IntegerProperty.create("b", 0, 3);
    public static final IntegerProperty WEST = IntegerProperty.create("c", 0, 3);
    public static final IntegerProperty EAST = IntegerProperty.create("d", 0, 3);
    public static final IntegerProperty NORTH = IntegerProperty.create("e", 0, 3);
    public static final IntegerProperty SOUTH = IntegerProperty.create("f", 0, 3);

    private static final double FULL_LENGTH = 16.0;
    private static final double BASE_MARGIN = 5.0;
    private static final double LINK_MARGIN = 6.0;
    private static final double ATTACHMENT_MARGIN = 2.0;
    private static final double ATTACHMENT_WIDTH = 0.5;
    private static final Vec3 MIN_BASE_CORNER = new Vec3(BASE_MARGIN, BASE_MARGIN, BASE_MARGIN);
    private static final Vec3 MAX_BASE_CORNER = new Vec3(FULL_LENGTH - BASE_MARGIN, FULL_LENGTH - BASE_MARGIN,
            FULL_LENGTH - BASE_MARGIN);

    private static final VoxelShape BASE_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, LINK_MARGIN,
    		FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);

    private static final VoxelShape BASE_SHAPE_CONNECTED = Block.box(MIN_BASE_CORNER.x, MIN_BASE_CORNER.y, MIN_BASE_CORNER.z,
            MAX_BASE_CORNER.x, MAX_BASE_CORNER.y, MAX_BASE_CORNER.z);

    private static final VoxelShape FULL_LINK_UP_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape FULL_LINK_DOWN_SHAPE = Block.box(LINK_MARGIN, 0.0, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape FULL_LINK_WEST_SHAPE = Block.box(0.0, LINK_MARGIN, LINK_MARGIN, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape FULL_LINK_EAST_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, LINK_MARGIN,
            FULL_LENGTH, FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN);
    private static final VoxelShape FULL_LINK_NORTH_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, 0.0,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN, LINK_MARGIN);
    private static final VoxelShape FULL_LINK_SOUTH_SHAPE = Block.box(LINK_MARGIN, LINK_MARGIN, LINK_MARGIN,
            FULL_LENGTH - LINK_MARGIN, FULL_LENGTH - LINK_MARGIN, FULL_LENGTH);

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
