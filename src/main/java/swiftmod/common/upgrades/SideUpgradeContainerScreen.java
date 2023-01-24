package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.client.SideConfigurationPacket;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.SideIOConfigurationWidget;

@OnlyIn(Dist.CLIENT)
public class SideUpgradeContainerScreen extends GuiContainerScreen<SideUpgradeContainer>
{
    public SideUpgradeContainerScreen(SideUpgradeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, 176, 190, BACKGROUND_TEXTURE);

        m_sideIOConfigWidget = new SideIOConfigurationWidget(this, 62, 26);

        m_sideIOConfigWidget.setStateChangedCallback(this::setSideIOStateChanged);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        m_sideIOConfigWidget.setStates(menu.getCache().getStates());

        add(m_sideIOConfigWidget);
    }

    protected void setSideIOStateChanged(Direction dir, byte state)
    {
        byte[] states = m_sideIOConfigWidget.getStates();
        menu.getCache().setStates(states);
        
        SideConfigurationPacket updatePacket = new SideConfigurationPacket();
        updatePacket.directionStates = states;
        menu.sendUpdatePacketToServer(updatePacket);
    }

    // TODO: Switch to a different background. Can be a lot smaller.
    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/basic_filter_background.png");

    protected SideIOConfigurationWidget m_sideIOConfigWidget;
}
