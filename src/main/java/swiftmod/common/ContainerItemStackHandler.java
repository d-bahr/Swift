package swiftmod.common;

import javax.annotation.Nonnull;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ContainerItemStackHandler extends ItemStackHandler
{
    public ContainerItemStackHandler()
    {
        super();
    }

    public ContainerItemStackHandler(int size)
    {
        super(size);
    }

    public ContainerItemStackHandler(NonNullList<ItemStack> stacks)
    {
        super(stacks);
    }

    /*
     * Only purpose of this is to turn the protected function into a public one
     * for use with slots on the container side.
     */
    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        return super.getStackLimit(slot, stack);
    }
}
