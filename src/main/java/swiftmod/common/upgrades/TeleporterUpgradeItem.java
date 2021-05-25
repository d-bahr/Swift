package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.ChannelSpec;

public class TeleporterUpgradeItem extends UpgradeItem
{
    public TeleporterUpgradeItem()
    {
        super(UpgradeType.TeleportUpgrade, REGISTRY_NAME);
    }

    protected TeleporterUpgradeItem(UpgradeType t, String n)
    {
        super(t, n);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                TeleporterUpgradeContainer::createContainerServerSide, TeleporterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Transport over any distance within the dimension.", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Can only be used in an ultimate-tier pipe.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        ChannelSpec spec = ChannelConfigurationDataCache.getChannel(stack);
        if (spec == null || spec.name.isEmpty())
        {
            tooltip.add(new StringTextComponent(SwiftTextUtils.color("Channel not set.", SwiftTextUtils.PINK)));
        }
        else
        {
            tooltip.add(new StringTextComponent(SwiftTextUtils.color("Type: " + (spec.owner.isPrivate() ? "Private" : "Public"), SwiftTextUtils.AQUA)));
            tooltip.add(new StringTextComponent(SwiftTextUtils.color("Channel: " + spec.name, SwiftTextUtils.AQUA)));
        }
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "teleporter_upgrade";
    public static final String NBT_TAG = SwiftUtils.tagName("channel");
}
