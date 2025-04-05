package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiDeleteButton extends GuiTextureButton
{
    public GuiDeleteButton(GuiContainerScreen<?> screen, int x, int y)
    {
        super(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, TEXTURE);
        initTooltip();
    }

    public GuiDeleteButton(GuiContainerScreen<?> screen, int x, int y, IClickable onClick)
    {
        super(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, TEXTURE, onClick);
        initTooltip();
    }

    public GuiDeleteButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height, TEXTURE);
        initTooltip();
    }

    public GuiDeleteButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, IClickable onClick)
    {
        super(screen, x, y, width, height, TEXTURE, onClick);
        initTooltip();
    }

    private void initTooltip()
    {
        setTooltip(Component.literal("Delete"));
    }

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "textures/gui/delete_button.png");
}
