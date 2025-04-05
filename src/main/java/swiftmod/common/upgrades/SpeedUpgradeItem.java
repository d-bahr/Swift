package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import swiftmod.common.SwiftTextUtils;

public class SpeedUpgradeItem extends UpgradeItem
{
    public SpeedUpgradeItem()
    {
        super(UpgradeType.SpeedUpgrade);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Shortens extraction speed by one tick per upgrade.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "speed_upgrade";
}
