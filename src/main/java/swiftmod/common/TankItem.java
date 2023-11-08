package swiftmod.common;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class TankItem extends BlockItem
{
    public TankItem()
    {
        super(SwiftBlocks.s_tankBlock.get(), new Item.Properties().stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
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

        tooltip.add(Component.literal(SwiftTextUtils.color("Contents: " + contents, SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "tank";

    public static final String NBT_TAG = SwiftUtils.tagName("tank");
}
