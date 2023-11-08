package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.ChannelSpec;

public class TeleporterUpgradeItem extends UpgradeItem
{
    public TeleporterUpgradeItem()
    {
        super(UpgradeType.TeleportUpgrade);
    }

    protected TeleporterUpgradeItem(UpgradeType t)
    {
        super(t);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                TeleporterUpgradeContainer::createContainerServerSide, TeleporterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Transport over any distance within the dimension.", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Can only be used in an ultimate-tier pipe.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        ChannelSpec spec = ChannelConfigurationDataCache.getChannel(stack);
        if (spec == null || spec.name.isEmpty())
        {
            tooltip.add(Component.literal(SwiftTextUtils.color("Channel not set.", SwiftTextUtils.PINK)));
        }
        else
        {
            tooltip.add(Component.literal(SwiftTextUtils.color("Type: " + (spec.owner.isPrivate() ? "Private" : "Public"), SwiftTextUtils.AQUA)));
            tooltip.add(Component.literal(SwiftTextUtils.color("Channel: " + spec.name, SwiftTextUtils.AQUA)));
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
