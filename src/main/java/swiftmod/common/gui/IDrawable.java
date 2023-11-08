package swiftmod.common.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IDrawable
{
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);
}
