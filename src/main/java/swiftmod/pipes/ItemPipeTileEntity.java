package swiftmod.pipes;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;
import swiftmod.common.Filter;
import swiftmod.common.NeighboringItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ItemChannelManager;
import swiftmod.common.channels.OwnerBasedChannelManager;
import swiftmod.common.upgrades.IItemFilterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;

public abstract class ItemPipeTileEntity extends PipeTileEntity<PipeDataCache, IItemHandler, ItemStack>
{
    public ItemPipeTileEntity(TileEntityType<?> type, UpgradeInventory upgradeInventory,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier)
    {
        super(type, new PipeDataCache(), upgradeInventory, sideUpgradeInventorySupplier);
    }

    protected PipeTileEntity<PipeDataCache, IItemHandler, ItemStack> castToSelf(TileEntity entity)
    {
        if (entity instanceof ItemPipeTileEntity)
            return (ItemPipeTileEntity) entity;
        else
            return null;
    }

    public void serializeBufferForContainer(PacketBuffer buffer, PlayerEntity player)
    {
        NeighboringItems items = new NeighboringItems(level, worldPosition, ItemPipeBlock::canConnectTo);
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            getCache().channelConfiguration.itemStack = m_baseUpgradeInventory.getItem(slot);
        else
            getCache().channelConfiguration.itemStack = ItemStack.EMPTY;
        getCache().channelConfiguration.assignCurrentChannels(ItemChannelManager.getManager(), player);
        getCache().serialize(buffer, items);
    }

    @Override
    protected void refreshFilter(Direction dir)
    {
        int index = SwiftUtils.dirToIndex(dir);
        UpgradeInventory inventory = m_sideUpgradeInventories[index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IItemFilterUpgradeItem)
                {
                    Filter<ItemStack> filter = ((IItemFilterUpgradeItem) stack.getItem()).createItemFilter(stack);
                    m_filters[index] = filter;
                    return;
                }
            }
        }

        m_filters[index] = null;
    }

    @Override
    protected IItemHandler getHandler(TileEntity tileEntity, Direction dir)
    {
        return SwiftUtils.getItemHandler(tileEntity, dir);
    }

    @Override
    protected int getSize(IItemHandler handler)
    {
        return handler.getSlots();
    }

    @Override
    protected int transfer(IItemHandler extractHandler, int extractSlot, IItemHandler insertHandler, int insertSlot, ItemStack stack, int numToTransfer)
    {
        ItemStack extractedStack = extractHandler.extractItem(extractSlot, numToTransfer, false);
        stack = insertHandler.insertItem(insertSlot, extractedStack, false);

        return extractedStack.getCount();
    }

    @Override
    protected int simulateInsertion(IItemHandler insertHandler, int insertSlot, ItemStack stack)
    {
        ItemStack simulation = insertHandler.insertItem(insertSlot, stack, true);
        return stack.getCount() - simulation.getCount();
    }

    @Override
    protected ItemStack getStack(IItemHandler handler, int slot)
    {
        return handler.getStackInSlot(slot);
    }

    @Override
    protected int getCount(ItemStack stack)
    {
        return stack.getCount();
    }

    @Override
    protected boolean isEmpty(ItemStack stack)
    {
        return stack.isEmpty();
    }

    @Override
    protected OwnerBasedChannelManager<ChannelData> getChannelManager()
    {
        return ItemChannelManager.getManager();
    }

    @Override
    protected TransferQuantity getTransferQuantity(int stacks)
    {
        if (stacks <= 0)
            return new TransferQuantity(false, 1);
        else
            return new TransferQuantity(true, stacks);
    }
}
