package swiftmod.common.gui;

import java.awt.Point;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.SwiftUtils;

@OnlyIn(Dist.CLIENT)
public class DirectionSelectionWidget extends GuiWidget
{
    @FunctionalInterface
    public interface DirectionButtonPressCallback
    {
        void onDirectionButtonPress(Direction direction);
    }

    public DirectionSelectionWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public DirectionSelectionWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        super(screen, x, y, 54, 54, Component.empty());
        m_buttons = new GuiButton[Direction.values().length];
        for (Direction d : Direction.values())
            clearItemForDirection(d);
        m_directionButtonPressCallback = null;
    }

    public void setDirectionButtonPressCallback(DirectionButtonPressCallback callback)
    {
        m_directionButtonPressCallback = callback;
    }

    public void setItemForDirection(Direction dir, ItemStack itemStack)
    {
        Point loc = getButtonLocation(dir);
        int dirIndex = SwiftUtils.dirToIndex(dir);

        if (m_buttons[dirIndex] != null)
            removeChild(m_buttons[dirIndex]);

        if (itemStack != null && !itemStack.isEmpty())
        {
            m_buttons[dirIndex] = new GuiItemTextureButton(getScreen(), loc.x, loc.y, SwiftGui.BUTTON_WIDTH,
                    SwiftGui.BUTTON_HEIGHT, 3, 3, itemStack, (button, mouseButton) ->
                    {
                        onDirectionButtonPress(dir);
                    });
            m_buttons[dirIndex].setBackgroundInactiveTexture(GuiButton.s_inactiveTexture);
        }
        else
        {
            ResourceLocation texture = DIRECTION_ICONS[dirIndex];
            m_buttons[dirIndex] = new GuiTextureButton(getScreen(), loc.x, loc.y, SwiftGui.BUTTON_WIDTH,
                    SwiftGui.BUTTON_HEIGHT, texture, (button, mouseButton) ->
                    {
                        onDirectionButtonPress(dir);
                    });
            m_buttons[dirIndex].setBackgroundInactiveTexture(GuiButton.s_inactiveTexture);
        }

        String name = dir.getName();
        String cap = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        m_buttons[dirIndex].setTooltip(Component.literal(cap));

        if (m_buttons[dirIndex] != null)
            addChild(m_buttons[dirIndex]);
    }
    
    public void setDirectionEnabled(Direction dir, boolean enabled)
    {
        int dirIndex = SwiftUtils.dirToIndex(dir);
        if (m_buttons[dirIndex] != null)
        	m_buttons[dirIndex].active = enabled;
    }

    public void clearItemForDirection(Direction dir)
    {
        setItemForDirection(dir, null);
    }

    protected void onDirectionButtonPress(Direction direction)
    {
        if (m_directionButtonPressCallback != null)
            m_directionButtonPressCallback.onDirectionButtonPress(direction);
    }

    private static ResourceLocation[] getDirectionIcons()
    {
        Direction[] dirs = Direction.values();
        ResourceLocation[] resources = new ResourceLocation[dirs.length];
        for (int i = 0; i < dirs.length; ++i)
        {
            String loc;
            switch (dirs[i])
            {
            case UP:
                loc = "textures/gui/up.png";
                break;
            case DOWN:
                loc = "textures/gui/down.png";
                break;
            case WEST:
                loc = "textures/gui/west.png";
                break;
            case EAST:
                loc = "textures/gui/east.png";
                break;
            case NORTH:
                loc = "textures/gui/north.png";
                break;
            case SOUTH:
                loc = "textures/gui/south.png";
                break;
            default:
                continue;
            }
            resources[SwiftUtils.dirToIndex(dirs[i])] = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, loc);
        }
        return resources;
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

    protected GuiButton[] m_buttons;
    private DirectionButtonPressCallback m_directionButtonPressCallback;

    protected static final ResourceLocation DIRECTION_ICONS[] = getDirectionIcons();
}
