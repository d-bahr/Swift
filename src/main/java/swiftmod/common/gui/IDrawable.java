package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IDrawable
{
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
}
