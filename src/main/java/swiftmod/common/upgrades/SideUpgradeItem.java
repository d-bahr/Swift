package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import swiftmod.common.Color;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.SwiftUtils;

public class SideUpgradeItem extends UpgradeItem
{
    public SideUpgradeItem()
    {
        super(UpgradeType.SideUpgrade, REGISTRY_NAME);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                SideUpgradeContainer::createContainerServerSide, SideUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Allows inserting or extracting from any", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("face of an attached inventory.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        byte[] states = SideUpgradeDataCache.getStates(stack);
        if (states.length == 0)
        {
            states = new byte[Direction.values().length];
            for (int i = 0; i < states.length; ++i)
                states[i] = 0;
        }
        tooltip.add(getTooltip("North: ", states[SwiftUtils.dirToIndex(Direction.NORTH)]));
        tooltip.add(getTooltip("South: ", states[SwiftUtils.dirToIndex(Direction.SOUTH)]));
        tooltip.add(getTooltip("West: ", states[SwiftUtils.dirToIndex(Direction.WEST)]));
        tooltip.add(getTooltip("East: ", states[SwiftUtils.dirToIndex(Direction.EAST)]));
        tooltip.add(getTooltip("Up: ", states[SwiftUtils.dirToIndex(Direction.UP)]));
        tooltip.add(getTooltip("Down: ", states[SwiftUtils.dirToIndex(Direction.DOWN)]));
    }

    private Component getTooltip(String direction, byte state)
    {
        if (state == 0)
        {
            return new TextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color("Disabled", SwiftTextUtils.RED));
        }
        else if (state == 17)
        {
            return new TextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color("Any", SwiftTextUtils.WHITE));
        }
        else
        {
            Color c = Color.fromIndex((int)state);
            return new TextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color(c.getName(), c));
        }
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "side_upgrade";

    public static final String NBT_TAG = SwiftUtils.tagName("sides");
}
