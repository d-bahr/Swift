package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextWidget extends GuiFontWidget
{
    public GuiTextWidget(GuiContainerScreen<?> screen, int width, int height, ITextComponent text)
    {
        super(screen, width, height);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, width, height, font);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text)
    {
        super(screen, x, y, width, height);
        m_text = text;
    }

    public GuiTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, x, y, width, height, font);
        m_text = text;
    }

    public void setText(ITextComponent text)
    {
        m_text = text;
        onTextChanged();
    }

    public ITextComponent getText()
    {
        return m_text;
    }

    protected void onTextChanged()
    {
    }

    protected void drawText(MatrixStack matrixStack, float x, float y)
    {
        drawText(matrixStack, m_text, x, y);
    }

    protected void drawText(MatrixStack matrixStack, float x, float y, Color color)
    {
        drawText(matrixStack, m_text, x, y, color);
    }

    protected void drawText(MatrixStack matrixStack, float x, float y, int color)
    {
        drawText(matrixStack, m_text, x, y, color);
    }

    protected ITextComponent m_text;
}
