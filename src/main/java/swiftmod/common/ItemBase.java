package swiftmod.common;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ItemBase extends Item
{
    public ItemBase(Item.Properties properties)
    {
        super(properties);
    }

    public ItemBase(int stackSize, ItemGroup group)
    {
        super(new Item.Properties().stacksTo(stackSize).tab(group));
    }

    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        addStandardInformation(stack, world, tooltip, flag);
        if (hasShiftInformation())
        {
            if (SwiftKeyBindings.isShiftKeyPressed())
                addShiftInformation(stack, world, tooltip, flag);
            else
                tooltip.add(new StringTextComponent("\u00A7bHold \u00A7b\u00A76shift\u00A76\u00A7b for more info.\u00A7b"));
        }
    }

    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
    }

    public void addShiftInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
    }
    
    public boolean hasShiftInformation()
    {
        return false;
    }
}
