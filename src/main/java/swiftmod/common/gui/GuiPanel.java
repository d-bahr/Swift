package swiftmod.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Simple container for other widgets.
 */
@OnlyIn(Dist.CLIENT)
public class GuiPanel extends GuiWidget
{
    public GuiPanel(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, width, height, Component.empty());
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, Component title)
    {
        super(screen, width, height, title);
        m_backgroundTexture = null;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, Component.empty());
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component title)
    {
        super(screen, x, y, width, height, title);
        m_backgroundTexture = null;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture)
    {
        this(screen, width, height, backgroundTexture, Component.empty());
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture, Component title)
    {
        super(screen, width, height, title);
        m_backgroundTexture = backgroundTexture;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation backgroundTexture)
    {
        this(screen, x, y, width, height, backgroundTexture, Component.empty());
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation backgroundTexture, Component title)
    {
        super(screen, x, y, width, height, title);
        m_backgroundTexture = backgroundTexture;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public void setBackgroundTexture(ResourceLocation texture)
    {
        m_backgroundTexture = texture;
    }

    public ResourceLocation getBackgroundTexture()
    {
        return m_backgroundTexture;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);
        if (m_drawBackground)
            drawBackground(graphics, mouseX, mouseY, partialTicks);
    }

    protected void drawBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (m_backgroundTexture != null)
        {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            graphics.blit(m_backgroundTexture, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    protected ResourceLocation m_backgroundTexture;
    protected boolean m_drawBackground;
}
