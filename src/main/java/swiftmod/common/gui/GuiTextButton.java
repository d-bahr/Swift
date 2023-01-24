package swiftmod.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextButton extends GuiButton
{
    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, Component text)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, Component text, IClickable onClick)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture, onClick);
        createLabel(text);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, Component text, Font font)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture);
        createLabel(text, font);
    }

    public GuiTextButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation baseTexture,
            ResourceLocation highlightedTexture, Component text, Font font, IClickable onClick)
    {
        super(screen, x, y, width, height, baseTexture, highlightedTexture, onClick);
        createLabel(text, font);
    }

    public void createLabel(Component text)
    {
        Minecraft mc = Minecraft.getInstance();
        createLabel(text, mc.font);
    }

    public void createLabel(Component text, Font font)
    {
        m_label = new GuiLabel(getScreen(), 0, 0, width, height, text, font);
        m_label.setAlignment(GuiVerticalAlignment.Middle, GuiHorizontalAlignment.Center);
        m_label.setDrawDropShadow(true);
        m_label.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE));
        addChild(m_label);
    }

    public void setText(Component text)
    {
        m_label.setText(text);
        onTextChanged();
    }

    public Component getText()
    {
        return m_label.getText();
    }

    protected void onTextChanged()
    {
    }

    public void setFont(Font font)
    {
        m_label.setFont(font);
    }

    public Font getFont()
    {
        return m_label.getFont();
    }

    public void setFontColor(TextColor c)
    {
        m_label.setFontColor(c);
    }

    public TextColor getFontColor()
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
