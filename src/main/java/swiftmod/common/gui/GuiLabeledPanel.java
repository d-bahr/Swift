package swiftmod.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLabeledPanel extends GuiPanel
{
    public GuiLabeledPanel(GuiContainerScreen<?> screen, int width, int height, Component label)
    {
        super(screen, width, height, Component.empty());
        createLabel(label);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int width, int height, Component label, Font font)
    {
        super(screen, width, height, Component.empty());
        createLabel(label, font);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component label)
    {
        super(screen, x, y, width, height, Component.empty());
        createLabel(label);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component label,
            Font font)
    {
        super(screen, x, y, width, height, Component.empty());
        createLabel(label, font);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture,
            Component label)
    {
        super(screen, width, height, Component.empty());
        createLabel(label);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int width, int height, ResourceLocation backgroundTexture,
            Component label, Font font)
    {
        super(screen, width, height, Component.empty());
        createLabel(label, font);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation backgroundTexture, Component label)
    {
        super(screen, x, y, width, height, Component.empty());
        createLabel(label);
    }

    public GuiLabeledPanel(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation backgroundTexture, Component label, Font font)
    {
        super(screen, x, y, width, height, Component.empty());
        createLabel(label, font);
    }

    public void createLabel(Component text)
    {
        Minecraft mc = Minecraft.getInstance();
        createLabel(text, mc.font);
    }

    public void createLabel(Component text, Font font)
    {
        m_label = new GuiLabel(getScreen(), 0, 0, width, height, text, font);
        m_label.setAlignment(GuiVerticalAlignment.Top, GuiHorizontalAlignment.Left);
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

    public void setLabelPosition(int x, int y)
    {
        m_label.setPosition(x, y);
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
