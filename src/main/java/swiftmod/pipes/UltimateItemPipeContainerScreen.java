package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.SlotBase;
import swiftmod.common.Swift;
import swiftmod.common.gui.ChannelSelectionWidget;
import swiftmod.common.gui.GuiPanel;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.GuiSettingsButton;
import swiftmod.common.gui.GuiTexture;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.SwiftGui;
import swiftmod.common.upgrades.UpgradeType;

@OnlyIn(Dist.CLIENT)
public class UltimateItemPipeContainerScreen extends AbstractAdvancedItemPipeContainerScreen<UltimateItemPipeContainer>
{
    public UltimateItemPipeContainerScreen(UltimateItemPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
        
        menu.setTeleportSlotChangedCallback(this::onTeleportSlotChanged);

        m_channelSelectionWidget = new ChannelSelectionWidget(this, 0, 0, width() - BASE_PANEL_X * 2, 8);

        m_channelSelectionWidget.setAddChannelCallback(menu::addChannel);
        m_channelSelectionWidget.setDeleteChannelCallback(menu::deleteChannel);
        m_channelSelectionWidget.setSetChannelCallback(menu::setChannel);
        m_channelSelectionWidget.setUnsetChannelCallback(menu::unsetChannel);

        m_teleportSettingsPanel = new GuiPanel(this, BASE_PANEL_X, 24, m_channelSelectionWidget.width(),
                m_channelSelectionWidget.height());

        m_teleportSettingsPanel.addChild(m_channelSelectionWidget);
    }

    @Override
    protected GuiTexture createUpgradeSlotsTexture()
    {
        return new GuiTexture(this, 0, 0, 36, 36, UPGRADE_SLOTS_TEXTURE);
    }

    @Override
    protected void initUpgradePanel()
    {
        super.initUpgradePanel();

        m_teleportSettingsButton = new GuiSettingsButton(this, m_upgradePanel.width() - SwiftGui.BUTTON_WIDTH - 1,
                m_upgradePanel.height() - SwiftGui.BUTTON_HEIGHT - 1);
        m_teleportSettingsButton.setClickCallback(this::openTeleportSettings);
        m_teleportSettingsButton.setTooltip(Component.literal("Change channel settings"));

        m_upgradePanel.addChild(m_teleportSettingsButton);
    }

    @Override
    public void containerTick()
    {
        super.containerTick();
    }

    @Override
    public void lateInit()
    {
        super.lateInit();

        m_teleportSettingsButton.visible = menu.containsUpgradeInSlot(UpgradeType.TeleportUpgrade);
    }

    protected void onTeleportSlotChanged(SlotBase slot)
    {
        m_teleportSettingsButton.visible = slot.hasItem();
    }

    @Override
    protected void onPageChanged(GuiPanelStack stack)
    {
        super.onPageChanged(stack);

        if (stack.topPanel() == m_teleportSettingsPanel)
        {
            if (m_upgradePanel != null)
                m_upgradePanel.hide();
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            showPlayerInventory(false);
        }
    }

    protected void openTeleportSettings(GuiWidget widget, MouseButton button)
    {
        m_channelSelectionWidget.setPrivateChannels(menu.getChannelConfigurationCache().privateChannels);
        m_channelSelectionWidget.setPublicChannels(menu.getChannelConfigurationCache().publicChannels);
        m_channelSelectionWidget.setCurrentChannel(menu.getCurrentChannelConfiguration(), true);

        m_panelStack.push(m_teleportSettingsPanel);

        m_channelSelectionWidget.requestTextFieldFocus();
    }

    protected GuiSettingsButton m_teleportSettingsButton;
    protected ChannelSelectionWidget m_channelSelectionWidget;
    protected GuiPanel m_teleportSettingsPanel;

    protected static final ResourceLocation UPGRADE_SLOTS_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/ultimate_pipe_upgrade_slots.png");
}
