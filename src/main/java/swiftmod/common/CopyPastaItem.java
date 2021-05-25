package swiftmod.common;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import swiftmod.pipes.PipeTileEntity;

public class CopyPastaItem extends ItemBase
{
    public enum CopyType
    {
        CopyLikeToLike,
        CopyBase,
        CopySingleDirection,
        CopyAllDirections,
        CopyAll
    }

    public CopyPastaItem()
    {
        super(1, Swift.ITEM_GROUP);
        setRegistryName(Swift.MOD_NAME, REGISTRY_NAME);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity instanceof PipeTileEntity)
            return true;
        else
            return false;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide)
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);

        if (player.isShiftKeyDown())
        {
            itemStack.removeTagKey(NBT_TAG);
            player.displayClientMessage(new StringTextComponent("Cleared"), true);
        }

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Copies settings from one pipe to another.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        CopyType type = getCopyType(stack);
        String str = copyTypeToString(type);

        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Current mode: " + str, SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Shift + Right Click = Copy", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Right Click = Paste", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Shift + Right Click Air = Clear", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static boolean copyTileEntitySettings(ItemStack itemStack, PipeTileEntity<?,?,?> tileEntity, Direction dir)
    {
        itemStack.removeTagKey(NBT_TAG);
        CompoundNBT nbt = itemStack.getOrCreateTagElement(NBT_TAG);
        return tileEntity.copyTileEntityUpgrades(nbt, dir, getCopyType(itemStack));
    }

    public static boolean pasteTileEntitySettings(ItemStack itemStack, PipeTileEntity<?,?,?> tileEntity, Direction dir)
    {
        CompoundNBT nbt = itemStack.getTagElement(NBT_TAG);
        if (nbt == null)
            return false;
        else
            return tileEntity.pasteTileEntityUpgrades(nbt, dir, getCopyType(itemStack));
    }

    public static void incrementCopyType(ItemStack itemStack)
    {
        CompoundNBT nbt = itemStack.getOrCreateTagElement(ITEM_NBT_TAG);
        int value = nbt.getInt(SwiftUtils.tagName("value"));
        CopyType type = intToCopyType(value);

        switch (type)
        {
        default:
        case CopyLikeToLike:
            type = CopyType.CopyBase;
            break;
        case CopyBase:
            type = CopyType.CopySingleDirection;
            break;
        case CopySingleDirection:
            type = CopyType.CopyAllDirections;
            break;
        case CopyAllDirections:
            type = CopyType.CopyAll;
            break;
        case CopyAll:
            type = CopyType.CopyLikeToLike;
            break;
        }

        nbt.putInt(SwiftUtils.tagName("value"), copyTypeToInt(type));
    }

    public static void decrementCopyType(ItemStack itemStack)
    {
        CompoundNBT nbt = itemStack.getOrCreateTagElement(ITEM_NBT_TAG);
        int value = nbt.getInt(SwiftUtils.tagName("value"));
        CopyType type = intToCopyType(value);

        switch (type)
        {
        default:
        case CopyLikeToLike:
            type = CopyType.CopyAll;
            break;
        case CopyBase:
            type = CopyType.CopyLikeToLike;
            break;
        case CopySingleDirection:
            type = CopyType.CopyBase;
            break;
        case CopyAllDirections:
            type = CopyType.CopySingleDirection;
            break;
        case CopyAll:
            type = CopyType.CopyAllDirections;
            break;
        }

        nbt.putInt(SwiftUtils.tagName("value"), copyTypeToInt(type));
    }
    
    public static void messageCurrentType(PlayerEntity player)
    {
        CopyType type = getCopyType(player.getItemInHand(Hand.MAIN_HAND));
        String str = copyTypeToString(type);
        player.displayClientMessage(new StringTextComponent("Mode: " + str), true);
    }

    public static CopyType getCopyType(ItemStack stack)
    {
        CompoundNBT nbt = stack.getOrCreateTagElement(ITEM_NBT_TAG);
        int value = nbt.getInt(SwiftUtils.tagName("value"));
        return intToCopyType(value);
    }
    
    public static String copyTypeToString(CopyType type)
    {
        switch (type)
        {
        default:
        case CopyLikeToLike:
            return "Copy like to like";
        case CopyBase:
            return "Copy base only";
        case CopySingleDirection:
            return "Copy single direction only";
        case CopyAllDirections:
            return "Copy all directions";
        case CopyAll:
            return "Copy all";
        }
    }

    private static CopyType intToCopyType(int i)
    {
        switch (i)
        {
        case 0:
        default:
            return CopyType.CopyLikeToLike;
        case 1:
            return CopyType.CopyBase;
        case 2:
            return CopyType.CopySingleDirection;
        case 3:
            return CopyType.CopyAllDirections;
        case 4:
            return CopyType.CopyAll;
        }
    }

    private static int copyTypeToInt(CopyType c)
    {
        switch (c)
        {
        default:
        case CopyLikeToLike:
            return 0;
        case CopyBase:
            return 1;
        case CopySingleDirection:
            return 2;
        case CopyAllDirections:
            return 3;
        case CopyAll:
            return 4;
        }
    }

    public static final String REGISTRY_NAME = "copy_pasta";

    public static final String ITEM_NBT_TAG = SwiftUtils.tagName("copy_type");
    public static final String NBT_TAG = SwiftUtils.tagName("copy");
}
