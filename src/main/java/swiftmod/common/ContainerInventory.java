package swiftmod.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import swiftmod.common.gui.SwiftGui;

public class ContainerInventory implements IInventory
{
    @FunctionalInterface
    public interface SlotStackPredicate
    {
        public boolean test(ContainerInventory inventory, int slot, ItemStack stack);
    }

    public ContainerInventory(int size)
    {
        m_canPlayerAccessInventoryCallback = x -> true;
        m_canInsertItemCallback = (inventory, slot, stack) -> m_contents.isItemValid(slot, stack);
        m_contentsChangedCallback = (inventory) -> {};
        m_markDirtyCallback = () -> {};
        m_openInventoryCallback = () -> {};
        m_closeInventoryCallback = () -> {};
        m_contents = new ContainerItemStackHandler(size);
    }

    public ContainerInventory(int size, SlotStackPredicate predicate)
    {
        m_canPlayerAccessInventoryCallback = x -> true;
        m_canInsertItemCallback = predicate;
        m_contentsChangedCallback = (inventory) -> {};
        m_markDirtyCallback = () -> {};
        m_openInventoryCallback = () -> {};
        m_closeInventoryCallback = () -> {};
        m_contents = new ContainerItemStackHandler(size);
    }

    public ContainerInventory(ContainerItemStackHandler handler)
    {
        m_canPlayerAccessInventoryCallback = x -> true;
        m_canInsertItemCallback = (inventory, slot, stack) -> m_contents.isItemValid(slot, stack);
        m_contentsChangedCallback = (inventory) -> {};
        m_markDirtyCallback = () -> {};
        m_openInventoryCallback = () -> {};
        m_closeInventoryCallback = () -> {};
        m_contents = handler;
    }

    public CompoundNBT serializeNBT()
    {
        return m_contents.serializeNBT();
    }

    public void deserializeNBT(CompoundNBT nbt)
    {
        m_contents.deserializeNBT(nbt);
    }

    public void setCanPlayerAccessInventoryCallback(Predicate<PlayerEntity> callback)
    {
        m_canPlayerAccessInventoryCallback = callback;
    }

    public void setCanInsertItemCallback(SlotStackPredicate callback)
    {
        m_canInsertItemCallback = callback;
    }

    public void setContentsChangedCallback(Consumer<ContainerInventory> callback)
    {
        m_contentsChangedCallback = callback;
    }

    public void setMarkDirtyCallback(Notification callback)
    {
        m_markDirtyCallback = callback;
    }

    public void setOpenInventoryCallback(Notification callback)
    {
        m_openInventoryCallback = callback;
    }

    public void setCloseInventoryCallback(Notification callback)
    {
        m_closeInventoryCallback = callback;
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return m_canPlayerAccessInventoryCallback.test(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return m_canInsertItemCallback.test(this, index, stack);
    }

    @Override
    public void setChanged()
    {
        m_markDirtyCallback.invoke();
    }

    @Override
    public void startOpen(PlayerEntity player)
    {
        m_openInventoryCallback.invoke();
    }

    @Override
    public void stopOpen(PlayerEntity player)
    {
        m_closeInventoryCallback.invoke();
    }

    @Override
    public int getContainerSize()
    {
        return m_contents.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < m_contents.getSlots(); ++i)
        {
            if (!m_contents.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return m_contents.getStackInSlot(index);
    }

    public ItemStack incrStackSize(int index, ItemStack itemStackToInsert)
    {
        ItemStack x = m_contents.insertItem(index, itemStackToInsert, false);
        if (x.getCount() != itemStackToInsert.getCount())
            m_contentsChangedCallback.accept(this);
        return x;
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        if (count < 0)
            throw new IllegalArgumentException("count should be >= 0: " + count);
        ItemStack x = m_contents.extractItem(index, count, false);
        if (x.getCount() > 0)
            m_contentsChangedCallback.accept(this);
        return x;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        int maxPossibleItemStackSize = m_contents.getSlotLimit(index);
        ItemStack x = m_contents.extractItem(index, maxPossibleItemStackSize, false);
        if (x.getCount() > 0)
            m_contentsChangedCallback.accept(this);
        return x;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        m_contents.setStackInSlot(index, stack);
        m_contentsChangedCallback.accept(this);
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < m_contents.getSlots(); ++i)
            m_contents.setStackInSlot(i, ItemStack.EMPTY);
        m_contentsChangedCallback.accept(this);
    }

    public ItemStack tryInsert(int slot, ItemStack stack, boolean simulate)
    {
        return m_contents.insertItem(slot, stack, simulate);
    }

    public int getStackLimit(int slot, ItemStack stack)
    {
        return m_contents.getStackLimit(slot, stack);
    }

    public SlotBase[] createSlots(int x, int y, int rows, int columns)
    {
        return createSlots(x, y, rows, columns, SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT);
    }

    public SlotBase[] createSlots(int x, int y, int rows, int columns, int widthPerColumn, int heightPerRow)
    {
        int numSlots = Math.min(rows * columns, getContainerSize());
        SlotBase[] slots = new SlotBase[numSlots];
        if (numSlots == 0)
            return slots;

        for (int r = 0; r < rows; ++r)
        {
            for (int c = 0; c < columns; ++c)
            {
                int i = r * columns + c;
                slots[i] = new SlotBase(this, i, x + widthPerColumn * c,
                        y + heightPerRow * r);
                
                --numSlots;
                if (numSlots <= 0)
                    return slots;
            }
        }
        return slots;
    }

    private Predicate<PlayerEntity> m_canPlayerAccessInventoryCallback;
    private SlotStackPredicate m_canInsertItemCallback;
    private Consumer<ContainerInventory> m_contentsChangedCallback;
    private Notification m_markDirtyCallback;
    private Notification m_openInventoryCallback;
    private Notification m_closeInventoryCallback;
    protected ContainerItemStackHandler m_contents;
}
