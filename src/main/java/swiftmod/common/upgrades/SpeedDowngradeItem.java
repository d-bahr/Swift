package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.SwiftTextUtils;

public class SpeedDowngradeItem extends UpgradeItem
{
    public SpeedDowngradeItem()
    {
        super(UpgradeType.SpeedDowngrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Lengthens extraction speed by one second per upgrade.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "speed_downgrade";
}
