package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiTexture;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractAdvancedItemPipeContainerScreen<T extends ItemPipeContainer> extends AdvancedPipeContainerScreen<T>
{
    public AbstractAdvancedItemPipeContainerScreen(T c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }

    @Override
    protected void addDirectionalUpgradeSlots()
    {
        super.addDirectionalUpgradeSlots();

        GuiTexture filterUpgradeSlotTexture = new GuiTexture(this, SIDE_UPGRADE_PANEL_SLOT_START_X,
                SIDE_UPGRADE_PANEL_SLOT_START_Y, SIDE_UPGRADE_PANEL_SLOT_WIDTH, SIDE_UPGRADE_PANEL_SLOT_HEIGHT,
                FILTER_UPGRADE_SLOT_TEXTURE);

        m_directionalConfigPanel.addChild(filterUpgradeSlotTexture);
    }

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

    protected void fillBasicFiltersFromCache()
    {
        BasicItemFilterUpgradeDataCache cache = menu.getBasicFilterCache(m_selectedDirection);
        m_basicFilterWidget.setMatchCount(cache.getMatchCount());
        m_basicFilterWidget.setMatchDamage(cache.getMatchDamage());
        m_basicFilterWidget.setMatchMod(cache.getMatchMod());
        m_basicFilterWidget.setMatchNBT(cache.getMatchNBT());
        m_basicFilterWidget.setMatchOreDictionary(cache.getMatchOreDictionary());
        m_basicFilterWidget.setWhiteBlackListState(cache.getWhiteListState());
        m_basicFilterWidget.setFilters(cache.getFilters());
    }

    protected void onWhiteBlackListChanged(WhiteBlackListButton button, WhiteListState newWhiteListState)
    {
        menu.getBasicFilterCache(m_selectedDirection).setWhiteListState(newWhiteListState);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchCount(matchCount);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchDamageChanged(GuiBooleanStateButton button, boolean matchDamage)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchDamage(matchDamage);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchMod(matchMod);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchNBTChanged(GuiBooleanStateButton button, boolean matchNBT)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchNBT(matchNBT);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchOreDictionary(matchOreDictionary);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onFilterSlotClicked(int slot, ItemStack itemStack, int quantity)
    {
        menu.updateFilter(m_selectedDirection, slot, itemStack, quantity);
    }

    protected void sendFilterConfigurationUpdatePacketToServer()
    {
        ItemFilterConfigurationPacket updatePacket = new ItemFilterConfigurationPacket();
        updatePacket.direction = m_selectedDirection;
        updatePacket.whiteListState = m_basicFilterWidget.getWhiteBlackListState();
        updatePacket.matchCount = m_basicFilterWidget.getMatchCount();
        updatePacket.matchDamage = m_basicFilterWidget.getMatchDamage();
        updatePacket.matchMod = m_basicFilterWidget.getMatchMod();
        updatePacket.matchNBT = m_basicFilterWidget.getMatchNBT();
        updatePacket.matchOreDictionary = m_basicFilterWidget.getMatchOreDictionary();
        menu.sendUpdatePacketToServer(updatePacket);
    }

    protected BasicItemFilterWidget m_basicFilterWidget;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/item_filter_upgrade_slot.png");
}
