package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.SwiftTextUtils;

public class SpeedUpgradeItem extends UpgradeItem
{
    public SpeedUpgradeItem()
    {
        super(UpgradeType.SpeedUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Shortens extraction speed by one tick per upgrade.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "speed_upgrade";
}
