package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiSettingsButton extends GuiTextureButton
{
    public GuiSettingsButton(GuiContainerScreen<?> screen, int x, int y)
    {
        super(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, TEXTURE);
    }

    public GuiSettingsButton(GuiContainerScreen<?> screen, int x, int y, IClickable onClick)
    {
        super(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, TEXTURE);
    }

    public GuiSettingsButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, TEXTURE);
    }

    public GuiSettingsButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, IClickable onClick)
    {
        super(screen, x, y, width, height, TEXTURE, onClick);
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(Swift.MOD_NAME, "textures/gui/settings_button.png");
}
