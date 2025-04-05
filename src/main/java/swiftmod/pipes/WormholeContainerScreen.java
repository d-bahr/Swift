package swiftmod.pipes;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import swiftmod.common.Swift;
import swiftmod.common.channels.ChannelType;
import swiftmod.common.gui.ChannelSelectionWidget;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.GuiTab;
import swiftmod.common.gui.GuiTabGroup;

public class WormholeContainerScreen extends GuiContainerScreen<WormholeContainer>
{
    public WormholeContainerScreen(WormholeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, 176, 175, BACKGROUND_TEXTURE);

        m_channelSelectionWidget = new ChannelSelectionWidget(this, 10, 20);

        m_channelSelectionWidget.setAddChannelCallback(menu::addChannel);
        m_channelSelectionWidget.setDeleteChannelCallback(menu::deleteChannel);
        m_channelSelectionWidget.setSetChannelCallback(menu::setChannel);
        m_channelSelectionWidget.setUnsetChannelCallback(menu::unsetChannel);

        showPlayerInventory(false);
        
        m_typeTabs = new GuiTabGroup(this, width(), 0, 27, height());
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_ITEM_TEXTURE));
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_FLUID_TEXTURE));
        m_typeTabs.addTab(new GuiTab(this, GuiTab.TAB_ENERGY_TEXTURE));
        m_typeTabs.setTabSelectedHandler(this::onTypeTabChanged);
        m_typeTabs.setSelectedTab(m_channelSelectionWidget.getChannelType().getIndex(), false);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();
        
        int index = m_channelSelectionWidget.getChannelType().getIndex();

        m_channelSelectionWidget.setPrivateChannels(menu.getPrivateChannels(index));
        m_channelSelectionWidget.setPublicChannels(menu.getPublicChannels(index));
        m_channelSelectionWidget.setCurrentChannel(menu.getCache().getChannel(index), true);

        add(m_channelSelectionWidget);
        add(m_typeTabs);
    }

    @Override
    public void lateInit()
    {
    	m_channelSelectionWidget.requestTextFieldFocus();
    }
    
    public void onTypeTabChanged(GuiTab tab, int oldIndex, int newIndex)
    {
    	// Update channels.
        m_channelSelectionWidget.setPrivateChannels(menu.getPrivateChannels(newIndex));
        m_channelSelectionWidget.setPublicChannels(menu.getPublicChannels(newIndex));
        m_channelSelectionWidget.setCurrentChannel(menu.getCache().getChannel(newIndex), true);
        m_channelSelectionWidget.setChannelType(ChannelType.fromIndex(newIndex));
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/channel_background.png");

    protected ChannelSelectionWidget m_channelSelectionWidget;
    protected GuiTabGroup m_typeTabs;
}
