package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.SwiftTextUtils;

public class StackUpgradeItem extends UpgradeItem
{
    public StackUpgradeItem()
    {
        super(UpgradeType.StackUpgrade, REGISTRY_NAME);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Increases amount of items or fuilds transfered.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addSneakInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Items: 1 stack per upgrade", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Fluids: 1 bucket per upgrade", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasSneakInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "stack_upgrade";
}
