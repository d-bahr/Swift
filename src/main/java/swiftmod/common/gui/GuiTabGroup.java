package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Group of selectable tabs
 */
@OnlyIn(Dist.CLIENT)
public class GuiTabGroup extends GuiWidget
{
    @FunctionalInterface
    public interface TabSelectedHandler
    {
        public void onTabSelected(GuiTab tab, int oldIndex, int newIndex);
    }
    
    public GuiTabGroup(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, 0, 0, width, height, null);
    }

    public GuiTabGroup(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, null);
    }
    
    public GuiTabGroup(GuiContainerScreen<?> screen, int width, int height, TabSelectedHandler handler)
    {
        this(screen, 0, 0, width, height, handler);
    }

    public GuiTabGroup(GuiContainerScreen<?> screen, int x, int y, int width, int height, TabSelectedHandler handler)
    {
        super(screen, x, y, width, height, Component.empty());
        m_tabs = new ArrayList<GuiTab>();
        m_selectedTab = -1;
        m_handler = handler;
    }
    
    public void setSelectedTab(GuiTab tab)
    {
    	setSelectedTab(tab, false);
    }
    
    public void setSelectedTab(GuiTab tab, boolean invokeHandler)
    {
    	int index = m_tabs.indexOf(tab);
    	if (index >= 0)
    		setSelectedTab(index, invokeHandler);
    }
    
    public void setSelectedTab(int index)
    {
    	setSelectedTab(index, false);
    }
    
    public void setSelectedTab(int index, boolean invokeHandler)
    {
    	if (m_selectedTab == index)
    		return;

    	if (m_selectedTab >= 0 && m_selectedTab < m_tabs.size())
    	{
    		m_tabs.get(m_selectedTab).setSelected(false, false);
    		m_tabs.get(m_selectedTab).setZ(Z_UNSELECTED);
    	}
    	
    	int oldIndex = m_selectedTab;
    	
    	if (index >= 0 && index < m_tabs.size())
    		m_selectedTab = index;
    	else
    		m_selectedTab = -1;
    	
    	if (m_selectedTab >= 0 && m_selectedTab < m_tabs.size())
    	{
    		m_tabs.get(m_selectedTab).setZ(Z_SELECTED);
    		m_tabs.get(m_selectedTab).setSelected(true, false);
    		if (m_handler != null && invokeHandler)
    			m_handler.onTabSelected(m_tabs.get(m_selectedTab), oldIndex, m_selectedTab);
    	}
    	else
    	{
    		if (m_handler != null && invokeHandler)
    			m_handler.onTabSelected(null, oldIndex, -1);
    	}
    }
    
    public int getSelectedTab()
    {
    	return m_selectedTab;
    }
    
    public void addTab(GuiTab tab)
    {
    	tab.setStateChangedHandler(this::onTabSelected);
    	tab.setX(0);
    	if (m_tabs.size() > 0)
    		tab.setY(m_tabs.get(m_tabs.size() - 1).bottom() - VERTICAL_OVERLAP);
    	else
    		tab.setY(0);
    	m_tabs.add(tab);
    	addChild(tab);
    }
    
    public void addTab(GuiTab tab, int index)
    {
    	tab.setStateChangedHandler(this::onTabSelected);
    	
    	if (index > m_tabs.size())
    		index = m_tabs.size();
    	else if (index < 0)
    		index = 0;
    	
    	if (index > 0)
    		tab.setY(m_tabs.get(index).getY());
    	else
    		tab.setY(0);
    	
    	m_tabs.add(index, tab);
    	addChild(tab);
    	
    	// Shift tabs down.
    	for (int i = index + 1; i < m_tabs.size(); ++i)
    		m_tabs.get(i).setY(m_tabs.get(i - 1).bottom() - VERTICAL_OVERLAP);
    	
    	if (index <= m_selectedTab)
    		m_selectedTab++;
    }
    
    public GuiTab getTab(int index)
    {
    	return m_tabs.get(index);
    }
    
    public void removeTab(GuiTab tab)
    {
    	removeTab(m_tabs.indexOf(tab));
    }
    
    public void removeTab(int index)
    {
    	if (index >= 0 && index < m_tabs.size())
    	{
    		if (index == m_selectedTab)
    		{
        		m_tabs.get(m_selectedTab).setZ(Z_UNSELECTED);
    			m_selectedTab = -1;
    		}
    		else if (index < m_selectedTab)
    		{
    			m_selectedTab--;
    		}

    		m_tabs.get(index).setStateChangedHandler(null);
        	GuiTab removedTab = m_tabs.remove(index);
        	removeChild(removedTab);
        	
        	if (m_tabs.size() > 0)
        	{
	        	// Shift tabs up.
	        	if (index == 0)
	        	{
	        		m_tabs.get(0).setY(0);
	        		index++;
	        	}
	        	for (int i = index; i < m_tabs.size(); ++i)
	        		m_tabs.get(i).setY(m_tabs.get(i - 1).bottom() - VERTICAL_OVERLAP);
        	}
    	}
    }
    
    public void setTabSelectedHandler(TabSelectedHandler handler)
    {
    	m_handler = handler;
    }
    
    private void onTabSelected(GuiBooleanStateButton button, boolean state)
    {
    	if (state == true)
    		setSelectedTab((GuiTab)button, true);
    }

    protected List<GuiTab> m_tabs;
    protected int m_selectedTab;
    protected TabSelectedHandler m_handler;
    
    private static final int Z_SELECTED = 10;
    private static final int Z_UNSELECTED = 0;
    private static final int VERTICAL_OVERLAP = 1; // One pixel overlap makes it look nice.
}
