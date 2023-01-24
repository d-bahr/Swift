package swiftmod.common;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import swiftmod.common.gui.SwiftGui;

public abstract class PlayerInventoryContainer extends AbstractContainerMenu
{
    protected PlayerInventoryContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
            int x, int y)
    {
        super(type, windowID);
        m_numPlayerInventorySlots = playerInventory.items.size();
        setPlayerInventoryLocation(playerInventory, x, y);
    }

    private void setPlayerInventoryLocation(Inventory playerInventory, int x, int y)
    {
        // Add the hotbar to the GUI. Slots 0-8.
        for (int i = 0; i < Inventory.getSelectionSize(); ++i)
        {
            addSlot(new EnableableSlot(playerInventory, i, x + SwiftGui.INVENTORY_SLOT_WIDTH * i, y + HOTBAR_OFFSET_Y));
        }

        // Add the player inventory to the GUI. Slots 9-35.
        for (int r = 0; r < INVENTORY_NUM_ROWS; ++r)
        {
            for (int c = 0; c < INVENTORY_NUM_COLUMNS; ++c)
            {
                int i = Inventory.getSelectionSize() + r * INVENTORY_NUM_COLUMNS + c;
                addSlot(new EnableableSlot(playerInventory, i, x + SwiftGui.INVENTORY_SLOT_WIDTH * c,
                        y + SwiftGui.INVENTORY_SLOT_HEIGHT * r));
            }
        }
    }

    public void enableSlots(int start, int end, boolean enable)
    {
        if (start < 0)
            return;

        for (int i = start; i < end; ++i)
        {
            if (i >= slots.size())
                break;

            Slot s = slots.get(i);
            if (s instanceof EnableableSlot)
                ((EnableableSlot)s).enable = enable;
        }
    }

    public void enablePlayerInventorySlots(boolean enable)
    {
        enableSlots(0, m_numPlayerInventorySlots, enable);
    }

    protected int getNumSlots()
    {
        return slots.size();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    // This is called when the player shift-clicks a slot in the inventory screen.
    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex)
    {
        Slot sourceSlot = slots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack sourceItemStack = sourceSlot.getItem();
        ItemStack sourceStackBeforeMerge = sourceItemStack.copy();
        boolean successfulTransfer = false;

        if (Inventory.isHotbarSlot(sourceSlotIndex))
        {
            if (slots.size() > getPlayerInventorySize(player))
            {
                // Try moving to the container inventory first.
                successfulTransfer = moveItemStackTo(sourceItemStack, getPlayerInventorySize(player),
                        slots.size(), false);
            }

            if (!successfulTransfer)
            {
                // Move from the hotbar to the inventory instead.
                successfulTransfer = moveItemStackTo(sourceItemStack, Inventory.getSelectionSize(),
                		getPlayerInventorySize(player), false);
            }
        }
        else if (isInventorySlot(player.getInventory(), sourceSlotIndex))
        {
            if (slots.size() > getPlayerInventorySize(player))
            {
                // Try moving to the container inventory first.
                successfulTransfer = moveItemStackTo(sourceItemStack, getPlayerInventorySize(player),
                        slots.size(), false);
            }

            if (!successfulTransfer)
            {
                // Move from the inventory to the hotbar instead.
                successfulTransfer = moveItemStackTo(sourceItemStack, 0, Inventory.getSelectionSize(), false);
            }
        }
        else
        {
            // Try moving from the container inventory to the hotbar.
            successfulTransfer = moveItemStackTo(sourceItemStack, 0, Inventory.getSelectionSize(), false);

            // Move from the container inventory to the player inventory instead.
            if (!successfulTransfer)
            {
                successfulTransfer = moveItemStackTo(sourceItemStack, Inventory.getSelectionSize(),
                		getPlayerInventorySize(player), false);
            }
        }

        if (!successfulTransfer)
            return ItemStack.EMPTY;

        if (sourceItemStack.isEmpty())
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        if (sourceItemStack.getCount() == sourceStackBeforeMerge.getCount())
            return ItemStack.EMPTY;

        sourceSlot.onTake(player, sourceItemStack);
        return sourceStackBeforeMerge;
    }
    
    protected static int getPlayerInventorySize(Player player)
    {
        return player.getInventory().items.size();
    }

    public static boolean isInventorySlot(Inventory inventory, int slot)
    {
        return slot >= Inventory.getSelectionSize() && slot < inventory.items.size();
    }

    private final int m_numPlayerInventorySlots;

    protected static final int INVENTORY_NUM_ROWS = 3;
    protected static final int INVENTORY_NUM_COLUMNS = 9;
    protected static final int INVENTORY_NUM_SLOTS = INVENTORY_NUM_ROWS * INVENTORY_NUM_COLUMNS;

    protected static final int HOTBAR_OFFSET_Y = 58;
}
