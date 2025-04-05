package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.SwiftUtils;
import swiftmod.common.gui.GuiTab;
import swiftmod.common.gui.GuiTabGroup;
import swiftmod.common.gui.SideIOConfigurationWidget;

@OnlyIn(Dist.CLIENT)
public class AdvancedOmniPipeContainerScreen extends PipeContainerScreen<AdvancedOmniPipeContainer>
{
    public AdvancedOmniPipeContainerScreen(AdvancedOmniPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, SideIOConfigurationWidget.FilterType.Item);
        
        m_typeTabs = new GuiTabGroup(this, width(), 0, 27, height());
        m_typeTabs.addTab(new GuiTab(this, 27, 30));
        m_typeTabs.addTab(new GuiTab(this, 27, 30));
        m_typeTabs.addTab(new GuiTab(this, 27, 30));
        m_typeTabs.setTabSelectedHandler(this::onTypeTabChanged);
        m_typeTabs.setSelectedTab(ITEM_TAB, false);
    }

	@Override
	protected void showBasicFilterWidget()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void showWildcardFilterWidget()
	{
		// TODO Auto-generated method stub
	}
    
    public void onTypeTabChanged(GuiTab tab, int oldIndex, int newIndex)
    {
    	// Update channels.
    	if (newIndex == ITEM_TAB)
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.Item);
    	else if (newIndex == FLUID_TAB)
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.Fluid);
    	else
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.None);
    }
    
    @Override
    protected int getCurrentIndex()
    {
    	// TODO: Fix.
    	return SwiftUtils.dirToIndex(m_selectedDirection);
    }

    protected GuiTabGroup m_typeTabs;
    
    private static final int ITEM_TAB = 0;
    private static final int FLUID_TAB = 1;
    @SuppressWarnings("unused")
	private static final int ENERGY_TAB = 2;
}
