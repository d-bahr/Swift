package swiftmod.common.gui;

import java.util.Stack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class GuiPanelStack extends GuiWidget
{
    @FunctionalInterface
    public interface StackChangedCallback
    {
        void onStackChanged(GuiPanelStack stack);
    };

    public GuiPanelStack(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, 0, 0, TextComponent.EMPTY);
    }

    public GuiPanelStack(GuiContainerScreen<?> screen, int x, int y, Component title)
    {
        this(screen, x, y, 0, 0, title);
    }

    public GuiPanelStack(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, TextComponent.EMPTY);
    }

    public GuiPanelStack(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component title)
    {
        super(screen, x, y, width, height, title);
        m_stack = new Stack<GuiPanel>();
        m_backButton = null;
        m_stackChangedCallback = null;
        m_playClickOnPress = false;
    }

    public void setStackChangedCallback(StackChangedCallback callback)
    {
        m_stackChangedCallback = callback;
    }

    public void setDefaultBackButton()
    {
        setDefaultBackButton(0, 0, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT);
    }

    public void setDefaultBackButton(int x, int y)
    {
        setBackButton(new GuiBackButton(getScreen(), x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT));
    }

    public void setDefaultBackButton(int x, int y, int width, int height)
    {
        setBackButton(new GuiBackButton(getScreen(), x, y, width, height));
    }

    public void setBackButton(GuiButton button)
    {
        if (m_backButton != null)
        {
            m_backButton.hide();
            m_backButton.setClickCallback(null);
            removeChild(m_backButton);
        }
        m_backButton = button;
        m_backButton.setClickCallback(this::onBackButtonPressed);
        if (size() > 1)
            m_backButton.show();
        else
            m_backButton.hide();
        addChild(m_backButton);
    }

    private void onBackButtonPressed(GuiWidget widget, MouseButton mouseButton)
    {
        pop(true);
    }

    public GuiPanel push()
    {
        GuiPanel panel = new GuiPanel(getScreen(), width, height);
        push(panel);
        return panel;
    }

    public void push(GuiPanel panel)
    {
        if (!empty())
            topPanel().hide();
        panel.show();
        m_stack.push(panel);
        addChild(panel);
        updateBackButtonVisibility();
        invokeStackChangedCallback();
    }

    public void pop()
    {
        if (!m_stack.isEmpty())
        {
            m_stack.peek().hide();
            removeChild(m_stack.peek());
            m_stack.pop();
            if (!m_stack.isEmpty())
                m_stack.peek().show();
            updateBackButtonVisibility();
            invokeStackChangedCallback();
        }
    }

    public void pop(boolean leaveOne)
    {
        if (leaveOne)
        {
            if (m_stack.size() > 1)
                pop();
        }
        else
        {
            pop();
        }
    }

    public GuiPanel topPanel()
    {
        return m_stack.peek();
    }

    public int size()
    {
        return m_stack.size();
    }

    public boolean empty()
    {
        return m_stack.isEmpty();
    }

    public void clear(boolean leaveOne)
    {
        if (leaveOne)
        {
            while (m_stack.size() > 1)
            {
                m_stack.peek().hide();
                removeChild(m_stack.peek());
                m_stack.pop();
            }

            if (!m_stack.empty())
            {
                m_stack.peek().show();
                updateBackButtonVisibility();
            }

            invokeStackChangedCallback();
        }
        else
        {
            clear();
        }
    }

    public void clear()
    {
        while (!m_stack.isEmpty())
        {
            m_stack.peek().hide();
            removeChild(m_stack.peek());
            m_stack.pop();
        }
        updateBackButtonVisibility();
        invokeStackChangedCallback();
    }

    private void updateBackButtonVisibility()
    {
        if (m_backButton != null)
        {
            if (size() > 1)
                m_backButton.show();
            else
                m_backButton.hide();
        }
    }

    private void invokeStackChangedCallback()
    {
        if (m_stackChangedCallback != null)
            m_stackChangedCallback.onStackChanged(this);
    }

    private Stack<GuiPanel> m_stack;
    private GuiButton m_backButton;
    private StackChangedCallback m_stackChangedCallback;
}
