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
public abstract class ItemPipeContainerScreen<T extends ItemPipeContainer> extends PipeContainerScreen<T>
{
	public ItemPipeContainerScreen(T c, Inventory inv, Component title)
	{
		super(c, inv, title, SideIOConfigurationWidget.FilterType.Item);

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
        WildcardFilterUpgradeDataCache cache = getMenu().getWildcardFilterCache(getCurrentIndex());
        m_wildcardFilterWidget.setFilters(cache.getFilters());
        m_panelStack.push(m_wildcardFilterConfigPanel);
        m_wildcardFilterWidget.requestTextFieldFocus();
    }
    
    protected void onFilterAdded(String filter)
    {
        menu.addWildcardFilter(getCurrentIndex(), filter);
    }

    protected void onFilterRemoved(String filter)
    {
        menu.removeWildcardFilter(getCurrentIndex(), filter);
    }

    protected void onDeleteFilter()
    {
        menu.clearAllFilters(getCurrentIndex());
    }
    
    @Override
    protected int getCurrentIndex()
    {
    	return SwiftUtils.dirToIndex(m_selectedDirection);
    }
    
    protected WildcardFilterWidget m_wildcardFilterWidget;
}
