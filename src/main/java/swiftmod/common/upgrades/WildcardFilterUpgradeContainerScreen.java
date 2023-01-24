package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.WildcardFilterWidget;

@OnlyIn(Dist.CLIENT)
public class WildcardFilterUpgradeContainerScreen extends GuiContainerScreen<WildcardFilterUpgradeContainer>
{
    public WildcardFilterUpgradeContainerScreen(WildcardFilterUpgradeContainer c, Inventory inv,
            Component title)
    {
        super(c, inv, title, 176, 126, BACKGROUND_TEXTURE);

        m_filterWidget = new WildcardFilterWidget(this, 10, 11, width() - 20, 8);
        m_filterWidget.setAddCallback(this::onFilterAdded);
        m_filterWidget.setRemoveCallback(this::onFilterRemoved);

        showPlayerInventory(false);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        m_filterWidget.setFilters(menu.getCache().getFilters());

        add(m_filterWidget);
    }

    @Override
    public void lateInit()
    {
    	m_filterWidget.requestTextFieldFocus();
    }

    protected void onFilterAdded(String filter)
    {
        menu.addFilter(filter);
    }

    protected void onFilterRemoved(String filter)
    {
        menu.removeFilter(filter);
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/wildcard_filter_background.png");

    protected WildcardFilterWidget m_filterWidget;
}
