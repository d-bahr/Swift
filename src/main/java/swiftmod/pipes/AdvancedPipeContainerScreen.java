package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import swiftmod.common.MouseButton;
import swiftmod.common.SlotBase;
import swiftmod.common.Swift;
import swiftmod.common.client.SideConfigurationPacket;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiPanel;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.GuiSettingsButton;
import swiftmod.common.gui.GuiTexture;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.gui.WildcardFilterWidget;
import swiftmod.common.upgrades.SideUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

public abstract class AdvancedPipeContainerScreen<T extends PipeContainer> extends PipeContainerScreen<T>
{
    public AdvancedPipeContainerScreen(T c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);

        menu.setFilterSlotChangedCallback(this::onFilterSlotChanged);
        menu.setSideSlotChangedCallback(this::onSideSlotChanged);

        m_upgradePanel = new GuiPanel(this, UPGRADE_PANEL_X, UPGRADE_PANEL_Y, 36, 54);
        m_basicFilterConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X, 90);
        m_wildcardFilterConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y + 19, width() - (BASE_PANEL_X * 2),
                height() - (BASE_PANEL_Y + 20));
        m_sideConfigPanel = new GuiPanel(this, BASE_PANEL_X + 55, BASE_PANEL_Y + 18, 54, 54);

        initUpgradePanel();
        addDirectionalUpgradeSlots();
        initBasicFilterConfigPanel();
        initWildcardFilterConfigPanel();
        initSideConfigPanel();
    }

    protected GuiTexture createUpgradeSlotsTexture()
    {
        return new GuiTexture(this, 0, 0, 36, 36, UPGRADE_SLOTS_TEXTURE);
    }

    protected void initUpgradePanel()
    {
        m_upgradePanel.addChild(createUpgradeSlotsTexture());
    }

    protected abstract GuiWidget initBasicFilterWidget();

    protected void initBasicFilterConfigPanel()
    {
        m_basicFilterConfigPanel.addChild(initBasicFilterWidget());
    }

    protected void initWildcardFilterConfigPanel()
    {
        m_wildcardFilterWidget = new WildcardFilterWidget(this, 0, 0, width() - BASE_PANEL_X * 2, 12);

        m_wildcardFilterWidget.setAddCallback(this::onFilterAdded);
        m_wildcardFilterWidget.setRemoveCallback(this::onFilterRemoved);

        m_wildcardFilterConfigPanel.addChild(m_wildcardFilterWidget);
    }

    protected void initSideConfigPanel()
    {
        m_sideIOConfigWidget = new SideIOConfigurationWidget(this, 0, 0);

        m_sideIOConfigWidget.setStateChangedCallback(this::setSideIOStateChanged);

        m_sideConfigPanel.addChild(m_sideIOConfigWidget);
    }

    protected void addDirectionalUpgradeSlots()
    {
        m_filterSettingsButton = new GuiSettingsButton(this, SIDE_UPGRADE_PANEL_SLOT_START_X + SIDE_UPGRADE_PANEL_SLOT_WIDTH + 1,
                SIDE_UPGRADE_PANEL_SLOT_START_Y + 1);
        m_filterSettingsButton.setClickCallback(this::openFilterSettings);
        m_filterSettingsButton.setTooltip(new StringTextComponent("Change filter settings"));

        GuiTexture sideUpgradeSlotTexture = new GuiTexture(this,
                SIDE_UPGRADE_PANEL_SLOT_START_X + SIDE_UPGRADE_PANEL_SLOT_OFFSET_X, SIDE_UPGRADE_PANEL_SLOT_START_Y, 18,
                18, SIDE_UPGRADE_SLOT_TEXTURE);

        m_sideUpgradeSettingsButton = new GuiSettingsButton(this, sideUpgradeSlotTexture.right() + 1,
                sideUpgradeSlotTexture.top() + 1);
        m_sideUpgradeSettingsButton.setClickCallback(this::openSideConfigSettings);
        m_sideUpgradeSettingsButton.setTooltip(new StringTextComponent("Change side I/O settings"));

        m_directionalConfigPanel.addChild(m_filterSettingsButton);
        m_directionalConfigPanel.addChild(sideUpgradeSlotTexture);
        m_directionalConfigPanel.addChild(m_sideUpgradeSettingsButton);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        add(m_upgradePanel);
    }

    @Override
    public void lateInit()
    {
        super.lateInit();

        m_filterSettingsButton.visible = menu.containsUpgradeInSlot(m_selectedDirection,
                UpgradeType.WildcardFilterUpgrade);
        m_sideUpgradeSettingsButton.visible = menu.containsUpgradeInSlot(m_selectedDirection,
                UpgradeType.SideUpgrade);
    }

    protected void onFilterSlotChanged(SlotBase slot, Direction dir)
    {
        if (dir == m_selectedDirection)
            m_filterSettingsButton.visible = slot.hasItem();
    }

    protected void onSideSlotChanged(SlotBase slot, Direction dir)
    {
        if (dir == m_selectedDirection)
            m_sideUpgradeSettingsButton.visible = slot.hasItem();
    }

    @Override
    protected void onPageChanged(GuiPanelStack stack)
    {
        super.onPageChanged(stack);

        if (stack.topPanel() == m_basicFilterConfigPanel)
        {
            if (m_upgradePanel != null)
                m_upgradePanel.hide();
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            showPlayerInventory(true);
        }
        else if (stack.topPanel() == m_wildcardFilterConfigPanel)
        {
            if (m_upgradePanel != null)
                m_upgradePanel.hide();
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            showPlayerInventory(false);
        }
        else if (stack.topPanel() == m_sideConfigPanel)
        {
            if (m_upgradePanel != null)
                m_upgradePanel.show();
            menu.enableBaseUpgradeSlots(true);
            menu.disableSideUpgradeSlots();
            showPlayerInventory(true);
        }
        else if (stack.topPanel() == m_basePanel || stack.topPanel() == m_directionalConfigPanel)
        {
            if (m_upgradePanel != null)
                m_upgradePanel.show();
        }
    }

    protected void onDirectionChanged(Direction direction)
    {
        m_filterSettingsButton.visible = menu.containsUpgradeInSlot(m_selectedDirection,
                UpgradeType.WildcardFilterUpgrade);
        m_sideUpgradeSettingsButton.visible = menu.containsUpgradeInSlot(m_selectedDirection,
                UpgradeType.SideUpgrade);
    }

    protected abstract void fillBasicFiltersFromCache();

    protected void openFilterSettings(GuiWidget widget, MouseButton button)
    {
        if (menu.containsUpgrade(m_selectedDirection, UpgradeType.WildcardFilterUpgrade))
        {
            WildcardFilterUpgradeDataCache cache = menu.getWildcardFilterCache(m_selectedDirection);
            m_wildcardFilterWidget.setFilters(cache.getFilters());
            m_panelStack.push(m_wildcardFilterConfigPanel);
        }
        else
        {
            fillBasicFiltersFromCache();

            m_panelStack.push(m_basicFilterConfigPanel);
        }
    }

    protected void openSideConfigSettings(GuiWidget widget, MouseButton button)
    {
        SideUpgradeDataCache cache = menu.getSideUpgradeCache(m_selectedDirection);
        m_sideIOConfigWidget.setStates(cache.getStates());

        m_panelStack.push(m_sideConfigPanel);
    }

    protected void onDeleteFilter()
    {
        menu.clearAllFilters(m_selectedDirection);
    }

    protected void setSideIOStateChanged(Direction dir, byte state)
    {
        byte[] states = m_sideIOConfigWidget.getStates();
        menu.getSideUpgradeCache(dir).setStates(states);

        SideConfigurationPacket updatePacket = new SideConfigurationPacket();
        updatePacket.direction = m_selectedDirection;
        updatePacket.directionStates = states;
        menu.sendUpdatePacketToServer(updatePacket);
    }

    protected void onFilterAdded(String filter)
    {
        menu.addFilter(m_selectedDirection, filter);
    }

    protected void onFilterRemoved(String filter)
    {
        menu.removeFilter(m_selectedDirection, filter);
    }

    protected GuiPanel m_upgradePanel;
    protected GuiPanel m_basicFilterConfigPanel;
    protected GuiPanel m_wildcardFilterConfigPanel;
    protected BasicItemFilterWidget m_basicFilterWidget;
    protected WildcardFilterWidget m_wildcardFilterWidget;
    protected SideIOConfigurationWidget m_sideIOConfigWidget;
    protected GuiPanel m_sideConfigPanel;
    protected GuiSettingsButton m_filterSettingsButton;
    protected GuiSettingsButton m_sideUpgradeSettingsButton;

    protected static final ResourceLocation SIDE_UPGRADE_SLOT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/side_upgrade_slot.png");
    protected static final ResourceLocation UPGRADE_SLOTS_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/advanced_pipe_upgrade_slots.png");
}
