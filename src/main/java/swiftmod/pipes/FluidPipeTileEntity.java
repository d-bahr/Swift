package swiftmod.pipes;

import java.util.function.Supplier;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import swiftmod.common.Filter;
import swiftmod.common.NeighboringItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.OwnerBasedChannelManager;
import swiftmod.common.upgrades.IFluidFilterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;

public abstract class FluidPipeTileEntity extends PipeTileEntity<PipeDataCache, IFluidHandler, FluidStack>
{
    public FluidPipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, UpgradeInventory upgradeInventory,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier)
    {
        super(type, pos, state, new PipeDataCache(), upgradeInventory, sideUpgradeInventorySupplier);
    }

    protected PipeTileEntity<PipeDataCache, IFluidHandler, FluidStack> castToSelf(BlockEntity entity)
    {
        if (entity instanceof FluidPipeTileEntity)
            return (FluidPipeTileEntity) entity;
        else
            return null;
    }

    public void serializeBufferForContainer(FriendlyByteBuf buffer, Player player, Direction startingDir)
    {
        NeighboringItems items = new NeighboringItems(level, worldPosition, FluidPipeBlock::canConnectTo);
        items.setStartingDirection(startingDir);
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            getCache().channelConfiguration.itemStack = m_baseUpgradeInventory.getItem(slot);
        else
            getCache().channelConfiguration.itemStack = ItemStack.EMPTY;
        getCache().channelConfiguration.assignCurrentChannels(BaseChannelManager.getManager(), player);
        getCache().serialize(buffer, items);
    }

    @Override
    protected void refreshFilter(Direction dir)
    {
        int index = SwiftUtils.dirToIndex(dir);
        UpgradeInventory inventory = m_sideUpgradeInventories[index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IFluidFilterUpgradeItem)
                {
                    Filter<FluidStack> filter = ((IFluidFilterUpgradeItem) stack.getItem()).createFluidFilter(stack);
                    m_filters[index] = filter;
                    return;
                }
            }
        }

        m_filters[index] = null;
    }

    @Override
    protected IFluidHandler getHandler(BlockEntity blockEntity, Direction dir)
    {
        return SwiftUtils.getFluidHandler(blockEntity, dir);
    }

    @Override
    protected int getSize(IFluidHandler handler)
    {
        return handler.getTanks();
    }

    @Override
    protected int transfer(IFluidHandler extractHandler, int extractSlot, IFluidHandler insertHandler, int insertSlot, FluidStack stack, int numToTransfer)
    {
        FluidStack copy = stack.copy();
        copy.setAmount(numToTransfer);
        FluidStack extractedStack = extractHandler.drain(copy, FluidAction.EXECUTE);
        return insertHandler.fill(extractedStack, FluidAction.EXECUTE);
    }

    @Override
    protected int simulateInsertion(IFluidHandler insertHandler, int insertSlot, FluidStack stack)
    {
        return insertHandler.fill(stack, FluidAction.SIMULATE);
    }

    @Override
    protected FluidStack getStack(IFluidHandler handler, int slot)
    {
        return handler.getFluidInTank(slot);
    }

    @Override
    protected int getCount(FluidStack stack)
    {
        return stack.getAmount();
    }

    @Override
    protected boolean isEmpty(FluidStack stack)
    {
        return stack.isEmpty();
    }

    @Override
    protected OwnerBasedChannelManager<ChannelData> getChannelManager()
    {
        return BaseChannelManager.getManager();
    }

    @Override
    protected int getChannelTag()
    {
        return ChannelSpec.TAG_FLUIDS;
    }

    @Override
    protected TransferQuantity getTransferQuantity(int stacks)
    {
        // Multiply by 1000 to convert from millibuckets to buckets.
        long stacksLong = 1000L * ((long)stacks + 1L);
        if (stacksLong > (long)Integer.MAX_VALUE)
            stacksLong = (long)Integer.MAX_VALUE;
        return new TransferQuantity(false, (int)stacksLong);
    }
}
