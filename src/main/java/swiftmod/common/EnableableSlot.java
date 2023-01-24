package swiftmod.common;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Very light wrapper around Slot class just to allow custom enabling/disabling.
 *
 */
public class EnableableSlot extends Slot
{
    public EnableableSlot(Container inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
        enable = true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isActive()
    {
        return enable;
    }

    public boolean enable;
}
