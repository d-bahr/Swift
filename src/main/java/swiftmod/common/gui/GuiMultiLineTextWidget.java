package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMultiLineTextWidget extends GuiFontWidget
{
    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height)
    {
        super(screen, width, height);
        m_text = new ArrayList<ITextComponent>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, FontRenderer font)
    {
        super(screen, width, height, font);
        m_text = new ArrayList<ITextComponent>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        m_text = new ArrayList<ITextComponent>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, FontRenderer font)
    {
        super(screen, x, y, width, height, font);
        m_text = new ArrayList<ITextComponent>();
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, ITextComponent text)
    {
        super(screen, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text)
    {
        super(screen, x, y, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, x, y, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, List<ITextComponent> text)
    {
        super(screen, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int width, int height, List<ITextComponent> text, FontRenderer font)
    {
        super(screen, width, height, font);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, List<ITextComponent> text)
    {
        super(screen, x, y, width, height);
        initText(text);
    }

    public GuiMultiLineTextWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, List<ITextComponent> text, FontRenderer font)
    {
        super(screen, x, y, width, height, font);
        initText(text);
    }

    private void initText(ITextComponent text)
    {
        m_text = new ArrayList<ITextComponent>();
        if (text != null)
            m_text.add(text);
    }

    private void initText(List<ITextComponent> text)
    {
        if (text != null)
            m_text = text;
        else
            m_text = new ArrayList<ITextComponent>();
    }

    public void addText(ITextComponent text)
    {
        if (text != null)
            m_text.add(text);
        onTextChanged();
    }

    public void addText(Collection<ITextComponent> text)
    {
        if (text != null)
            m_text.addAll(text);
        onTextChanged();
    }

    public void setText(ITextComponent text)
    {
        m_text.clear();
        if (text != null)
            m_text.add(text);
        onTextChanged();
    }

    public void setText(int line, ITextComponent text)
    {
        if (text != null)
            m_text.set(line, text);
        else
            m_text.set(line, StringTextComponent.EMPTY);
        onTextChanged();
    }

    public void setText(List<ITextComponent> text)
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

    public boolean removeText(ITextComponent text)
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

    public ITextComponent getText(int line)
    {
        return m_text.get(line);
    }

    public List<ITextComponent> getText()
    {
        return m_text;
    }

    protected void onTextChanged()
    {
    }

    protected List<ITextComponent> m_text;
}
