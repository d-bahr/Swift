package swiftmod.common.gui;

import swiftmod.common.MouseButton;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IClickable
{
    void onClick(GuiWidget widget, MouseButton mouseButton);
}
