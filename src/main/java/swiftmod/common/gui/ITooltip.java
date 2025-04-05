package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITooltip
{
    void onTooltip(GuiWidget widget, PoseStack matrixStack, int mouseX, int mouseY);
}
