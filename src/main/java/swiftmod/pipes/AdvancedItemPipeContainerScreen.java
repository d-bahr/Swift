package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Color;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;
import swiftmod.common.WhiteListState;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiPanel;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.HandlerDirectionSelectionWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeType;

@OnlyIn(Dist.CLIENT)
public class AdvancedItemPipeContainerScreen extends ItemPipeContainerScreen<AdvancedItemPipeContainer>
{
    public AdvancedItemPipeContainerScreen(AdvancedItemPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
        
        m_handlerDirection = Direction.NORTH;
        
        m_handlerConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, 90);
        
        initHandlerConfigPanel();
    }

    @Override
    protected GuiWidget initBasicFilterWidget()
    {
    	m_basicFilterWidget = new BasicItemFilterWidget(this, 0, 17);

        m_basicFilterWidget.setWhiteBlackListChangedCallback(this::onWhiteBlackListChanged);
        m_basicFilterWidget.setMatchCountChangedCallback(this::onMatchCountChanged);
        m_basicFilterWidget.setMatchDamageChangedCallback(this::onMatchDamageChanged);
        m_basicFilterWidget.setMatchModChangedCallback(this::onMatchModChanged);
        m_basicFilterWidget.setMatchNBTChangedCallback(this::onMatchNBTChanged);
        m_basicFilterWidget.setMatchOreDictionaryChangedCallback(this::onMatchOreDictionaryChanged);
        m_basicFilterWidget.setFilterChangedCallback(this::onFilterSlotClicked);
        m_basicFilterWidget.setDeleteFilterCallback(this::onDeleteFilter);

        return m_basicFilterWidget;
    }
    
    @Override
    protected void showBasicFilterWidget()
    {
        BasicItemFilterUpgradeDataCache cache = getMenu().getBasicFilterCache(m_selectedDirection, m_handlerDirection);
        m_basicFilterWidget.setMatchCount(cache.getMatchCount());
        m_basicFilterWidget.setMatchDamage(cache.getMatchDamage());
        m_basicFilterWidget.setMatchMod(cache.getMatchMod());
        m_basicFilterWidget.setMatchNBT(cache.getMatchNBT());
        m_basicFilterWidget.setMatchOreDictionary(cache.getMatchOreDictionary());
        m_basicFilterWidget.setWhiteBlackListState(cache.getWhiteListState());
        m_basicFilterWidget.setFilters(cache.getFilters());
        m_panelStack.push(m_basicFilterConfigPanel);
    }

    protected void initHandlerConfigPanel()
    {
        m_handlerDirSelectionWidget = new HandlerDirectionSelectionWidget(this, 55, 18);
        m_handlerDirSelectionWidget.setDirectionButtonPressCallback(this::onHandlerDirectionButtonPressed);
        
    	m_handlerConfigPanel.addChild(m_handlerDirSelectionWidget);
    }

    @Override
    protected void onPageChanged(GuiPanelStack stack)
    {
    	if (stack.topPanel() == m_handlerConfigPanel)
    	{
    		menu.enableBaseUpgradeSlots(true);
            menu.disableSideUpgradeSlots();
            m_chunkLoaderSlotTexture.visible = true;
            m_backgroundTexture.setHeight(DEFAULT_HEIGHT);
            m_backgroundTexture.setTexture(DEFAULT_BACKGROUND_TEXTURE);
            showPlayerInventory(true);
            setHeight(DEFAULT_HEIGHT);
    	}
    	else
    	{
    		super.onPageChanged(stack);
    	}
    }
    
    @Override
    protected void onDirectionButtonPressed(Direction direction)
    {
		Direction oldDirection = m_selectedDirection;
        m_selectedDirection = direction;
        
        m_handlerDirSelectionWidget.setItem(menu.getNeighbor(m_selectedDirection), menu.getNeighborPos(m_selectedDirection));
        m_handlerDirSelectionWidget.setBlockFacingDirection(menu.getNeighborFacing(m_selectedDirection));

        m_panelStack.push(m_handlerConfigPanel);
        onDirectionChanged(oldDirection, direction);
    }
    
    private void onHandlerDirectionButtonPressed(Direction direction)
    {
		Direction oldHandlerDirection = m_handlerDirection;
		m_handlerDirection = direction;
        
        int index = getCurrentIndex();
    	
        RedstoneControl redstoneControl = menu.getCache().getRedstoneControl(index);
        m_sideIOConfigurationWidget.setRedstoneControl(redstoneControl);

        TransferDirection transferDirection = menu.getCache().getTransferDirection(index);
        m_sideIOConfigurationWidget.setTransferDirection(transferDirection);

        Color color = menu.getCache().getColor(index);
        m_sideIOConfigurationWidget.setColor(color);
        
        m_sideIOConfigurationWidget.setFilterSettingsButtonVisible(
        		menu.containsUpgradeInSlot(index, UpgradeType.WildcardFilterUpgrade));

        m_panelStack.push(m_directionalConfigPanel);
        onHandlerDirectionChanged(oldHandlerDirection, direction);
    }
    
    protected void onHandlerDirectionChanged(Direction oldDirection, Direction newDirection)
    {
    }

    protected void onWhiteBlackListChanged(WhiteBlackListButton button, WhiteListState newWhiteListState)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setWhiteListState(newWhiteListState);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setMatchCount(matchCount);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchDamageChanged(GuiBooleanStateButton button, boolean matchDamage)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setMatchDamage(matchDamage);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setMatchMod(matchMod);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchNBTChanged(GuiBooleanStateButton button, boolean matchNBT)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setMatchNBT(matchNBT);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getBasicFilterCache(m_selectedDirection, m_handlerDirection).setMatchOreDictionary(matchOreDictionary);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onFilterSlotClicked(int slot, ItemStack itemStack, int quantity)
    {
        menu.updateFilter(m_selectedDirection, m_handlerDirection, slot, itemStack, quantity);
    }

    protected void sendFilterConfigurationUpdatePacketToServer()
    {
    	menu.updateFilterConfiguration(m_selectedDirection, m_handlerDirection,
    			m_basicFilterWidget.getWhiteBlackListState(),
    			m_basicFilterWidget.getMatchCount(),
    			m_basicFilterWidget.getMatchDamage(),
    			m_basicFilterWidget.getMatchMod(),
    			m_basicFilterWidget.getMatchNBT(),
    			m_basicFilterWidget.getMatchOreDictionary());
    }
    
    @Override
    protected int getCurrentIndex()
    {
    	return AdvancedItemPipeTileEntity.toTransferIndex(m_selectedDirection, m_handlerDirection);
    }

    protected Direction m_handlerDirection;
    protected GuiPanel m_handlerConfigPanel;
    protected HandlerDirectionSelectionWidget m_handlerDirSelectionWidget;
    protected BasicItemFilterWidget m_basicFilterWidget;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/item_filter_upgrade_slot.png");
}
