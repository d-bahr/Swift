package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;
import swiftmod.common.SwiftUtils;

public class ColoredDirectionButton extends GuiTextureButton
{
    @FunctionalInterface
    public interface StateChangeHandler
    {
        public void onStateChanged(ColoredDirectionButton button, byte state);
    }

    public ColoredDirectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, null);
    }

    public ColoredDirectionButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, StateChangeHandler handler)
    {
        super(screen, x, y, width, height, null);
        m_state = 0;
        m_rainbowTickCounter = 0;
        m_handler = handler;
        m_disabledTexture = new ResourceLocation(Swift.MOD_NAME, "textures/gui/disabled_button.png");
        m_textOverlay = new GuiLabel(screen, 0, 0, width + 1, height + 1, StringTextComponent.EMPTY);
        m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GRAY));
        m_textOverlay.setAlignment(GuiVerticalAlignment.Middle, GuiHorizontalAlignment.Center);
        m_textOverlay.setFontScale(1.4f);
        addChild(m_textOverlay);
        setDirection(Direction.NORTH);
        updateTexture();
    }

    public void setDirection(Direction dir)
    {
        m_textOverlay.setText(DirectionLabels[SwiftUtils.dirToIndex(dir)]);
    }

    public byte getState()
    {
        return m_state;
    }

    public void setState(byte state)
    {
        setState(state, false);
    }

    public void setState(byte state, boolean invokeHandler)
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
        if (button == MouseButton.Left)
        {
            if (m_state >= 17)
                m_state = 0;
            else
                m_state++;
        }
        else
        {
            if (m_state == 0)
                m_state = 17;
            else
                m_state--;
        }

        updateTexture();
        onStateChanged();

        if (m_handler != null)
            m_handler.onStateChanged(this, m_state);

        return true;
    }
    
    @Override
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void tick()
    {
        super.tick();
        m_rainbowTickCounter++;
        if (m_rainbowTickCounter >= RainbowColors.length)
            m_rainbowTickCounter = 0;
        if (m_state == 17)
            m_textOverlay.setFontColor(RainbowColors[m_rainbowTickCounter]);
    }

    protected void onStateChanged()
    {
    }

    private void updateTexture()
    {
        if (m_state == 0)
        {
            setForegroundTexture(m_disabledTexture);
            m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GRAY));
        }
        else if (m_state == 17)
        {
            // TODO: Make a rainbow texture.
            setForegroundTexture(null);
            m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GRAY));
        }
        else
        {
            setForegroundTexture(null);
            
            // TODO: Clean this up; incorporate into the Color class.
            switch (m_state)
            {
            case 0:
            default:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GRAY));
                break;
            case 1:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.BLACK));
                break;
            case 2:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GRAY));
                break;
            case 3:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.GRAY));
                break;
            case 4:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.WHITE));
                break;
            case 5:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.RED));
                break;
            case 6:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_RED));
                break;
            case 7:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.GOLD));
                break;
            case 8:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.YELLOW));
                break;
            case 9:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.GREEN));
                break;
            case 10:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_GREEN));
                break;
            case 11:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.AQUA));
                break;
            case 12:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_AQUA));
                break;
            case 13:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_BLUE));
                break;
            case 14:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.LIGHT_PURPLE));
                break;
            case 15:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.DARK_PURPLE));
                break;
            case 16:
                m_textOverlay.setFontColor(Color.fromLegacyFormat(TextFormatting.GOLD));
                break;
            }
        }
    }

    private static ITextComponent[] createDirectionLabels()
    {
        Direction[] dirs = Direction.values();
        ITextComponent[] strs = new ITextComponent[dirs.length];
        for (int i = 0; i < dirs.length; ++i)
        {
            strs[i] = new StringTextComponent(dirs[i].getName().substring(0, 1).toUpperCase()).withStyle(TextFormatting.BOLD);
        }
        return strs;
    }

    private static Color[] createRainbowColors()
    {
        Color[] colors = new Color[30];
        colors[0] = Color.fromLegacyFormat(TextFormatting.RED);
        colors[1] = Color.fromLegacyFormat(TextFormatting.RED);
        colors[2] = Color.fromLegacyFormat(TextFormatting.RED);
        colors[3] = Color.fromLegacyFormat(TextFormatting.DARK_RED);
        colors[4] = Color.fromLegacyFormat(TextFormatting.DARK_RED);
        colors[5] = Color.fromLegacyFormat(TextFormatting.DARK_RED);
        colors[6] = Color.fromLegacyFormat(TextFormatting.GOLD);
        colors[7] = Color.fromLegacyFormat(TextFormatting.GOLD);
        colors[8] = Color.fromLegacyFormat(TextFormatting.GOLD);
        colors[9] = Color.fromLegacyFormat(TextFormatting.YELLOW);
        colors[10] = Color.fromLegacyFormat(TextFormatting.YELLOW);
        colors[11] = Color.fromLegacyFormat(TextFormatting.YELLOW);
        colors[12] = Color.fromLegacyFormat(TextFormatting.GREEN);
        colors[13] = Color.fromLegacyFormat(TextFormatting.GREEN);
        colors[14] = Color.fromLegacyFormat(TextFormatting.GREEN);
        colors[15] = Color.fromLegacyFormat(TextFormatting.DARK_GREEN);
        colors[16] = Color.fromLegacyFormat(TextFormatting.DARK_GREEN);
        colors[17] = Color.fromLegacyFormat(TextFormatting.DARK_GREEN);
        colors[18] = Color.fromLegacyFormat(TextFormatting.BLUE);
        colors[19] = Color.fromLegacyFormat(TextFormatting.BLUE);
        colors[20] = Color.fromLegacyFormat(TextFormatting.BLUE);
        colors[21] = Color.fromLegacyFormat(TextFormatting.DARK_BLUE);
        colors[22] = Color.fromLegacyFormat(TextFormatting.DARK_BLUE);
        colors[23] = Color.fromLegacyFormat(TextFormatting.DARK_BLUE);
        colors[24] = Color.fromLegacyFormat(TextFormatting.DARK_PURPLE);
        colors[25] = Color.fromLegacyFormat(TextFormatting.DARK_PURPLE);
        colors[26] = Color.fromLegacyFormat(TextFormatting.DARK_PURPLE);
        colors[27] = Color.fromLegacyFormat(TextFormatting.LIGHT_PURPLE);
        colors[28] = Color.fromLegacyFormat(TextFormatting.LIGHT_PURPLE);
        colors[29] = Color.fromLegacyFormat(TextFormatting.LIGHT_PURPLE);
        return colors;
    }

    private static final ITextComponent[] DirectionLabels = createDirectionLabels();
    private static final Color[] RainbowColors = createRainbowColors();
    private byte m_state;
    private int m_rainbowTickCounter;
    private StateChangeHandler m_handler;
    private ResourceLocation m_disabledTexture;
    private GuiLabel m_textOverlay;
}
