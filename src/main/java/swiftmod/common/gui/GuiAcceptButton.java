package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        setTooltip(Component.literal("Accept"));
    }

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "textures/gui/accept_button.png");
}
