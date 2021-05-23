package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;

@OnlyIn(Dist.CLIENT)
public class WhiteBlackListButton extends GuiTextureButton
{
    @FunctionalInterface
    public interface StateChangeHandler
    {
        public void onStateChanged(WhiteBlackListButton button, WhiteListState state);
    }

    public WhiteBlackListButton(GuiContainerScreen<?> screen, int x, int y, StateChangeHandler handler)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, handler);
    }

    public WhiteBlackListButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, 0, 0, null);
        m_state = WhiteListState.WhiteList;
        m_handler = handler;
        updateTexture();
        updateTooltip();
    }

    public WhiteListState getState()
    {
        return m_state;
    }

    public void setState(WhiteListState state)
    {
        setState(state, false);
    }

    public void setState(WhiteListState state, boolean invokeHandler)
    {
        m_state = state;
        updateTexture();
        updateTooltip();
        if (invokeHandler && m_handler != null)
            m_handler.onStateChanged(this, m_state);
    }
    
    public void setStateChangedHandler(StateChangeHandler handler)
    {
        m_handler = handler;
    }

    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        if (m_state == WhiteListState.WhiteList)
            m_state = WhiteListState.BlackList;
        else
            m_state = WhiteListState.WhiteList;

        updateTexture();
        updateTooltip();

        if (m_handler != null)
            m_handler.onStateChanged(this, m_state);

        return true;
    }

    private void updateTexture()
    {
        if (m_state == WhiteListState.WhiteList)
            setForegroundTexture(WHITELIST_TEXTURE);
        else
            setForegroundTexture(BLACKLIST_TEXTURE);
    }

    private void updateTooltip()
    {
        if (m_state == WhiteListState.WhiteList)
            setTooltip(new StringTextComponent("Whitelist"));
        else
            setTooltip(new StringTextComponent("Blacklist"));
    }

    public static final ResourceLocation WHITELIST_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/whitelist.png");
    public static final ResourceLocation BLACKLIST_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/blacklist.png");

    private WhiteListState m_state;
    private StateChangeHandler m_handler;
}
