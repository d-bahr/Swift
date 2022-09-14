package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Simple container for other widgets.
 */
@OnlyIn(Dist.CLIENT)
public class GuiPanel extends GuiWidget
{
    public GuiPanel(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, width, height, TextComponent.EMPTY);
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
        this(screen, x, y, width, height, TextComponent.EMPTY);
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
        this(screen, width, height, backgroundTexture, TextComponent.EMPTY);
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
        this(screen, x, y, width, height, backgroundTexture, TextComponent.EMPTY);
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
    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
        if (m_drawBackground)
            drawBackground(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void drawBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (m_backgroundTexture != null)
        {
        	RenderSystem.setShaderTexture(0, m_backgroundTexture);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    protected ResourceLocation m_backgroundTexture;
    protected boolean m_drawBackground;
}
