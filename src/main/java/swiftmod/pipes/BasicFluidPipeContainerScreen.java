package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;
import swiftmod.common.gui.BasicFluidFilterWidget;
import swiftmod.common.gui.GuiBooleanStateButton;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.WhiteBlackListButton;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;

@OnlyIn(Dist.CLIENT)
public class BasicFluidPipeContainerScreen extends FluidPipeContainerScreen<BasicFluidPipeContainer>
{
    public BasicFluidPipeContainerScreen(BasicFluidPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
    }

    @Override
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
    
    @Override
    protected void showBasicFilterWidget()
    {
        BasicFluidFilterUpgradeDataCache cache = menu.getBasicFilterCache(m_selectedDirection);
        m_basicFilterWidget.setMatchCount(cache.getMatchCount());
        m_basicFilterWidget.setMatchMod(cache.getMatchMod());
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
    	menu.updateFilterConfiguration(m_selectedDirection,
    			m_basicFilterWidget.getWhiteBlackListState(),
    			m_basicFilterWidget.getMatchCount(),
    			m_basicFilterWidget.getMatchMod(),
    			m_basicFilterWidget.getMatchOreDictionary());
    }

    protected BasicFluidFilterWidget m_basicFilterWidget;

    protected static final ResourceLocation FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/fluid_filter_upgrade_slot.png");
}
