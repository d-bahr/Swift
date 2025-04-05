package swiftmod.common.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        super(screen, x, y, width, height, Component.empty());
        m_requestFocusOnPress = false;
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
        super(screen, x, y, width, height, Component.empty());
        m_requestFocusOnPress = false;
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

    public void setTooltip(Component text)
    {
        m_tooltip.setText(text);
    }

    public void setTooltip(List<Component> text)
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
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);
        if (m_drawBackground)
            drawBackground(graphics, mouseX, mouseY, partialTicks);
    }

    protected void drawBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
    	ResourceLocation texture;
        if (!active && m_inactiveTexture != null)
        {
        	texture = m_inactiveTexture;
        }
        else if (containsMouse(mouseX, mouseY))
        {
        	texture = m_highlightedTexture;
        }
        else
        {
        	texture = m_baseTexture;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);
    }

    public static final ResourceLocation s_baseTexture = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/button_base.png");
    public static final ResourceLocation s_highlightedTexture = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/button_highlighted.png");
    public static final ResourceLocation s_inactiveTexture = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/button_inactive.png");

    protected GuiTooltip m_tooltip;
    protected IClickable m_onClick;
    protected ResourceLocation m_baseTexture;
    protected ResourceLocation m_highlightedTexture;
    protected ResourceLocation m_inactiveTexture;
    protected boolean m_drawBackground;
}
