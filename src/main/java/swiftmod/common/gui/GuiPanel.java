package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
        this(screen, width, height, StringTextComponent.EMPTY);
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, ITextComponent title)
    {
        super(screen, width, height, title);
        m_backgroundTexture = null;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, StringTextComponent.EMPTY);
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent title)
    {
        super(screen, x, y, width, height, title);
        m_backgroundTexture = null;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture)
    {
        this(screen, width, height, backgroundTexture, StringTextComponent.EMPTY);
    }

    public GuiPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture, ITextComponent title)
    {
        super(screen, width, height, title);
        m_backgroundTexture = backgroundTexture;
        m_drawBackground = true;
        m_playClickOnPress = false;
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation backgroundTexture)
    {
        this(screen, x, y, width, height, backgroundTexture, StringTextComponent.EMPTY);
    }

    public GuiPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation backgroundTexture, ITextComponent title)
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
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
        if (m_drawBackground)
            drawBackground(matrixStack, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("deprecation")
    protected void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (m_backgroundTexture != null)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bind(m_backgroundTexture);
    
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    protected ResourceLocation m_backgroundTexture;
    protected boolean m_drawBackground;
}
