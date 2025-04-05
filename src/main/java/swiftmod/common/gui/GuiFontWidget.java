package swiftmod.common.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFontWidget extends GuiWidget
{
    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height, Component.empty());
        m_font = Minecraft.getInstance().font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height, Font font)
    {
        super(screen, width, height, Component.empty());
        m_font = font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, Component.empty());
        m_font = Minecraft.getInstance().font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Font font)
    {
        super(screen, x, y, width, height, Component.empty());
        m_font = font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public void setFont(Font font)
    {
        m_font = font;
    }

    public Font getFont()
    {
        return m_font;
    }

    public void setFontColor(TextColor c)
    {
        m_fontColor = c;
    }

    public TextColor getFontColor()
    {
        return m_fontColor;
    }

    public void setFontScale(float scale)
    {
        m_fontScale = scale;
    }
    
    public float getFontScale()
    {
        return m_fontScale;
    }

    public void setDrawDropShadow(boolean draw)
    {
        m_drawDropShadow = draw;
    }
    
    public boolean getDrawDropShadow()
    {
        return m_drawDropShadow;
    }

    protected void drawText(GuiGraphics graphics, String text, float x, float y)
    {
        drawText(graphics, Component.literal(text), x, y);
    }

    protected void drawText(GuiGraphics graphics, String text, float x, float y, TextColor color)
    {
        drawText(graphics, Component.literal(text), x, y, color);
    }

    protected void drawText(GuiGraphics graphics, String text, float x, float y, int color)
    {
        drawText(graphics, Component.literal(text), x, y, color);
    }

    protected void drawText(GuiGraphics graphics, Component text, float x, float y)
    {
        drawText(graphics, text, x, y, m_fontColor.getValue());
    }

    protected void drawText(GuiGraphics graphics, Component text, float x, float y, TextColor color)
    {
        drawText(graphics, text, x, y, color.getValue());
    }

    protected void drawText(GuiGraphics graphics, Component text, float x, float y, int color)
    {
    	// Note: There is no overload which takes floats (x,y) and also a Component,
    	// so we have to get the underlying formatted text. See GuiGraphics class.
    	graphics.drawString(m_font, text.getVisualOrderText(), x, y, color, m_drawDropShadow);
    }

    protected Font m_font;
    protected TextColor m_fontColor;
    protected float m_fontScale;
    protected boolean m_drawDropShadow;
}
