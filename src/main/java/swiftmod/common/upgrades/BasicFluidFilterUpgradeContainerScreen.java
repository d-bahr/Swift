package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.FluidFilterConfigurationPacket;
import swiftmod.common.gui.BasicFluidFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.WhiteBlackListButton;

@OnlyIn(Dist.CLIENT)
public class BasicFluidFilterUpgradeContainerScreen extends GuiContainerScreen<BasicFluidFilterUpgradeContainer>
{
    public BasicFluidFilterUpgradeContainerScreen(BasicFluidFilterUpgradeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, 176, 190, BACKGROUND_TEXTURE);

        m_filterWidget = new BasicFluidFilterWidget(this, 7, 20);
        m_filterWidget.setWhiteBlackListChangedCallback(this::onWhiteBlackListChanged);
        m_filterWidget.setMatchCountChangedCallback(this::onMatchCountChanged);
        m_filterWidget.setMatchModChangedCallback(this::onMatchModChanged);
        m_filterWidget.setMatchOreDictionaryChangedCallback(this::onMatchOreDictionaryChanged);
        m_filterWidget.setFilterChangedCallback(this::onFilterSlotClicked);
        m_filterWidget.setDeleteFilterCallback(this::onDeleteFilter);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        m_filterWidget.setMatchCount(menu.getCache().getMatchCount());
        m_filterWidget.setMatchMod(menu.getCache().getMatchMod());
        m_filterWidget.setMatchOreDictionary(menu.getCache().getMatchOreDictionary());
        m_filterWidget.setWhiteBlackListState(menu.getCache().getWhiteListState());
        m_filterWidget.setFilters(menu.getCache().getFilters());

        add(m_filterWidget);
    }

    protected void onWhiteBlackListChanged(WhiteBlackListButton button, WhiteListState newWhiteListState)
    {
        menu.getCache().setWhiteListState(newWhiteListState);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        menu.getCache().setMatchCount(matchCount);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getCache().setMatchMod(matchMod);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getCache().setMatchOreDictionary(matchOreDictionary);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onDeleteFilter()
    {
        menu.clearAllFilters();
    }

    protected void onFilterSlotClicked(int slot, FluidStack fluidStack)
    {
        menu.updateFilter(slot, fluidStack);
    }

    protected void sendFilterConfigurationUpdatePacketToServer()
    {
        FluidFilterConfigurationPacket updatePacket = new FluidFilterConfigurationPacket();
        updatePacket.whiteListState = m_filterWidget.getWhiteBlackListState();
        updatePacket.matchCount = m_filterWidget.getMatchCount();
        updatePacket.matchMod = m_filterWidget.getMatchMod();
        updatePacket.matchOreDictionary = m_filterWidget.getMatchOreDictionary();
        menu.sendUpdatePacketToServer(updatePacket);
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/basic_filter_background.png");

    protected BasicFluidFilterWidget m_filterWidget;
}
