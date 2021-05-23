package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class MatchOreDictionaryButton extends GuiBooleanStateButton
{
    public MatchOreDictionaryButton(GuiContainerScreen<?> screen, int x, int y, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, handler);
        setTooltip();
    }

    public MatchOreDictionaryButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, handler, MATCH_TEXTURE, IGNORE_TEXTURE);
        setTooltip();
    }

    private void setTooltip()
    {
        if (getState())
            setTooltip(new StringTextComponent("Match any ore dictionary tag"));
        else
            setTooltip(new StringTextComponent("Ignore ore dictionary tags"));
    }

    protected void onStateChanged()
    {
        super.onStateChanged();
        setTooltip();
    }

    public static final ResourceLocation MATCH_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/match_ore_dictionary.png");
    public static final ResourceLocation IGNORE_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/ignore_ore_dictionary.png");
}
