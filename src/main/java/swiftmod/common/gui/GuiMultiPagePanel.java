package swiftmod.common.gui;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMultiPagePanel extends GuiWidget
{
    public GuiMultiPagePanel(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height, StringTextComponent.EMPTY);
        m_pages = new ArrayList<GuiPanel>();
    }

    public GuiMultiPagePanel(GuiContainerScreen<?> screen, int width, int height, ITextComponent title)
    {
        super(screen, width, height, title);
        m_pages = new ArrayList<GuiPanel>();
    }

    public GuiMultiPagePanel(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_pages = new ArrayList<GuiPanel>();
    }

    public GuiMultiPagePanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent title)
    {
        super(screen, x, y, width, height, title);
        m_pages = new ArrayList<GuiPanel>();
    }

    /*public void setPage(int page, GuiPanel panel)
    {
        m_pages.set(page, panel);
    }*/

    public void setBackgroundTexture(int page, ResourceLocation texture)
    {
        m_pages.get(page).setBackgroundTexture(texture);
    }

    public ResourceLocation getBackgroundTexture(int page)
    {
        return m_pages.get(page).getBackgroundTexture();
    }

    public GuiPanel addPage()
    {
        GuiPanel panel = new GuiPanel(getScreen(), width, height);
        addPage(panel);
        return panel;
    }

    public void addPage(GuiPanel panel)
    {
        m_pages.add(panel);
        addChild(panel);
    }

    public void showPage(int page)
    {
        if (page >= m_pages.size())
            throw new IllegalArgumentException("Invalid page number.");

        if (m_currentPage >= 0)
        {
            m_pages.get(m_currentPage).hide();
        }
        else
        {
            for (int i = 0; i < m_pages.size(); ++i)
                m_pages.get(i).hide();
        }
        m_currentPage = page;
        m_pages.get(page).show();
    }

    public int getCurrentPage()
    {
        return m_currentPage;
    }

    private ArrayList<GuiPanel> m_pages;
    private int m_currentPage;
}
