package swiftmod.common;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
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
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
    	Level level = context.getLevel();
    	BlockEntity entity = level.getBlockEntity(context.getClickedPos());
    	if (entity instanceof PipeTileEntity<?,?,?>)
    	{
    		PipeTileEntity<?,?,?> pipeEntity = (PipeTileEntity<?,?,?>) entity;

	    	Player player = context.getPlayer();
	    	Direction dir = context.getClickedFace();
	    	
	        if (player.isShiftKeyDown())
	        {
	            if (CopyPastaItem.copyTileEntitySettings(stack, pipeEntity, dir))
	            {
	                player.displayClientMessage(new TextComponent("Copied"), true);
	                return InteractionResult.SUCCESS;
	            }
	            else
	            {
	                player.displayClientMessage(new TextComponent("Cannot copy"), true);
	                return InteractionResult.SUCCESS;
	            }
	        }
	        else
	        {
	            if (CopyPastaItem.pasteTileEntitySettings(stack, pipeEntity, dir))
	            {
	                player.displayClientMessage(new TextComponent("Pasted"), true);
	                return InteractionResult.SUCCESS;
	            }
	            else
	            {
	                player.displayClientMessage(new TextComponent("Cannot paste"), true);
	                return InteractionResult.SUCCESS;
	            }
	        }
    	}
    	return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide)
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);

        if (player.isShiftKeyDown())
        {
            itemStack.removeTagKey(NBT_TAG);
            player.displayClientMessage(new TextComponent("Cleared"), true);
        }

        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Copies settings from one pipe to another.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        CopyType type = getCopyType(stack);
        String str = copyTypeToString(type);

        tooltip.add(new TextComponent(SwiftTextUtils.color("Current mode: " + str, SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Shift + Right Click = Copy", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Right Click = Paste", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Shift + Right Click Air = Clear", SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static boolean copyTileEntitySettings(ItemStack itemStack, PipeTileEntity<?,?,?> blockEntity, Direction dir)
    {
        itemStack.removeTagKey(NBT_TAG);
        CompoundTag nbt = itemStack.getOrCreateTagElement(NBT_TAG);
        return blockEntity.copyTileEntityUpgrades(nbt, dir, getCopyType(itemStack));
    }

    public static boolean pasteTileEntitySettings(ItemStack itemStack, PipeTileEntity<?,?,?> blockEntity, Direction dir)
    {
        CompoundTag nbt = itemStack.getTagElement(NBT_TAG);
        if (nbt == null)
            return false;
        else
            return blockEntity.pasteTileEntityUpgrades(nbt, dir, getCopyType(itemStack));
    }

    public static void incrementCopyType(ItemStack itemStack)
    {
        CompoundTag nbt = itemStack.getOrCreateTagElement(ITEM_NBT_TAG);
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
        CompoundTag nbt = itemStack.getOrCreateTagElement(ITEM_NBT_TAG);
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
    
    public static void messageCurrentType(Player player)
    {
        CopyType type = getCopyType(player.getItemInHand(InteractionHand.MAIN_HAND));
        String str = copyTypeToString(type);
        player.displayClientMessage(new TextComponent("Mode: " + str), true);
    }

    public static CopyType getCopyType(ItemStack stack)
    {
        CompoundTag nbt = stack.getOrCreateTagElement(ITEM_NBT_TAG);
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
