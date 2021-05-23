package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
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
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                SideUpgradeContainer::createContainerServerSide, SideUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Allows inserting or extracting from any", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("face of an attached inventory.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addSneakInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
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

    private ITextComponent getTooltip(String direction, byte state)
    {
        if (state == 0)
        {
            return new StringTextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color("Disabled", SwiftTextUtils.RED));
        }
        else if (state == 17)
        {
            return new StringTextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color("Any", SwiftTextUtils.WHITE));
        }
        else
        {
            Color c = Color.fromIndex((int)state);
            return new StringTextComponent(SwiftTextUtils.color(direction, SwiftTextUtils.AQUA)
                    + SwiftTextUtils.color(c.getName(), c));
        }
    }

    @Override
    public boolean hasSneakInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "side_upgrade";

    public static final String NBT_TAG = SwiftUtils.tagName("sides");
}
