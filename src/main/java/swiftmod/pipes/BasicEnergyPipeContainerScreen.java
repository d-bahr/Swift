package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.SwiftUtils;
import swiftmod.common.gui.SideIOConfigurationWidget;

@OnlyIn(Dist.CLIENT)
public class BasicEnergyPipeContainerScreen extends PipeContainerScreen<BasicEnergyPipeContainer>
{
    public BasicEnergyPipeContainerScreen(BasicEnergyPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, SideIOConfigurationWidget.FilterType.None);
    }

	@Override
	protected void showBasicFilterWidget()
	{
	}

	@Override
	protected void showWildcardFilterWidget()
	{
	}
    
    @Override
    protected int getCurrentIndex()
    {
    	return SwiftUtils.dirToIndex(m_selectedDirection);
    }
}
