package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import swiftmod.common.SwiftTextUtils;

public class StackUpgradeItem extends UpgradeItem
{
    public StackUpgradeItem()
    {
        super(UpgradeType.StackUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Increases amount of items or fuilds transfered.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Items: 1 stack per upgrade", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Fluids: 1 bucket per upgrade", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "stack_upgrade";
}
