package swiftmod.pipes;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import swiftmod.common.Color;
import swiftmod.common.NeighboringItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.pipes.networks.PipeNetwork;

public abstract class EnergyPipeTileEntity extends PipeTileEntity
{
    public EnergyPipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, UpgradeInventory upgradeInventory,
    		int numSideInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        super(type, EnumSet.of(PipeType.Energy), pos, state, upgradeInventory, numSideInventories, sideUpgradeInventorySupplier);

        m_network = null;
    }

	@Override
	public List<PipeNetwork> getNetworks()
	{
		LinkedList<PipeNetwork> networks = new LinkedList<PipeNetwork>();
		if (m_network != null)
			networks.add(m_network);
		return networks;
	}

	@Override
    public PipeTransferData<?> getTransferData(PipeType type, Direction neighborDir, Direction handlerDir)
    {
    	int dirIndex = SwiftUtils.dirToIndex(neighborDir);
    	FilterlessPipeTransferData td = new FilterlessPipeTransferData();
    	td.maxTransferQuantity = m_transferQuantities[dirIndex];
    	td.tickRate = m_tickRates[dirIndex];
    	td.redstoneControl = m_cache.redstoneControls[dirIndex];
    	td.color = m_cache.colors[dirIndex];
    	return td;
    }

    @Override
    protected void assignNetwork(PipeNetwork network, PipeType type)
    {
    	if (network == null || type == PipeType.Energy)
    		m_network = network;
    }
    
    public PipeNetwork getNetwork(PipeType type)
    {
    	if (type == PipeType.Energy)
    		return m_network;
    	else
    		return null;
    }
	
    @Override
	public Color getRenderColorForSide(Direction dir)
	{
    	if (m_cache == null)
    		return Color.Transparent;
		return m_cache.getColor(SwiftUtils.dirToIndex(dir));
	}

    public void serializeBufferForContainer(RegistryFriendlyByteBuf buffer, Player player, Direction startingDir)
    {
        NeighboringItems items = new NeighboringItems(level, worldPosition, EnergyPipeBlock::isConnectableNeighbor);
        items.setStartingDirection(startingDir);
        m_cache.serialize(buffer, items);
        BlockPos.STREAM_CODEC.encode(buffer, getBlockPos());
    }
    
    @Override
    protected PipeTransferHandler<?> createTransferHandler(int transferIndex)
    {
    	EnergyPipeTransferHandler newHandler = new EnergyPipeTransferHandler();
    	newHandler.pipe = this;
    	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex);
    	newHandler.handlerDir = newHandler.neighborDir.getOpposite();
    	return newHandler;
    }

    @Override
    protected PipeTransferQuantity getTransferQuantity(int transferIndex, int stacks)
    {
    	return getTransferQuantity(stacks);
    }
    
    public static PipeTransferQuantity getTransferQuantity(int stacks)
    {
    	// The quantity here is a little strange, but they are designed to
    	// give a meaningful improvement at each step, which still leaving
    	// the ultimate upgrade to be the best option if it can be afforded.
    	if (stacks > 64) // Implies an ultimate stack upgrade
    		return new PipeTransferQuantity(false, Integer.MAX_VALUE);
    	
    	if (stacks <= 0)
    		return new PipeTransferQuantity(false, 10000); // 500 RF/t (with no speed upgrades)
    	else
    		return new PipeTransferQuantity(false, stacks * 100000); // 5000 RF/t per stack upgrade (with no speed upgrades)
    }
    
    @Override
    protected PipeType getTypeForIndex(int transferIndex)
    {
    	return PipeType.Energy;
    }

    protected PipeNetwork m_network;
}
