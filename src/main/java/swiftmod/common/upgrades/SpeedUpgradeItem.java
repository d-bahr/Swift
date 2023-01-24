package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import swiftmod.common.SwiftTextUtils;

public class SpeedUpgradeItem extends UpgradeItem
{
    public SpeedUpgradeItem()
    {
        super(UpgradeType.SpeedUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Shortens extraction speed by one tick per upgrade.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "speed_upgrade";
}
