package swiftmod.common;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class TankItem extends BlockItem
{
    public TankItem()
    {
        super(SwiftBlocks.s_tankBlock, new Item.Properties().stacksTo(64).tab(Swift.ITEM_GROUP));
        setRegistryName(Swift.MOD_NAME, REGISTRY_NAME);
    }

    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
    	String contents = "Empty";
        if (stack.hasTag())
        {
	        FluidStack fluid = FluidTank.readFluidStack(stack.getTag().getCompound(NBT_TAG));
	        if (fluid != null && !fluid.isEmpty())
	        {
	            contents = Integer.toString(fluid.getAmount()) + " mb "
	                    + fluid.getDisplayName().getString();
	        }
	    }

        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Contents: " + contents, SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "tank";

    public static final String NBT_TAG = SwiftUtils.tagName("tank");
}
