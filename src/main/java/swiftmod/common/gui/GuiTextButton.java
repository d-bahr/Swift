package swiftmod.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextButton extends GuiButton
{
    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text)
    {
        super(screen, x, y, width, height);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, x, y, width, height);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, FontRenderer font, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, ITextComponent text)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, ITextComponent text, IClickable onClick)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture, onClick);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, ITextComponent text, FontRenderer font)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, ITextComponent text, FontRenderer font, IClickable onClick)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture, onClick);
        createLabel(text, font);
    }

    public void createLabel(ITextComponent text)
    {
        Minecraft mc = Minecraft.getInstance();
        createLabel(text, mc.font);
    }

    public void createLabel(ITextComponent text, FontRenderer font)
    {
        m_label = new GuiLabel(getScreen(), 0, 0, width, height, text, font);
        m_label.setAlignment(GuiVerticalAlignment.Middle, GuiHorizontalAlignment.Center);
        m_label.setDrawDropShadow(true);
        m_label.setFontColor(Color.fromLegacyFormat(TextFormatting.WHITE));
        addChild(m_label);
    }

    public void setText(ITextComponent text)
    {
        m_label.setText(text);
        onTextChanged();
    }

    public ITextComponent getText()
    {
        return m_label.getText();
    }

    protected void onTextChanged()
    {
    }

    public void setFont(FontRenderer font)
    {
        m_label.setFont(font);
    }

    public FontRenderer getFont()
    {
        return m_label.getFont();
    }

    public void setFontColor(Color c)
    {
        m_label.setFontColor(c);
    }

    public Color getFontColor()
    {
        return m_label.getFontColor();
    }

    public void setDrawDropShadow(boolean draw)
    {
        m_label.setDrawDropShadow(draw);
    }

    public boolean getDrawDropShadow()
    {
        return m_label.getDrawDropShadow();
    }

    public void setTextVerticalAlignment(GuiVerticalAlignment alignment)
    {
        m_label.setVerticalAlignment(alignment);
    }

    public GuiVerticalAlignment getTextVerticalAlignment()
    {
        return m_label.getVerticalAlignment();
    }

    public void setTextHorizontalAlignment(GuiHorizontalAlignment alignment)
    {
        m_label.setHorizontalAlignment(alignment);
    }

    public GuiHorizontalAlignment getTextHorizontalAlignment()
    {
        return m_label.getHorizontalAlignment();
    }

    public void setTextAlignment(GuiVerticalAlignment verticalAlignment, GuiHorizontalAlignment horizontalAlignment)
    {
        m_label.setAlignment(verticalAlignment, horizontalAlignment);
    }

    private GuiLabel m_label;
}
