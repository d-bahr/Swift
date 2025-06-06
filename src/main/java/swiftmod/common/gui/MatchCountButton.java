package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        m_matchTextComponent = Component.literal("Match item count");
        m_ignoreTextComponent = Component.literal("Ignore item count");
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

    public void setMatchTooltip(Component text)
    {
        m_matchTextComponent = text;
    }

    public void setIgnoreTooltip(Component text)
    {
        m_ignoreTextComponent = text;
    }

    private Component m_matchTextComponent;
    private Component m_ignoreTextComponent;

    public static final ResourceLocation MATCH_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/match_count.png");
    public static final ResourceLocation IGNORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/ignore_count.png");
}
