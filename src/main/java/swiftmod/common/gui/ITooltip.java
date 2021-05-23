package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITooltip
{
    void onTooltip(GuiWidget widget, MatrixStack matrixStack, int mouseX, int mouseY);
}
