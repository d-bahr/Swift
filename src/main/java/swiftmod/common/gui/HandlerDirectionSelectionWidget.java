package swiftmod.common.gui;

import java.awt.Point;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Swift;
import swiftmod.common.SwiftUtils;

@OnlyIn(Dist.CLIENT)
public class HandlerDirectionSelectionWidget extends GuiWidget
{
    @FunctionalInterface
    public interface DirectionButtonPressCallback
    {
        void onDirectionButtonPress(Direction direction);
    }

    public HandlerDirectionSelectionWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public HandlerDirectionSelectionWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        super(screen, x, y, 54, 54, Component.empty());
        m_blockFacingDirection = Direction.NORTH;
        m_buttons = new GuiButton[Direction.values().length];
        setItem(null, null);
        m_directionButtonPressCallback = null;
    }

    public void setDirectionButtonPressCallback(DirectionButtonPressCallback callback)
    {
        m_directionButtonPressCallback = callback;
    }
    
    public void setBlockFacingDirection(Direction dir)
    {
    	m_blockFacingDirection = dir;
    }
    
    public void setItem(ItemStack itemStack, BlockPos blockPos)
    {
		for (Direction dir : Direction.values())
		{
	        Point loc = getButtonLocation(dir);
	        int dirIndex = SwiftUtils.dirToIndex(dir);

	        if (m_buttons[dirIndex] != null)
	            removeChild(m_buttons[dirIndex]);

	        // It's technically faster to put this check outside the loop, but it's only evaluated 6 times
	        // and this is far less code to write, so whatever.
	        if (itemStack != null && !itemStack.isEmpty())
	        {
	            m_buttons[dirIndex] = new GuiFlatItemTextureButton(getScreen(), loc.x, loc.y,
	            		SwiftGui.BUTTON_WIDTH,
	                    SwiftGui.BUTTON_HEIGHT, 3, 3, blockPos,
	                    m_blockFacingDirection, dir, itemStack, (button, mouseButton) ->
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

	        m_buttons[dirIndex].setTooltip(Component.literal(getText(dir)));

	        if (m_buttons[dirIndex] != null)
	            addChild(m_buttons[dirIndex]);
		}
    }
    
    public void setDirectionEnabled(Direction dir, boolean enabled)
    {
        int dirIndex = SwiftUtils.dirToIndex(dir);
        if (m_buttons[dirIndex] != null)
        	m_buttons[dirIndex].active = enabled;
    }

    protected void onDirectionButtonPress(Direction direction)
    {
        if (m_directionButtonPressCallback != null)
            m_directionButtonPressCallback.onDirectionButtonPress(direction);
    }

    private static String getText(Direction direction)
    {
    	switch (direction)
    	{
    	case NORTH:
    		return "Front";
    	case SOUTH:
    		return "Back";
    	case WEST:
    		return "Right";
    	case EAST:
    		return "Left";
    	case UP:
    		return "Top";
    	case DOWN:
    		return "Bottom";
    	default:
    		return "";
    	}
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
            return new Point(36, 18);
        case EAST:
            return new Point(0, 18);
        case NORTH:
            return new Point(18, 18);
        case SOUTH:
            return new Point(0, 36);
        }
    }

    protected GuiButton[] m_buttons;
    private DirectionButtonPressCallback m_directionButtonPressCallback;
    private Direction m_blockFacingDirection;

    protected static final ResourceLocation DIRECTION_ICONS[] = getDirectionIcons();
}
