package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.FluidFilterConfigurationPacket;
import swiftmod.common.gui.BasicFluidFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiTexture;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;

public abstract class AbstractAdvancedFluidPipeContainerScreen<T extends FluidPipeContainer>
        extends AdvancedPipeContainerScreen<T>
{
    public AbstractAdvancedFluidPipeContainerScreen(T c, PlayerInventory inv, ITextComponent title)
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
        m_basicFilterWidget = new BasicFluidFilterWidget(this, 0, 17);

        m_basicFilterWidget.setWhiteBlackListChangedCallback(this::onWhiteBlackListChanged);
        m_basicFilterWidget.setMatchCountChangedCallback(this::onMatchCountChanged);
        m_basicFilterWidget.setMatchModChangedCallback(this::onMatchModChanged);
        m_basicFilterWidget.setMatchOreDictionaryChangedCallback(this::onMatchOreDictionaryChanged);
        m_basicFilterWidget.setFilterChangedCallback(this::onFilterSlotClicked);
        m_basicFilterWidget.setDeleteFilterCallback(this::onDeleteFilter);

        return m_basicFilterWidget;
    }

    protected void fillBasicFiltersFromCache()
    {
        BasicFluidFilterUpgradeDataCache cache = menu.getBasicFilterCache(m_selectedDirection);
        m_basicFilterWidget.setMatchCount(cache.getMatchCount());
        m_basicFilterWidget.setMatchMod(cache.getMatchMod());
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

    protected void onMatchModChanged(GuiBooleanStateButton button, boolean matchMod)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchMod(matchMod);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onMatchOreDictionaryChanged(GuiBooleanStateButton button, boolean matchOreDictionary)
    {
        menu.getBasicFilterCache(m_selectedDirection).setMatchOreDictionary(matchOreDictionary);

        sendFilterConfigurationUpdatePacketToServer();
    }

    protected void onFilterSlotClicked(int slot, FluidStack fluidStack)
    {
        menu.updateFilter(m_selectedDirection, slot, fluidStack);
    }

    protected void sendFilterConfigurationUpdatePacketToServer()
    {
        FluidFilterConfigurationPacket updatePacket = new FluidFilterConfigurationPacket();
        updatePacket.direction = m_selectedDirection;
        updatePacket.whiteListState = m_basicFilterWidget.getWhiteBlackListState();
        updatePacket.matchCount = m_basicFilterWidget.getMatchCount();
        updatePacket.matchMod = m_basicFilterWidget.getMatchMod();
        updatePacket.matchOreDictionary = m_basicFilterWidget.getMatchOreDictionary();
        menu.sendUpdatePacketToServer(updatePacket);
    }

    protected BasicFluidFilterWidget m_basicFilterWidget;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/fluid_filter_upgrade_slot.png");
}
