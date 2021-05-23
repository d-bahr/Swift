package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class GuiWidget extends Widget implements IDrawable
{
    public GuiWidget(GuiContainerScreen<?> screen, int width, int height, ITextComponent title)
    {
        this(screen, 0, 0, width, height, title);
    }

    public GuiWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent title)
    {
        super(x, y, width, height, title);
        m_playClickOnPress = true;
        m_screen = screen;
        m_parent = null;
        m_children = new ArrayList<GuiWidget>();
        z = 0.0;
        m_canLoseFocus = true;
    }

    public int top()
    {
        return y;
    }

    public int bottom()
    {
        return top() + height();
    }

    public int left()
    {
        return x;
    }

    public int right()
    {
        return left() + width();
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public int topAbsolute()
    {
        if (m_parent == null)
            return m_screen.getGuiTop() + top();
        else
            return m_parent.topAbsolute() + top();
    }

    public int bottomAbsolute()
    {
        return topAbsolute() + height();
    }

    public int leftAbsolute()
    {
        if (m_parent == null)
            return m_screen.getGuiLeft() + left();
        else
            return m_parent.leftAbsolute() + left();
    }

    public int rightAbsolute()
    {
        return leftAbsolute() + width();
    }

    public boolean containsMouse(int mouseX, int mouseY)
    {
        return mouseX >= leftAbsolute() && mouseX < rightAbsolute() && mouseY >= topAbsolute()
                && mouseY < bottomAbsolute();
    }

    public boolean containsMouse(double mouseX, double mouseY)
    {
        return containsMouse((int) mouseX, (int) mouseY);
    }

    public boolean containsMouseRelative(int mouseX, int mouseY)
    {
        return mouseX >= left() && mouseX < right() && mouseY >= top()
                && mouseY < bottom();
    }

    public boolean containsMouseRelative(double mouseX, double mouseY)
    {
        return containsMouseRelative((int) mouseX, (int) mouseY);
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public final void addChild(GuiWidget child)
    {
        child.m_parent = this;

        for (int i = 0; i < m_children.size(); ++i)
        {
            if (child.z < m_children.get(i).z)
            {
                m_children.add(i, child);
                return;
            }
        }
        m_children.add(child);
    }

    protected final void removeChild(GuiWidget child)
    {
        child.hide();
        m_children.remove(child);
        if (getScreen().getFocusedWidget() == child)
            getScreen().clearFocus();
    }

    /**
     * DO NOT MODIFY THE RETURN VALUE OF THIS FUNCTION, OR YOU WILL HORRIBLY BREAK THINGS.
     */
    public List<GuiWidget> getChildren()
    {
        return m_children;
    }

    public GuiWidget getParent()
    {
        return m_parent;
    }

    public GuiContainerScreen<?> getScreen()
    {
        return m_screen;
    }

    public Minecraft getMinecraft()
    {
        return m_screen.getMinecraft();
    }

    @SuppressWarnings("resource")
    public ClientPlayerEntity getPlayer()
    {
        return m_screen.getMinecraft().player;
    }

    public void show()
    {
        visible = true;
    }

    public void hide()
    {
        visible = false;
    }

    public void init()
    {
    }

    public void requestFocus()
    {
        getScreen().requestFocus(this);
    }

    public void setFocused(boolean focused)
    {
        if (isFocused() != focused)
        {
            if (focused || (!focused && m_canLoseFocus))
            {
                super.setFocused(focused);
                onFocusedChanged(focused);
            }
        }
    }

    public void setCanLoseFocus(boolean canLoseFocus)
    {
        m_canLoseFocus = canLoseFocus;
    }
    
    public boolean getCanLoseFocus()
    {
        return m_canLoseFocus;
    }

    protected boolean onKeyPress(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    protected boolean onKeyRelease(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    protected boolean onCharTyped(char codePoint, int modifiers)
    {
        return false;
    }

    protected boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        return false;
    }

    protected boolean onMouseRelease(MouseButton button, double mouseX, double mouseY)
    {
        return false;
    }

    protected boolean onMouseDrag(MouseButton button, double mouseX, double mouseY, double dragX, double dragY)
    {
        return false;
    }

    protected boolean onMouseScroll(double mouseX, double mouseY, double delta)
    {
        return false;
    }

    private boolean onKeyPressWorker(int keyCode, int scanCode, int modifiers)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.onKeyPressWorker(keyCode, scanCode, modifiers))
                    return true;
            }

            return onKeyPress(keyCode, scanCode, modifiers);
        }
        else
        {
            return false;
        }
    }

    private boolean onKeyReleaseWorker(int keyCode, int scanCode, int modifiers)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.onKeyReleaseWorker(keyCode, scanCode, modifiers))
                    return true;
            }

            return onKeyRelease(keyCode, scanCode, modifiers);
        }
        else
        {
            return false;
        }
    }

    private boolean onCharTypedWorker(char codePoint, int modifiers)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.onCharTypedWorker(codePoint, modifiers))
                    return true;
            }

            return onCharTyped(codePoint, modifiers);
        }
        else
        {
            return false;
        }
    }

    private boolean onMousePressWorker(MouseButton button, double mouseX, double mouseY)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.containsMouse(mouseX, mouseY))
                {
                    if (child.onMousePressWorker(button, mouseX, mouseY))
                        return true;
                }
            }

            boolean handled = onMousePress(button, mouseX, mouseY);
            if (handled)
            {
                requestFocus();
                if (m_playClickOnPress)
                    playDownSound(Minecraft.getInstance().getSoundManager());
            }
            return handled;
        }
        else
        {
            return false;
        }
    }

    private boolean onMouseReleaseWorker(MouseButton button, double mouseX, double mouseY)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.containsMouse(mouseX, mouseY))
                {
                    if (child.onMouseReleaseWorker(button, mouseX, mouseY))
                        return true;
                }
            }

            return onMouseRelease(button, mouseX, mouseY);
        }
        else
        {
            return false;
        }
    }

    private boolean onMouseDragWorker(MouseButton button, double mouseX, double mouseY, double dragX, double dragY)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.containsMouse(mouseX, mouseY))
                {
                    if (child.onMouseDragWorker(button, mouseX, mouseY, dragX, dragY))
                        return true;
                }
            }

            return onMouseDrag(button, mouseX, mouseY, dragX, dragY);
        }
        else
        {
            return false;
        }
    }

    private boolean onMouseScrollWorker(double mouseX, double mouseY, double delta)
    {
        if (active && visible)
        {
            for (int i = 0; i < m_children.size(); ++i)
            {
                GuiWidget child = m_children.get(i);
                if (child.containsMouse(mouseX, mouseY))
                {
                    if (child.onMouseScrollWorker(mouseX, mouseY, delta))
                        return true;
                }
            }

            return onMouseScroll(mouseX, mouseY, delta);
        }
        else
        {
            return false;
        }
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (active && visible)
        {
            if (keyCode != 257 && keyCode != 32 && keyCode != 335)
            {
                return onKeyPressWorker(keyCode, scanCode, modifiers);
            }
            else
            {
                return onMousePressWorker(MouseButton.Left, -1, -1);
            }
        }
        return false;
    }

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if (active && visible)
        {
            return onKeyReleaseWorker(keyCode, scanCode, modifiers);
        }
        else
        {
            return false;
        }
    }

    @Override
    public final boolean charTyped(char codePoint, int modifiers)
    {
        if (active && visible)
        {
            return onCharTypedWorker(codePoint, modifiers);
        }
        else
        {
            return false;
        }
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (active && visible)
        {
            if (isValidClickButton(button))
            {
                if (containsMouse(mouseX, mouseY))
                {
                    return onMousePressWorker(MouseButton.from(button), mouseX, mouseY);
                }
            }
        }
        return false;
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (active && visible)
        {
            if (isValidClickButton(button))
            {
                if (containsMouse(mouseX, mouseY))
                {
                    return onMouseReleaseWorker(MouseButton.from(button), mouseX, mouseY);
                }
            }
        }
        return false;
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (active && visible)
        {
            if (isValidClickButton(button))
            {
                if (containsMouse(mouseX, mouseY))
                {
                    return onMouseDragWorker(MouseButton.from(button), mouseX, mouseY, dragX, dragY);
                }
            }
        }
        return false;
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (active && visible)
        {
            if (containsMouse(mouseX, mouseY))
            {
                return onMouseScrollWorker(mouseX, mouseY, delta);
            }
        }
        return false;
    }

    public void tick()
    {
        for (int i = 0; i < m_children.size(); ++i)
            m_children.get(i).tick();
    }

    protected boolean isValidClickButton(int button)
    {
        // Left click, right click, middle (scroll-wheel) click.
        // User can also just override to process additional mouse buttons.
        return button == 0 || button == 1 || button == 2;
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {

    }

    private final void drawWorker(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            draw(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.pushPose();
            matrixStack.translate(x, y, z);
            for (int i = 0; i < m_children.size(); ++i)
                m_children.get(i).drawWorker(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        }
    }

    @Override
    public final void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            int left;
            int top;
            if (m_parent == null)
            {
                left = m_screen.getGuiLeft();
                top = m_screen.getGuiTop();
            }
            else
            {
                left = m_parent.leftAbsolute();
                top = m_parent.topAbsolute();
            }

            matrixStack.pushPose();
            matrixStack.translate(left, top, 0.0);
            drawWorker(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        }
    }

    public final void setZ(double z)
    {
        this.z = z;
        reconcileZChange();
    }

    public final double getZ()
    {
        return z;
    }

    private void reconcileZChange()
    {
        if (m_parent != null)
        {
            // Partial sort; the children will already be almost-sorted, so this will
            // be much faster than a merge sort or something else (O(n) vs. O(n log n)).
            // This sort works as follows:
            // We assume that only one value is out of order. It needs to move either up
            // the list or down the list. So we do one pass in each direction. When we
            // find the out-of-order item, it will either trickle up or trickle down.
            List<GuiWidget> siblings = m_parent.m_children;
            for (int i = 1; i < siblings.size(); ++i)
            {
                GuiWidget a = siblings.get(i - 1);
                GuiWidget b = siblings.get(i);
                if (b.z < a.z)
                {
                    // Swap them.
                    siblings.set(i - 1, b);
                    siblings.set(i, a);
                }
            }

            for (int i = siblings.size() - 2; i >= 0; --i)
            {
                GuiWidget a = siblings.get(i);
                GuiWidget b = siblings.get(i + 1);
                if (b.z < a.z)
                {
                    // Swap them.
                    siblings.set(i, b);
                    siblings.set(i + 1, a);
                }
            }
        }
    }

    /*
     * Overriding Vanilla/Forge to handle relative x/y position.
     */
    @Override
    protected boolean clicked(double mouseX, double mouseY)
    {
        return active && visible && containsMouse(mouseX, mouseY);
    }

    /*
     * Overriding Vanilla/Forge to handle relative x/y position.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return active && visible && containsMouse(mouseX, mouseY);
    }

    protected boolean m_playClickOnPress;
    private GuiContainerScreen<?> m_screen;
    private GuiWidget m_parent;
    private List<GuiWidget> m_children;
    private double z;
    private boolean m_canLoseFocus;
}
