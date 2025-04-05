package swiftmod.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class RedstoneButton extends GuiTextureButton
{
    @FunctionalInterface
    public interface StateChangeHandler
    {
        public void onStateChanged(RedstoneButton button, RedstoneControl state);
    }

    public RedstoneButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, null);
    }

    public RedstoneButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, 0, 0, null);
        m_state = RedstoneControl.Disabled;
        m_handler = handler;
        updateTexture();
        updateTooltip();
    }

    public RedstoneControl getState()
    {
        return m_state;
    }

    public void setState(RedstoneControl state)
    {
        setState(state, false);
    }

    public void setState(RedstoneControl state, boolean invokeHandler)
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
        if (button == MouseButton.Left)
        {
            switch (m_state)
            {
            case Disabled:
                m_state = RedstoneControl.Ignore;
                break;
            case Ignore:
                m_state = RedstoneControl.Normal;
                break;
            case Normal:
                m_state = RedstoneControl.Inverted;
                break;
            case Inverted:
                m_state = RedstoneControl.Disabled;
                break;
            default:
                m_state = RedstoneControl.Disabled;
                break;
            }
        }
        else
        {
            switch (m_state)
            {
            case Disabled:
                m_state = RedstoneControl.Inverted;
                break;
            case Ignore:
                m_state = RedstoneControl.Disabled;
                break;
            case Normal:
                m_state = RedstoneControl.Ignore;
                break;
            case Inverted:
                m_state = RedstoneControl.Normal;
                break;
            default:
                m_state = RedstoneControl.Disabled;
                break;
            }
        }

        updateTexture();
        updateTooltip();

        if (m_handler != null)
            m_handler.onStateChanged(this, m_state);

        return true;
    }

    private void updateTexture()
    {
        switch (m_state)
        {
        case Disabled:
            setForegroundTexture(REDSTONE_DISABLED_TEXTURE);
            break;
        case Ignore:
            setForegroundTexture(REDSTONE_IGNORED_TEXTURE);
            break;
        case Normal:
            setForegroundTexture(REDSTONE_NORMAL_TEXTURE);
            break;
        case Inverted:
            setForegroundTexture(REDSTONE_INVERTED_TEXTURE);
            break;
        default:
            setForegroundTexture(REDSTONE_DISABLED_TEXTURE);
            break;
        }
    }
    
    private void updateTooltip()
    {
        switch (m_state)
        {
        case Disabled:
            setTooltip(Component.literal("Inactive"));
            break;
        case Ignore:
            setTooltip(Component.literal("Always active"));
            break;
        case Normal:
            setTooltip(Component.literal("Active with redstone"));
            break;
        case Inverted:
            setTooltip(Component.literal("Active without redstone"));
            break;
        default:
            setTooltip(Component.literal("Inactive"));
            break;
        }
    }

    public static final ResourceLocation REDSTONE_DISABLED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/redstone_disabled.png");
    public static final ResourceLocation REDSTONE_IGNORED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/redstone_ignored.png");
    public static final ResourceLocation REDSTONE_NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/redstone_normal.png");
    public static final ResourceLocation REDSTONE_INVERTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/redstone_inverted.png");

    private RedstoneControl m_state;
    private StateChangeHandler m_handler;
}
