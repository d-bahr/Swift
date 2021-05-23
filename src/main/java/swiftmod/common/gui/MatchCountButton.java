package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class MatchCountButton extends GuiBooleanStateButton
{
    public MatchCountButton(GuiContainerScreen<?> screen, int x, int y, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, MATCH_TEXTURE, IGNORE_TEXTURE, handler);
    }

    public MatchCountButton(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            StateChangeHandler handler)
    {
        super(screen, x, y, width, height, handler, MATCH_TEXTURE, IGNORE_TEXTURE);
        initTooltip();
        setTooltip();
    }

    public MatchCountButton(GuiContainerScreen<?> screen, int x, int y, ResourceLocation matchTexture,
            ResourceLocation ignoreTexture, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, matchTexture, ignoreTexture, handler);
    }

    public MatchCountButton(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation matchTexture, ResourceLocation ignoreTexture, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, handler, matchTexture, ignoreTexture);
        initTooltip();
        setTooltip();
    }

    private void initTooltip()
    {
        m_matchTextComponent = new StringTextComponent("Match item count");
        m_ignoreTextComponent = new StringTextComponent("Ignore item count");
    }

    private void setTooltip()
    {
        if (getState())
            setTooltip(m_matchTextComponent);
        else
            setTooltip(m_ignoreTextComponent);
    }

    protected void onStateChanged()
    {
        super.onStateChanged();
        setTooltip();
    }

    public void setMatchTooltip(ITextComponent text)
    {
        m_matchTextComponent = text;
    }

    public void setIgnoreTooltip(ITextComponent text)
    {
        m_ignoreTextComponent = text;
    }

    private ITextComponent m_matchTextComponent;
    private ITextComponent m_ignoreTextComponent;

    public static final ResourceLocation MATCH_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/match_count.png");
    public static final ResourceLocation IGNORE_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/ignore_count.png");
}
