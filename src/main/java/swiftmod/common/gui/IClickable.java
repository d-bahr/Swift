package swiftmod.common.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public interface IClickable
{
    void onClick(GuiWidget widget, MouseButton mouseButton);
}
