package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Color;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TransferDirection;
import swiftmod.common.WhiteListState;
import swiftmod.common.gui.BasicFluidFilterWidget;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.GuiTab;
import swiftmod.common.gui.GuiTabGroup;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.gui.WildcardFilterWidget;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

@OnlyIn(Dist.CLIENT)
public class BasicOmniPipeContainerScreen extends PipeContainerScreen<BasicOmniPipeContainer>
{
    public BasicOmniPipeContainerScreen(BasicOmniPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, SideIOConfigurationWidget.FilterType.Item);
        
        initBasicItemFilterWidget();
        initBasicFluidFilterWidget();
        initWildcardFilterConfigPanel();
        
        m_typeTabs = new GuiTabGroup(this, width(), 0, 27, height());
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_ITEM_TEXTURE));
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_FLUID_TEXTURE));
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_ENERGY_TEXTURE));
        m_typeTabs.setTabSelectedHandler(this::onTypeTabChanged);
        m_typeTabs.setSelectedTab(ITEM_TAB, false);
    	m_typeTabs.visible = false;
    }

    protected void initBasicItemFilterWidget()
    {
        m_basicItemFilterWidget = new BasicItemFilterWidget(this, 0, 17);

        m_basicItemFilterWidget.setWhiteBlackListChangedCallback(this::onItemWhiteBlackListChanged);
        m_basicItemFilterWidget.setMatchCountChangedCallback(this::onItemMatchCountChanged);
        m_basicItemFilterWidget.setMatchModChangedCallback(this::onItemMatchModChanged);
        m_basicItemFilterWidget.setMatchOreDictionaryChangedCallback(this::onItemMatchOreDictionaryChanged);
        m_basicItemFilterWidget.setFilterChangedCallback(this::onItemFilterSlotClicked);
        m_basicItemFilterWidget.setDeleteFilterCallback(this::onDeleteItemFilters);
        
        m_basicFilterConfigPanel.addChild(m_basicItemFilterWidget);
    }

    protected void initBasicFluidFilterWidget()
    {
        m_basicFluidFilterWidget = new BasicFluidFilterWidget(this, 0, 17);

        m_basicFluidFilterWidget.setWhiteBlackListChangedCallback(this::onFluidWhiteBlackListChanged);
        m_basicFluidFilterWidget.setMatchCountChangedCallback(this::onFluidMatchCountChanged);
        m_basicFluidFilterWidget.setMatchModChangedCallback(this::onFluidMatchModChanged);
        m_basicFluidFilterWidget.setMatchOreDictionaryChangedCallback(this::onFluidMatchOreDictionaryChanged);
        m_basicFluidFilterWidget.setFilterChangedCallback(this::onFluidFilterSlotClicked);
        m_basicFluidFilterWidget.setDeleteFilterCallback(this::onDeleteFluidFilters);

        m_basicFilterConfigPanel.addChild(m_basicFluidFilterWidget);
    }
	
    protected void initWildcardFilterConfigPanel()
    {
        m_wildcardFilterWidget = new WildcardFilterWidget(this, 0, 17, width() - BASE_PANEL_X * 2, 9);

        m_wildcardFilterWidget.setAddCallback(this::onFilterAdded);
        m_wildcardFilterWidget.setRemoveCallback(this::onFilterRemoved);

        m_wildcardFilterConfigPanel.addChild(m_wildcardFilterWidget);
    }
    
    @Override
    public void earlyInit()
    {
    	super.earlyInit();

        add(m_typeTabs);
    }
    
    @Override
    public void lateInit()
    {
    	PipeType type = menu.getStartingPipeType();
    	switch (type)
    	{
    	default:
    	case Item:
    		m_typeTabs.setSelectedTab(ITEM_TAB, true);
    		break;
    	case Fluid:
    		m_typeTabs.setSelectedTab(FLUID_TAB, true);
    		break;
    	case Energy:
    		m_typeTabs.setSelectedTab(ENERGY_TAB, true);
    		break;
    	}
    	super.lateInit();
    }
    
    @Override
    protected void onPageChanged(GuiPanelStack stack)
    {
    	super.onPageChanged(stack);
    	
    	// Handle edge case where this callback happens in the constructor of PipeContainerScreen
    	// (super) before m_typeTabs is initialized.
    	if (m_typeTabs == null)
    		return;
    	
        if (stack.topPanel() == m_basePanel)
        {
    		m_typeTabs.visible = false;
        }
        else if (stack.topPanel() == m_directionalConfigPanel)
        {
    		m_typeTabs.visible = true;
        }
        else if (stack.topPanel() == m_basicFilterConfigPanel ||
        		 stack.topPanel() == m_wildcardFilterConfigPanel)
        {
    		m_typeTabs.visible = false;
        }
        else if (stack.topPanel() == m_priorityPanel)
        {
    		m_typeTabs.visible = false;
        }
        else
        {
    		m_typeTabs.visible = false;
        }
    }

	@Override
	protected void showBasicFilterWidget()
	{
        if (m_typeTabs.getSelectedTab() == ITEM_TAB)
		{
	        BasicItemFilterUpgradeDataCache cache = menu.getBasicItemFilterCache(m_selectedDirection);
	        m_basicItemFilterWidget.setMatchCount(cache.getMatchCount());
	        m_basicItemFilterWidget.setMatchMod(cache.getMatchMod());
	        m_basicItemFilterWidget.setMatchOreDictionary(cache.getMatchOreDictionary());
	        m_basicItemFilterWidget.setWhiteBlackListState(cache.getWhiteListState());
	        m_basicItemFilterWidget.setFilters(cache.getFilters());
	        m_basicItemFilterWidget.visible = true;
			m_basicFluidFilterWidget.visible = false;
	        m_panelStack.push(m_basicFilterConfigPanel);
		}
        else if (m_typeTabs.getSelectedTab() == FLUID_TAB)
		{
	        BasicFluidFilterUpgradeDataCache cache = menu.getBasicFluidFilterCache(m_selectedDirection);
	        m_basicFluidFilterWidget.setMatchCount(cache.getMatchCount());
	        m_basicFluidFilterWidget.setMatchMod(cache.getMatchMod());
	        m_basicFluidFilterWidget.setMatchOreDictionary(cache.getMatchOreDictionary());
	        m_basicFluidFilterWidget.setWhiteBlackListState(cache.getWhiteListState());
	        m_basicFluidFilterWidget.setFilters(cache.getFilters());
	        m_basicFluidFilterWidget.visible = true;
			m_basicItemFilterWidget.visible = false;
	        m_panelStack.push(m_basicFilterConfigPanel);
		}
		else
		{
	        m_basicFluidFilterWidget.visible = false;
			m_basicItemFilterWidget.visible = false;
		}
	}

	@Override
	protected void showWildcardFilterWidget()
	{
        if (m_typeTabs.getSelectedTab() == ITEM_TAB)
        {
        	WildcardFilterUpgradeDataCache cache = getMenu().getItemWildcardFilterCache(m_selectedDirection);
            m_wildcardFilterWidget.setFilters(cache.getFilters());
            m_panelStack.push(m_wildcardFilterConfigPanel);
            m_wildcardFilterWidget.requestTextFieldFocus();
        }
        else if (m_typeTabs.getSelectedTab() == FLUID_TAB)
        {
        	WildcardFilterUpgradeDataCache cache = getMenu().getFluidWildcardFilterCache(m_selectedDirection);
            m_wildcardFilterWidget.setFilters(cache.getFilters());
            m_panelStack.push(m_wildcardFilterConfigPanel);
            m_wildcardFilterWidget.requestTextFieldFocus();
        }
	}

    protected void onItemWhiteBlackListChanged(WhiteBlackListButton button, WhiteListState newWhiteListState)
    {
        menu.getBasicItemFilterCache(m_selectedDirection).setWhiteListState(newWhiteListState);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onItemMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        menu.getBasicItemFilterCache(m_selectedDirection).setMatchCount(matchCount);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onItemMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getBasicItemFilterCache(m_selectedDirection).setMatchMod(matchMod);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onItemMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getBasicItemFilterCache(m_selectedDirection).setMatchOreDictionary(matchOreDictionary);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onItemFilterSlotClicked(int slot, ItemStack itemStack, int quantity)
    {
        menu.updateItemFilter(m_selectedDirection, slot, itemStack, quantity);
    }

    protected void sendItemFilterConfigurationUpdatePacketToServer()
    {
    	menu.updateItemFilterConfiguration(m_selectedDirection,
    			m_basicItemFilterWidget.getWhiteBlackListState(),
    			m_basicItemFilterWidget.getMatchCount(),
    			m_basicItemFilterWidget.getMatchDamage(),
    			m_basicItemFilterWidget.getMatchMod(),
    			m_basicItemFilterWidget.getMatchNBT(),
    			m_basicItemFilterWidget.getMatchOreDictionary());
    }

    protected void onFluidWhiteBlackListChanged(WhiteBlackListButton button, WhiteListState newWhiteListState)
    {
        menu.getBasicFluidFilterCache(m_selectedDirection).setWhiteListState(newWhiteListState);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onFluidMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        menu.getBasicFluidFilterCache(m_selectedDirection).setMatchCount(matchCount);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onFluidMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getBasicFluidFilterCache(m_selectedDirection).setMatchMod(matchMod);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onFluidMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getBasicFluidFilterCache(m_selectedDirection).setMatchOreDictionary(matchOreDictionary);

        sendFluidFilterConfigurationUpdatePacketToServer();
    }

    protected void onFluidFilterSlotClicked(int slot, FluidStack fluidStack)
    {
        menu.updateFluidFilter(m_selectedDirection, slot, fluidStack);
    }

    protected void sendFluidFilterConfigurationUpdatePacketToServer()
    {
    	menu.updateFluidFilterConfiguration(m_selectedDirection,
    			m_basicFluidFilterWidget.getWhiteBlackListState(),
    			m_basicFluidFilterWidget.getMatchCount(),
    			m_basicFluidFilterWidget.getMatchMod(),
    			m_basicFluidFilterWidget.getMatchOreDictionary());
    }
    
    protected void onFilterAdded(String filter)
    {
        if (m_typeTabs.getSelectedTab() == ITEM_TAB)
        	menu.addItemWildcardFilter(m_selectedDirection, filter);
        else if (m_typeTabs.getSelectedTab() == FLUID_TAB)
        	menu.addFluidWildcardFilter(m_selectedDirection, filter);
    }

    protected void onFilterRemoved(String filter)
    {
        if (m_typeTabs.getSelectedTab() == ITEM_TAB)
        	menu.removeItemWildcardFilter(m_selectedDirection, filter);
        else if (m_typeTabs.getSelectedTab() == FLUID_TAB)
        	menu.removeFluidWildcardFilter(m_selectedDirection, filter);
    }

    protected void onDeleteItemFilters()
    {
    	menu.clearAllItemFilters(m_selectedDirection);
    }

    protected void onDeleteFluidFilters()
    {
    	menu.clearAllFluidFilters(m_selectedDirection);
    }
    
    public void onTypeTabChanged(GuiTab tab, int oldIndex, int newIndex)
    {
    	if (newIndex == ITEM_TAB)
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.Item);
    	else if (newIndex == FLUID_TAB)
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.Fluid);
    	else
    		m_sideIOConfigurationWidget.setFilterType(SideIOConfigurationWidget.FilterType.None);

    	int transferIndex = getCurrentIndex();
        RedstoneControl redstoneControl = menu.getCache().getRedstoneControl(transferIndex);
        m_sideIOConfigurationWidget.setRedstoneControl(redstoneControl);

        TransferDirection transferDirection = menu.getCache().getTransferDirection(transferIndex);
        m_sideIOConfigurationWidget.setTransferDirection(transferDirection);

        Color color = menu.getCache().getColor(transferIndex);
        m_sideIOConfigurationWidget.setColor(color);
        
        m_sideIOConfigurationWidget.setFilterSettingsButtonVisible(
        		menu.containsUpgradeInSlot(transferIndex, UpgradeType.WildcardFilterUpgrade));

        menu.enableSideUpgradeSlots(getIndex(m_selectedDirection, oldIndex), false);
        menu.enableSideUpgradeSlots(transferIndex, true);
    }
    
    private int getIndex(Direction dir, int tab)
    {
    	return SwiftUtils.dirToIndex(dir) + (tab * Direction.values().length);
    }
    
    private int getIndexForDir(Direction dir)
    {
    	return getIndex(dir, m_typeTabs.getSelectedTab());
    }
    
    @Override
    protected int getCurrentIndex()
    {
    	return getIndexForDir(m_selectedDirection);
    }
    
    @Override
    protected void onDirectionChanged(Direction oldDirection, Direction newDirection)
    {
    	if (oldDirection != newDirection)
            menu.enableSideUpgradeSlots(getIndexForDir(oldDirection), false);
    }

    protected BasicItemFilterWidget m_basicItemFilterWidget;
    protected BasicFluidFilterWidget m_basicFluidFilterWidget;
    protected WildcardFilterWidget m_wildcardFilterWidget;
    protected GuiTabGroup m_typeTabs;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/fluid_filter_upgrade_slot.png");
    
    private static final int ITEM_TAB = 0;
    private static final int FLUID_TAB = 1;
    @SuppressWarnings("unused")
	private static final int ENERGY_TAB = 2;
}
