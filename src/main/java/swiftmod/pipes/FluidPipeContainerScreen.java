package swiftmod.pipes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.SwiftUtils;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.gui.WildcardFilterWidget;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

@OnlyIn(Dist.CLIENT)
public abstract class FluidPipeContainerScreen<T extends FluidPipeContainer> extends PipeContainerScreen<T>
{
	public FluidPipeContainerScreen(T c, Inventory inv, Component title)
	{
		super(c, inv, title, SideIOConfigurationWidget.FilterType.Fluid);

        initBasicFilterConfigPanel();
        initWildcardFilterConfigPanel();
	}

    protected abstract GuiWidget initBasicFilterWidget();
	
	protected void initBasicFilterConfigPanel()
	{
        m_basicFilterConfigPanel.addChild(initBasicFilterWidget());
	}
	
    protected void initWildcardFilterConfigPanel()
    {
        m_wildcardFilterWidget = new WildcardFilterWidget(this, 0, 17, width() - BASE_PANEL_X * 2, 9);

        m_wildcardFilterWidget.setAddCallback(this::onFilterAdded);
        m_wildcardFilterWidget.setRemoveCallback(this::onFilterRemoved);

        m_wildcardFilterConfigPanel.addChild(m_wildcardFilterWidget);
    }
    
    @Override
    protected void showWildcardFilterWidget()
    {
        WildcardFilterUpgradeDataCache cache = getMenu().getWildcardFilterCache(m_selectedDirection);
        m_wildcardFilterWidget.setFilters(cache.getFilters());
        m_panelStack.push(m_wildcardFilterConfigPanel);
        m_wildcardFilterWidget.requestTextFieldFocus();
    }
    
    protected void onFilterAdded(String filter)
    {
        menu.addWildcardFilter(m_selectedDirection, filter);
    }

    protected void onFilterRemoved(String filter)
    {
        menu.removeWildcardFilter(m_selectedDirection, filter);
    }

    protected void onDeleteFilter()
    {
        menu.clearAllFilters(m_selectedDirection);
    }
    
    @Override
    protected int getCurrentIndex()
    {
    	return SwiftUtils.dirToIndex(m_selectedDirection);
    }
    
    protected WildcardFilterWidget m_wildcardFilterWidget;
}
