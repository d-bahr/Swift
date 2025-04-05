package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class MatchDamageButton extends GuiBooleanStateButton
{
    public MatchDamageButton(GuiContainerScreen<?> screen, int x, int y, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, handler);
        setTooltip();
    }

    public MatchDamageButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, handler, MATCH_TEXTURE, IGNORE_TEXTURE);
        setTooltip();
    }

    private void setTooltip()
    {
        if (getState())
            setTooltip(Component.literal("Match item damage value"));
        else
            setTooltip(Component.literal("Ignore item damage value"));
    }

    protected void onStateChanged()
    {
        super.onStateChanged();
        setTooltip();
    }

    public static final ResourceLocation MATCH_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/match_damage.png");
    public static final ResourceLocation IGNORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/ignore_damage.png");
}
