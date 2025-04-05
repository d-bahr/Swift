package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;

@OnlyIn(Dist.CLIENT)
public class BasicItemPipeContainerScreen extends ItemPipeContainerScreen<BasicItemPipeContainer>
{
    public BasicItemPipeContainerScreen(BasicItemPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
        
        initBasicFilterWidget();
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
        BasicItemFilterUpgradeDataCache cache = getMenu().getBasicFilterCache(m_selectedDirection);
        m_basicFilterWidget.setMatchCount(cache.getMatchCount());
        m_basicFilterWidget.setMatchDamage(cache.getMatchDamage());
        m_basicFilterWidget.setMatchMod(cache.getMatchMod());
        m_basicFilterWidget.setMatchNBT(cache.getMatchNBT());
        m_basicFilterWidget.setMatchOreDictionary(cache.getMatchOreDictionary());
        m_basicFilterWidget.setWhiteBlackListState(cache.getWhiteListState());
        m_basicFilterWidget.setFilters(cache.getFilters());
        m_panelStack.push(m_basicFilterConfigPanel);
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
    	menu.updateFilterConfiguration(m_selectedDirection,
    			m_basicFilterWidget.getWhiteBlackListState(),
    			m_basicFilterWidget.getMatchCount(),
    			m_basicFilterWidget.getMatchDamage(),
    			m_basicFilterWidget.getMatchMod(),
    			m_basicFilterWidget.getMatchNBT(),
    			m_basicFilterWidget.getMatchOreDictionary());
    }

    protected BasicItemFilterWidget m_basicFilterWidget;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/item_filter_upgrade_slot.png");
}
