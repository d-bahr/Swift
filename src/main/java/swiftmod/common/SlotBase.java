package swiftmod.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.world.item.ItemStack;

public class SlotBase extends EnableableSlot
{
    public SlotBase(ContainerInventory inventory, int index, int xPosition, int yPosition)
    {
        super(inventory, index, xPosition, yPosition);
        m_predicate = (stack) -> this.container.canPlaceItem(getSlotIndex(), stack);
        m_changedCallback = null;
    }

    public SlotBase(ContainerInventory inventory, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate)
    {
        super(inventory, index, xPosition, yPosition);
        m_predicate = predicate;
        m_changedCallback = null;
    }

    public void setChangedCallback(Consumer<SlotBase> callback)
    {
        m_changedCallback = callback;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return m_predicate.test(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return ((ContainerInventory)container).getStackLimit(getSlotIndex(), stack);
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        if (m_changedCallback != null)
            m_changedCallback.accept(this);
    }

    private Predicate<ItemStack> m_predicate;
    private Consumer<SlotBase> m_changedCallback;
}
