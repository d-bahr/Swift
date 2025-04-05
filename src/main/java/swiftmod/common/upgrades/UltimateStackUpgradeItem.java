package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import swiftmod.common.SwiftTextUtils;

public class UltimateStackUpgradeItem extends UpgradeItem
{
    public UltimateStackUpgradeItem()
    {
        super(UpgradeType.UltimateStackUpgrade);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Transfers an entire inventory at once.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "ultimate_stack_upgrade";
}
