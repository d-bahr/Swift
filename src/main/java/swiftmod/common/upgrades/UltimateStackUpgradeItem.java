package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import swiftmod.common.SwiftTextUtils;

public class UltimateStackUpgradeItem extends UpgradeItem
{
    public UltimateStackUpgradeItem()
    {
        super(UpgradeType.UltimateStackUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Transfers an entire inventory at once.", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Can only be used in an ultimate-tier pipe.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "ultimate_stack_upgrade";
}
