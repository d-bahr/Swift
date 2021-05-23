package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiAcceptButton extends GuiTextureButton
{
    public GuiAcceptButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, TEXTURE);
        initTooltip();
    }

    public GuiAcceptButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, IClickable onClick)
    {
        super(screen, x, y, width, height, TEXTURE, onClick);
        initTooltip();
    }

    private void initTooltip()
    {
        setTooltip(new StringTextComponent("Accept"));
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(Swift.MOD_NAME, "textures/gui/accept_button.png");
}
