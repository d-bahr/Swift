package swiftmod.common.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiButton extends GuiWidget
{
    public GuiButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, s_baseTexture, s_highlightedTexture);
    }

    public GuiButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_tooltip = new GuiTooltip(screen, 0, 0, width, height);
        addChild(m_tooltip);
        m_onClick = null;
        m_baseTexture = baseTexture;
        m_highlightedTexture = highlightedTexture;
        m_inactiveTexture = null;
        m_drawBackground = true;
    }

    public GuiButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, IClickable onClick)
    {
        this(screen, x, y, width, height, s_baseTexture, s_highlightedTexture, onClick);
    }

    public GuiButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, IClickable onClick)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_tooltip = new GuiTooltip(screen, 0, 0, width, height);
        addChild(m_tooltip);
        m_onClick = onClick;
        m_baseTexture = baseTexture;
        m_highlightedTexture = highlightedTexture;
        m_inactiveTexture = null;
        m_drawBackground = true;
    }

    public void setClickCallback(IClickable onClick)
    {
        m_onClick = onClick;
    }

    public void setBackgroundTexture(ResourceLocation texture)
    {
        m_baseTexture = texture;
    }

    public void setBackgroundHighlightedTexture(ResourceLocation texture)
    {
        m_highlightedTexture = texture;
    }

    public void setBackgroundInactiveTexture(ResourceLocation texture)
    {
        m_inactiveTexture = texture;
    }

    public void setTooltip(ITextComponent text)
    {
        m_tooltip.setText(text);
    }

    public void setTooltip(List<ITextComponent> text)
    {
        m_tooltip.setText(text);
    }

    protected boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        if (m_onClick != null)
        {
            m_onClick.onClick(this, button);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean onMouseRelease(MouseButton button, double mouseX, double mouseY)
    {
        return false;
    }

    protected boolean onMouseDrag(MouseButton button, double mouseX, double mouseY, double dragX, double dragY)
    {
        return false;
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
        Minecraft minecraft = Minecraft.getInstance();
        if (!active && m_inactiveTexture != null)
        {
            minecraft.getTextureManager().bind(m_inactiveTexture);
        }
        else if (containsMouse(mouseX, mouseY))
        {
            minecraft.getTextureManager().bind(m_highlightedTexture);
        }
        else
        {
            minecraft.getTextureManager().bind(m_baseTexture);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, 0, 0, width, height, width, height);
        //renderBg(matrixStack, minecraft, mouseX, mouseY);
        //if (isHovered())
        //    renderToolTip(matrixStack, mouseX, mouseY);
        //FontRenderer fontrenderer = minecraft.fontRenderer;
        //int j = getFGColor();
        //drawCenteredString(matrixStack, fontrenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
    }

    public static final ResourceLocation s_baseTexture = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/button_base.png");
    public static final ResourceLocation s_highlightedTexture = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/button_highlighted.png");

    protected GuiTooltip m_tooltip;
    protected IClickable m_onClick;
    protected ResourceLocation m_baseTexture;
    protected ResourceLocation m_highlightedTexture;
    protected ResourceLocation m_inactiveTexture;
    protected boolean m_drawBackground;
}
