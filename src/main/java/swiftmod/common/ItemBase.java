package swiftmod.common;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemBase extends Item
{
    public ItemBase(Item.Properties properties)
    {
        super(properties);
    }

    public ItemBase(int stackSize)
    {
        super(new Item.Properties().stacksTo(stackSize));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        addStandardInformation(stack, context, tooltip, flag);
        if (hasShiftInformation())
        {
            if (Screen.hasShiftDown())
                addShiftInformation(stack, context, tooltip, flag);
            else
                tooltip.add(Component.literal("\u00A7bHold \u00A7b\u00A76shift\u00A76\u00A7b for more info.\u00A7b"));
        }
    }

    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
    }

    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
    }
    
    public boolean hasShiftInformation()
    {
        return false;
    }
}
