package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class GuiBooleanStateButton extends GuiTextureButton
{
    @FunctionalInterface
    public interface StateChangeHandler
    {
        public void onStateChanged(GuiBooleanStateButton button, boolean state);
    }

    public GuiBooleanStateButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler,
            ResourceLocation trueTexture, ResourceLocation falseTexture)
    {
        super(screen, x, y, width, height, null);
        m_state = false;
        m_handler = handler;
        m_trueTexture = trueTexture;
        m_falseTexture = falseTexture;
        updateTexture();
    }

    public boolean getState()
    {
        return m_state;
    }

    public void setState(boolean state)
    {
        setState(state, false);
    }

    public void setState(boolean state, boolean invokeHandler)
    {
        m_state = state;
        updateTexture();
        onStateChanged();
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
        m_state = !m_state;

        updateTexture();
        onStateChanged();

        if (m_handler != null)
            m_handler.onStateChanged(this, m_state);

        return true;
    }

    protected void onStateChanged()
    {
    }

    private void updateTexture()
    {
        if (m_state)
            setForegroundTexture(m_trueTexture);
        else
            setForegroundTexture(m_falseTexture);
    }

    protected boolean m_state;
    protected StateChangeHandler m_handler;
    protected ResourceLocation m_trueTexture;
    protected ResourceLocation m_falseTexture;
}
