package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.WhiteBlackListButton;

@OnlyIn(Dist.CLIENT)
public class BasicItemFilterUpgradeContainerScreen extends GuiContainerScreen<BasicItemFilterUpgradeContainer>
{
    public BasicItemFilterUpgradeContainerScreen(BasicItemFilterUpgradeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title, 176, 190, BACKGROUND_TEXTURE);

        m_filterWidget = new BasicItemFilterWidget(this, 7, 20);
        m_filterWidget.setWhiteBlackListChangedCallback(this::onWhiteBlackListChanged);
        m_filterWidget.setMatchCountChangedCallback(this::onMatchCountChanged);
        m_filterWidget.setMatchDamageChangedCallback(this::onMatchDamageChanged);
        m_filterWidget.setMatchModChangedCallback(this::onMatchModChanged);
        m_filterWidget.setMatchNBTChangedCallback(this::onMatchNBTChanged);
        m_filterWidget.setMatchOreDictionaryChangedCallback(this::onMatchOreDictionaryChanged);
        m_filterWidget.setFilterChangedCallback(this::onFilterSlotClicked);
        m_filterWidget.setDeleteFilterCallback(this::onDeleteFilter);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        m_filterWidget.setMatchCount(menu.getCache().getMatchCount());
        m_filterWidget.setMatchDamage(menu.getCache().getMatchDamage());
        m_filterWidget.setMatchMod(menu.getCache().getMatchMod());
        m_filterWidget.setMatchNBT(menu.getCache().getMatchNBT());
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

    protected void onMatchDamageChanged(GuiBooleanStateButton button, boolean matchDamage)
    {
        menu.getCache().setMatchDamage(matchDamage);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getCache().setMatchMod(matchMod);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchNBTChanged(GuiBooleanStateButton button, boolean matchNBT)
    {
        menu.getCache().setMatchNBT(matchNBT);

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

    protected void onFilterSlotClicked(int slot, ItemStack itemStack, int quantity)
    {
        menu.updateFilter(slot, itemStack, quantity);
    }

    protected void sendFilterConfigurationUpdatePacketToServer()
    {
        ItemFilterConfigurationPacket updatePacket = new ItemFilterConfigurationPacket();
        updatePacket.whiteListState = m_filterWidget.getWhiteBlackListState();
        updatePacket.matchCount = m_filterWidget.getMatchCount();
        updatePacket.matchDamage = m_filterWidget.getMatchDamage();
        updatePacket.matchMod = m_filterWidget.getMatchMod();
        updatePacket.matchNBT = m_filterWidget.getMatchNBT();
        updatePacket.matchOreDictionary = m_filterWidget.getMatchOreDictionary();
        menu.sendUpdatePacketToServer(updatePacket);
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/basic_filter_background.png");

    protected BasicItemFilterWidget m_filterWidget;
}
