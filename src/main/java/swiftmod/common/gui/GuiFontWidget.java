package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFontWidget extends GuiWidget
{
    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height, StringTextComponent.EMPTY);
        m_font = Minecraft.getInstance().font;
        m_fontColor = Color.fromLegacyFormat(TextFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height, FontRenderer font)
    {
        super(screen, width, height, StringTextComponent.EMPTY);
        m_font = font;
        m_fontColor = Color.fromLegacyFormat(TextFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_font = Minecraft.getInstance().font;
        m_fontColor = Color.fromLegacyFormat(TextFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, FontRenderer font)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_font = font;
        m_fontColor = Color.fromLegacyFormat(TextFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public void setFont(FontRenderer font)
    {
        m_font = font;
    }

    public FontRenderer getFont()
    {
        return m_font;
    }

    public void setFontColor(Color c)
    {
        m_fontColor = c;
    }

    public Color getFontColor()
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

    protected void drawText(MatrixStack matrixStack, String text, float x, float y)
    {
        drawText(matrixStack, new StringTextComponent(text), x, y);
    }

    protected void drawText(MatrixStack matrixStack, String text, float x, float y, Color color)
    {
        drawText(matrixStack, new StringTextComponent(text), x, y, color);
    }

    protected void drawText(MatrixStack matrixStack, String text, float x, float y, int color)
    {
        drawText(matrixStack, new StringTextComponent(text), x, y, color);
    }

    protected void drawText(MatrixStack matrixStack, ITextComponent text, float x, float y)
    {
        drawText(matrixStack, text, x, y, m_fontColor.getValue());
    }

    protected void drawText(MatrixStack matrixStack, ITextComponent text, float x, float y, Color color)
    {
        drawText(matrixStack, text, x, y, color.getValue());
    }

    protected void drawText(MatrixStack matrixStack, ITextComponent text, float x, float y, int color)
    {
        if (m_drawDropShadow)
            m_font.drawShadow(matrixStack, text, x, y, color);
        else
            m_font.draw(matrixStack, text, x, y, color);
    }

    protected FontRenderer m_font;
    protected Color m_fontColor;
    protected float m_fontScale;
    protected boolean m_drawDropShadow;
}
