package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class MatchNBTButton extends GuiBooleanStateButton
{
    public MatchNBTButton(GuiContainerScreen<?> screen, int x, int y, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, handler);
        setTooltip();
    }

    public MatchNBTButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, handler, MATCH_TEXTURE, IGNORE_TEXTURE);
        setTooltip();
    }

    private void setTooltip()
    {
        if (getState())
            setTooltip(new TextComponent("Match NBT data"));
        else
            setTooltip(new TextComponent("Ignore NBT data"));
    }

    protected void onStateChanged()
    {
        super.onStateChanged();
        setTooltip();
    }

    public static final ResourceLocation MATCH_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/match_nbt.png");
    public static final ResourceLocation IGNORE_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/ignore_nbt.png");
}
