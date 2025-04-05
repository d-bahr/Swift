package swiftmod.common;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.pipes.PipeTileEntity;

public class WrenchItem extends ItemBase
{
    public WrenchItem()
    {
        super(64);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player)
    {
        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof PipeTileEntity;
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Disconnects pipes from adjacent pipes or inventories.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Right Click = Disconnect/Reconnect", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Shift + Right Click = Disassemble", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "wrench";
}
