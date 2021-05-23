package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.gui.ChannelSelectionWidget;
import swiftmod.common.gui.GuiContainerScreen;

@OnlyIn(Dist.CLIENT)
public class TeleporterUpgradeContainerScreen extends GuiContainerScreen<TeleporterUpgradeContainer>
{
    public TeleporterUpgradeContainerScreen(TeleporterUpgradeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title, 176, 175, BACKGROUND_TEXTURE);

        m_channelSelectionWidget = new ChannelSelectionWidget(this, 10, 20);

        m_channelSelectionWidget.setAddChannelCallback(menu::addChannel);
        m_channelSelectionWidget.setDeleteChannelCallback(menu::deleteChannel);
        m_channelSelectionWidget.setSetChannelCallback(menu::setChannel);
        m_channelSelectionWidget.setUnsetChannelCallback(menu::unsetChannel);

        showPlayerInventory(false);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        m_channelSelectionWidget.setPrivateChannels(menu.getCache().privateChannels);
        m_channelSelectionWidget.setPublicChannels(menu.getCache().publicChannels);
        m_channelSelectionWidget.setCurrentChannel(menu.getCache().getChannel(), true);

        add(m_channelSelectionWidget);
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/channel_background.png");

    protected ChannelSelectionWidget m_channelSelectionWidget;
}
