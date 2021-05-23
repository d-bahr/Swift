package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.WildcardFilterWidget;

@OnlyIn(Dist.CLIENT)
public class WildcardFilterUpgradeContainerScreen extends GuiContainerScreen<WildcardFilterUpgradeContainer>
{
    public WildcardFilterUpgradeContainerScreen(WildcardFilterUpgradeContainer c, PlayerInventory inv,
            ITextComponent title)
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
