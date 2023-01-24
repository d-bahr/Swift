package swiftmod.common;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemBase extends Item
{
    public ItemBase(Item.Properties properties)
    {
        super(properties);
    }

    public ItemBase(int stackSize, CreativeModeTab group)
    {
        super(new Item.Properties().stacksTo(stackSize).tab(group));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        addStandardInformation(stack, world, tooltip, flag);
        if (hasShiftInformation())
        {
            if (Screen.hasShiftDown())
                addShiftInformation(stack, world, tooltip, flag);
            else
                tooltip.add(new TextComponent("\u00A7bHold \u00A7b\u00A76shift\u00A76\u00A7b for more info.\u00A7b"));
        }
    }

    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
    }

    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
    }
    
    public boolean hasShiftInformation()
    {
        return false;
    }
}
