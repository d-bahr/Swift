package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMultiLineTextWidget extends GuiFontWidget
{
    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height);
        m_text = new ArrayList<Component>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, Font font)
    {
        super(screen, width, height, font);
        m_text = new ArrayList<Component>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        m_text = new ArrayList<Component>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Font font)
    {
        super(screen, x, y, width, height, font);
        m_text = new ArrayList<Component>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, Component text)
    {
        super(screen, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, Component text, Font font)
    {
        super(screen, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, List<Component> text)
    {
        super(screen, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, List<Component> text, Font font)
    {
        super(screen, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, List<Component> text)
    {
        super(screen, x, y, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, List<Component> text, Font font)
    {
        super(screen, x, y, width, height, font);
        initText(text);
    }

    private void initText(Component text)
    {
        m_text = new ArrayList<Component>();
        if (text != null)
            m_text.add(text);
    }

    private void initText(List<Component> text)
    {
        if (text != null)
            m_text = text;
        else
            m_text = new ArrayList<Component>();
    }

    public void addText(Component text)
    {
        if (text != null)
            m_text.add(text);
        onTextChanged();
    }

    public void addText(Collection<Component> text)
    {
        if (text != null)
            m_text.addAll(text);
        onTextChanged();
    }

    public void setText(Component text)
    {
        m_text.clear();
        if (text != null)
            m_text.add(text);
        onTextChanged();
    }

    public void setText(int line, Component text)
    {
        if (text != null)
            m_text.set(line, text);
        else
            m_text.set(line, TextComponent.EMPTY);
        onTextChanged();
    }

    public void setText(List<Component> text)
    {
        if (text == null)
            m_text.clear();
        else
            m_text = text;
        onTextChanged();
    }

    public void clearText()
    {
        m_text.clear();
        onTextChanged();
    }

    public boolean removeText(int line)
    {
        if (line >= 0 && line < m_text.size())
        {
            m_text.remove(line);
            onTextChanged();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean removeText(Component text)
    {
        for (int i = 0; i < m_text.size(); ++i)
        {
            if (m_text.get(i).getString().equals(text.getString()))
            {
                m_text.remove(i);
                onTextChanged();
                return true;
            }
        }

        return false;
    }

    public int numLines()
    {
        return m_text.size();
    }

    public Component getText(int line)
    {
        return m_text.get(line);
    }

    public List<Component> getText()
    {
        return m_text;
    }

    protected void onTextChanged()
    {
    }

    protected List<Component> m_text;
}
