package swiftmod.common;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

public class SlotBase extends EnableableSlot
{
    public SlotBase(ContainerInventory inventory, int index, int xPosition, int yPosition)
    {
        super(inventory, index, xPosition, yPosition);
        m_predicate = (stack) -> this.container.canPlaceItem(getSlotIndex(), stack);
    }

    public SlotBase(ContainerInventory inventory, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate)
    {
        super(inventory, index, xPosition, yPosition);
        m_predicate = predicate;
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

    private Predicate<ItemStack> m_predicate;
}
