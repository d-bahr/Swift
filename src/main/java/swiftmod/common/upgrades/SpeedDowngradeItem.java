package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import swiftmod.common.SwiftTextUtils;

public class SpeedDowngradeItem extends UpgradeItem
{
    public SpeedDowngradeItem()
    {
        super(UpgradeType.SpeedDowngrade);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Lengthens extraction speed by one second per upgrade.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "speed_downgrade";
}
