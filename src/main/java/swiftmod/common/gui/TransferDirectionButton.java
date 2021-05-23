package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;

@OnlyIn(Dist.CLIENT)
public class TransferDirectionButton extends GuiTextureButton
{
    @FunctionalInterface
    public interface StateChangeHandler
    {
        public void onStateChanged(TransferDirectionButton button, TransferDirection state);
    }

    public TransferDirectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, 0, 0, null);
        m_state = TransferDirection.Extract;
        m_handler = handler;
        updateTexture();
        updateTooltip();
    }

    public TransferDirection getState()
    {
        return m_state;
    }

    public void setState(TransferDirection state)
    {
        setState(state, false);
    }

    public void setState(TransferDirection state, boolean invokeHandler)
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
        if (m_state == TransferDirection.Extract)
            m_state = TransferDirection.Insert;
        else
            m_state = TransferDirection.Extract;

        updateTexture();
        updateTooltip();

        if (m_handler != null)
            m_handler.onStateChanged(this, m_state);

        return true;
    }

    private void updateTexture()
    {
        if (m_state == TransferDirection.Extract)
            setForegroundTexture(EXTRACT_TEXTURE);
        else
            setForegroundTexture(INSERT_TEXTURE);
    }

    private void updateTooltip()
    {
        if (m_state == TransferDirection.Extract)
            setTooltip(new StringTextComponent("Extract"));
        else
            setTooltip(new StringTextComponent("Insert"));
    }

    public static final ResourceLocation INSERT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/insert.png");
    public static final ResourceLocation EXTRACT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/extract.png");

    private TransferDirection m_state;
    private StateChangeHandler m_handler;
}
