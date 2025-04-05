package swiftmod.common;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import swiftmod.pipes.PipeTileEntity;

public class CopyPastaItem extends ItemBase
{
    public CopyPastaItem()
    {
        super(1);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player)
    {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity instanceof PipeTileEntity)
            return true;
        else
            return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide)
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);

        if (player.isShiftKeyDown())
        {
        	PipeTileEntity.removeTileEntityUpgrades(itemStack);
            player.displayClientMessage(Component.literal("Cleared"), true);
        }

        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Copies settings from one pipe to another.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Shift + Right Click = Copy", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Right Click = Paste", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Shift + Right Click Air = Clear", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static boolean copyTileEntitySettings(ItemStack itemStack, PipeTileEntity blockEntity, Direction dir)
    {
        return blockEntity.copyTileEntityUpgrades(itemStack, dir);
    }

    public static boolean pasteTileEntitySettings(ItemStack itemStack, PipeTileEntity blockEntity, Direction dir)
    {
    	return blockEntity.pasteTileEntityUpgrades(itemStack, dir);
    }

    public static final String REGISTRY_NAME = "copy_pasta";
}
