package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFontWidget extends GuiWidget
{
    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height, TextComponent.EMPTY);
        m_font = Minecraft.getInstance().font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int width, int height, Font font)
    {
        super(screen, width, height, TextComponent.EMPTY);
        m_font = font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    @SuppressWarnings("resource")
    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, TextComponent.EMPTY);
        m_font = Minecraft.getInstance().font;
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        m_fontScale = 1.0f;
        m_drawDropShadow = false;
    }

    public GuiFontWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Font font)
    {
        super(screen, x, y, width, height, TextComponent.EMPTY);
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

    protected void drawText(PoseStack matrixStack, String text, float x, float y)
    {
        drawText(matrixStack, new TextComponent(text), x, y);
    }

    protected void drawText(PoseStack matrixStack, String text, float x, float y, TextColor color)
    {
        drawText(matrixStack, new TextComponent(text), x, y, color);
    }

    protected void drawText(PoseStack matrixStack, String text, float x, float y, int color)
    {
        drawText(matrixStack, new TextComponent(text), x, y, color);
    }

    protected void drawText(PoseStack matrixStack, Component text, float x, float y)
    {
        drawText(matrixStack, text, x, y, m_fontColor.getValue());
    }

    protected void drawText(PoseStack matrixStack, Component text, float x, float y, TextColor color)
    {
        drawText(matrixStack, text, x, y, color.getValue());
    }

    protected void drawText(PoseStack matrixStack, Component text, float x, float y, int color)
    {
        if (m_drawDropShadow)
            m_font.drawShadow(matrixStack, text, x, y, color);
        else
            m_font.draw(matrixStack, text, x, y, color);
    }

    protected Font m_font;
    protected TextColor m_fontColor;
    protected float m_fontScale;
    protected boolean m_drawDropShadow;
}
