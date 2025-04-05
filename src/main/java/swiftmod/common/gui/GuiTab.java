package swiftmod.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;

/**
 * Selectable tab adjacent to the main GUI.
 */
@OnlyIn(Dist.CLIENT)
public class GuiTab extends GuiBooleanStateButton
{
    public GuiTab(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, null);
    }

    public GuiTab(GuiContainerScreen<?> screen, ResourceLocation foregroundTexture)
    {
        this(screen, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, foregroundTexture);
    }
    
    public GuiTab(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, 0, 0, width, height, null);
    }

    public GuiTab(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, null);
    }

    public GuiTab(GuiContainerScreen<?> screen, int width, int height, ResourceLocation foregroundTexture)
    {
        this(screen, 0, 0, width, height, foregroundTexture);
    }

    public GuiTab(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation foregroundTexture)
    {
        super(screen, x, y, width, height, null, TAB_SELECTED_TEXTURE, TAB_TEXTURE);
        // Due to the ordering of callbacks we have to play the button click sound manually,
        // so set this to false.
        m_playClickOnPress = false;
        if (foregroundTexture != null)
        {
            m_foregroundTexture = new GuiTexture(screen, TAB_ICON_WIDTH, TAB_ICON_HEIGHT, foregroundTexture);
        	// Center the foreground texture in the tab.
        	m_foregroundTexture.setX((width - RIGHT_MARGIN - m_foregroundTexture.width()) / 2);
        	m_foregroundTexture.setY((height - m_foregroundTexture.height()) / 2);
        	addChild(m_foregroundTexture);
        }
        else
        {
        	m_foregroundTexture = null;
        }
    }

    public void setSelected(boolean selected)
    {
    	if (selected != m_state)
    		setState(selected);
    }

    public void setSelected(boolean selected, boolean invokeHandler)
    {
    	if (selected != m_state)
    		setState(selected, invokeHandler);
    }
    
    public boolean getSelected()
    {
    	return getState();
    }

    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
    	// Can only be selected (to true) on mouse press.
    	// If tab is already selected, do nothing.
    	if (m_state == false)
    	{
    		boolean handled = super.onMousePress(button, mouseX, mouseY);
    		if (handled)
                playDownSound(Minecraft.getInstance().getSoundManager());
    		return handled;
    	}
    	else
    		return true;
    }
    
    protected GuiTexture m_foregroundTexture;
    
    private static final int RIGHT_MARGIN = 3;
    
    private static final int DEFAULT_WIDTH = 27;
    private static final int DEFAULT_HEIGHT = 30;
    
    private static final int TAB_ICON_WIDTH = 16;
    private static final int TAB_ICON_HEIGHT = 24;
    
    public static final ResourceLocation TAB_ITEM_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/tab_item.png");
    
    public static final ResourceLocation TAB_FLUID_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/tab_fluid.png");
    
    public static final ResourceLocation TAB_ENERGY_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/tab_energy.png");

    protected static final ResourceLocation TAB_SELECTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/tab_right_selected.png");
    protected static final ResourceLocation TAB_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/tab_right.png");
}
