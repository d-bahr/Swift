package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.gui.GuiWidget;

@OnlyIn(Dist.CLIENT)
public class AdvancedFluidPipeContainerScreen extends FluidPipeContainerScreen<AdvancedFluidPipeContainer>
{
    public AdvancedFluidPipeContainerScreen(AdvancedFluidPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
    }

	@Override
	protected GuiWidget initBasicFilterWidget()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void showBasicFilterWidget()
	{
		// TODO Auto-generated method stub
	}
}
