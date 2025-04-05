package swiftmod.common;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

public class TankItem extends BlockItem
{
    public TankItem()
    {
        super(SwiftBlocks.s_tankBlock.get(), new Item.Properties().stacksTo(64).component(SwiftDataComponents.FLUID_STACK_DATA_COMPONENT, new ImmutableFluidStack(FluidStack.EMPTY)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
    	String contents = "Empty";
    	FluidStack fluid = stack.get(SwiftDataComponents.FLUID_STACK_DATA_COMPONENT).fluidStack();
        if (fluid != null && !fluid.isEmpty())
        {
            contents = Integer.toString(fluid.getAmount()) + " mb "
                    + fluid.getHoverName().getString();
        }

        tooltip.add(Component.literal(SwiftTextUtils.color("Contents: " + contents, SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "tank";
}
