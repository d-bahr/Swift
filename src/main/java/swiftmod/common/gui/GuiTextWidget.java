package swiftmod.common.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextWidget extends GuiFontWidget
{
    public GuiTextWidget(GuiContainerScreen<?> screen, int width, int height, Component text)
    {
        super(screen, width, height);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int width, int height, Component text, Font font)
    {
        super(screen, width, height, font);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height, font);
        m_text = text;
    }

    public void setText(Component text)
    {
        m_text = text;
        onTextChanged();
    }

    public Component getText()
    {
        return m_text;
    }

    protected void onTextChanged()
    {
    }

    protected void drawText(GuiGraphics graphics, float x, float y)
    {
        drawText(graphics, m_text, x, y);
    }

    protected void drawText(GuiGraphics graphics, float x, float y, TextColor color)
    {
        drawText(graphics, m_text, x, y, color);
    }

    protected void drawText(GuiGraphics graphics, float x, float y, int color)
    {
        drawText(graphics, m_text, x, y, color);
    }

    protected Component m_text;
}
