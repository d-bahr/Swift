package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.SwiftTextUtils;

public class UltimateStackUpgradeItem extends UpgradeItem
{
    public UltimateStackUpgradeItem()
    {
        super(UpgradeType.UltimateStackUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Transfers an entire inventory at once.", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Can only be used in an ultimate-tier pipe.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "ultimate_stack_upgrade";
}
