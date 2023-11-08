package swiftmod.common.gui;

import java.awt.Point;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Color;
import swiftmod.common.SwiftUtils;

@OnlyIn(Dist.CLIENT)
public class SideIOConfigurationWidget extends GuiWidget
{
    @FunctionalInterface
    public interface StateChangedCallback
    {
        void onStateChanged(Direction dir, byte state);
    }

    public SideIOConfigurationWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public SideIOConfigurationWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        super(screen, x, y, 54, 54, Component.empty());
        Direction[] dirs = Direction.values();
        m_buttons = new ColoredDirectionButton[dirs.length];
        for (int i = 0; i < dirs.length; ++i)
        {
            Direction dir = dirs[i];
            Point p = getButtonLocation(dir);
            m_buttons[i] = new ColoredDirectionButton(screen, p.x, p.y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT,
                    (button, state) -> onStateChanged(button, dir, state));
            m_buttons[i].setDirection(dir);
            setTooltip(dir, (byte)0);
            addChild(m_buttons[i]);
        }
    }

    public void setStateChangedCallback(StateChangedCallback callback)
    {
        m_stateChangedCallback = callback;
    }

    public void setState(Direction dir, byte state)
    {
        m_buttons[SwiftUtils.dirToIndex(dir)].setState(state);
        setTooltip(dir, state);
    }

    public void setStates(byte[] states)
    {
        int len = Math.min(states.length, m_buttons.length);
        for (int i = 0; i < len; ++i)
        {
            m_buttons[i].setState(states[i]);
            setTooltip(SwiftUtils.indexToDir(i), states[i]);
        }
    }

    public byte getState(Direction dir)
    {
        return m_buttons[SwiftUtils.dirToIndex(dir)].getState();
    }

    public byte[] getStates()
    {
        byte[] b = new byte[m_buttons.length];
        for (int i = 0; i < m_buttons.length; ++i)
            b[i] = m_buttons[i].getState();
        return b;
    }

    protected void onStateChanged(ColoredDirectionButton button, Direction direction, byte state)
    {
        setTooltip(direction, state);
        if (m_stateChangedCallback != null)
            m_stateChangedCallback.onStateChanged(direction, state);
    }

    private void setTooltip(Direction direction, byte state)
    {
        String name = direction.getName();
        String cap = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + ": ";
        if (state == 0)
            cap += "Disabled";
        else if (state == 17)
            cap += "Any";
        else
            cap += Color.fromIndex(state).getName();
        m_buttons[SwiftUtils.dirToIndex(direction)].setTooltip(Component.literal(cap));
    }

    private static Point getButtonLocation(Direction direction)
    {
        // Locations:
        //   U
        // W S E
        // N D
        switch (direction)
        {
        default:
        case UP:
            return new Point(18, 0);
        case DOWN:
            return new Point(18, 36);
        case WEST:
            return new Point(0, 18);
        case EAST:
            return new Point(36, 18);
        case NORTH:
            return new Point(0, 36);
        case SOUTH:
            return new Point(18, 18);
        }
    }

    protected ColoredDirectionButton[] m_buttons;
    private StateChangedCallback m_stateChangedCallback;
}
