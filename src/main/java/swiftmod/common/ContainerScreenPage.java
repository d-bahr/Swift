package swiftmod.common;

import java.util.ArrayList;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;

// TODO: Deprecated; remove
public class ContainerScreenPage
{
    public ContainerScreenPage()
    {
        widgets = new ArrayList<Widget>();
        backgroundTexture = null;
    }

    public void add(Widget widget)
    {
        widgets.add(widget);
    }

    public void setBackgroundTexture(ResourceLocation texture)
    {
        backgroundTexture = texture;
    }
    
    public ResourceLocation getBackgroundTexture()
    {
        return backgroundTexture;
    }

    public void clear()
    {
        hide();
        widgets.clear();
        backgroundTexture = null;
    }

    public void show()
    {
        for (Widget w : widgets)
        {
            w.active = true;
            w.visible = true;
        }
    }
    
    public void hide()
    {
        for (Widget w : widgets)
        {
            w.active = false;
            w.visible = false;
        }
    }

    public ArrayList<Widget> widgets;
    public ResourceLocation backgroundTexture;
}
