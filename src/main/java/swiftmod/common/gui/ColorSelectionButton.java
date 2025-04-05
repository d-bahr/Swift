package swiftmod.common.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Color;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class ColorSelectionButton extends GuiButton
{
    @FunctionalInterface
    public interface ColorChangeHandler
    {
        public void onColorChanged(ColorSelectionButton button, Color color);
    }
    
    public ColorSelectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, Color.Transparent);
    }

    public ColorSelectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Color color)
    {
    	this(screen, x, y, width, height, color, null);
    }

    public ColorSelectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, ColorChangeHandler handler)
    {
        this(screen, x, y, width, height, Color.Transparent, null);
    }

    public ColorSelectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, Color color, ColorChangeHandler handler)
    {
        super(screen, x, y, width, height);
        setColor(color);
        setMargins(2);
        setColorChangeHandler(handler);
    }
    
    public void setMargins(int margin)
    {
    	setMargins(margin, margin, margin, margin);
    }

    public void setMargins(int horizontal, int vertical)
    {
    	setMargins(horizontal, horizontal, vertical, vertical);
    }

    public void setMargins(int left, int right, int top, int bottom)
    {
    	m_leftMargin = left;
    	m_rightMargin = right;
    	m_topMargin = top;
    	m_bottomMargin = bottom;
    }
    
    public void setColorChangeHandler(ColorChangeHandler handler)
    {
    	m_handler = handler;
    }
    
    public void setColor(Color c)
    {
    	m_color = c;
        updateTooltip();
    }
    
    public Color getColor()
    {
    	return m_color;
    }

    protected void onStateChanged()
    {
    	// Note: This function can be overriden in a derived class for custom behavior.
    }
    
    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        if (button == MouseButton.Left)
            m_color = m_color.next();
        else
            m_color = m_color.previous();

        updateTooltip();
        onStateChanged();

        if (m_handler != null)
            m_handler.onColorChanged(this, m_color);

        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);

    	// Cache to avoid costly recalculation of left/top.
    	int left = left();
    	int right = left + width;
    	int top = top();
    	int bottom = top + height;
    	
    	left += m_leftMargin;
    	right -= m_rightMargin;
    	top += m_topMargin;
    	bottom -= m_bottomMargin;
    	
        if (m_color == Color.Transparent)
        {
        	int hCenter = (left + right) / 2;
        	int vCenter = (top + bottom) / 2;
        	
        	// Top-left corner
        	graphics.fill(left, top, hCenter, vCenter, Color.White.argb());
        	
        	// Top-right corner
        	graphics.fill(hCenter, top, right, vCenter, Color.Green.argb());
        	
        	// Bottom-left corner
        	graphics.fill(left, vCenter, hCenter, bottom, Color.Blue.argb());
        	
        	// Bottom-right corner
        	graphics.fill(hCenter, vCenter, right, bottom, Color.Red.argb());
        }
        else
        {
        	graphics.fill(left, top, right, bottom, m_color.argb());
        }
    }
    
    private void updateTooltip()
    {
        String tooltip = "Color: " + (m_color == Color.Transparent ? "Any" : m_color.getName());
        setTooltip(Component.literal(tooltip));
    }
    
    private int m_leftMargin;
    private int m_rightMargin;
    private int m_topMargin;
    private int m_bottomMargin;
    private Color m_color;
    private ColorChangeHandler m_handler;
}
