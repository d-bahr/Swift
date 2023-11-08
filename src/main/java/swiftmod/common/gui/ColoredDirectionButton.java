package swiftmod.common.gui;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;
import swiftmod.common.SwiftUtils;

@OnlyIn(Dist.CLIENT)
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
        m_textOverlay = new GuiLabel(screen, 0, 0, width + 1, height + 1, Component.empty());
        m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
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
            m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
        }
        else if (m_state == 17)
        {
            setForegroundTexture(null);
            m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
        }
        else
        {
            setForegroundTexture(null);

            // TODO: Clean this up; incorporate into the Color class.
            switch (m_state)
            {
            case 0:
            default:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
                break;
            case 1:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.BLACK));
                break;
            case 2:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
                break;
            case 3:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY));
                break;
            case 4:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE));
                break;
            case 5:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.RED));
                break;
            case 6:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED));
                break;
            case 7:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD));
                break;
            case 8:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW));
                break;
            case 9:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN));
                break;
            case 10:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN));
                break;
            case 11:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
                break;
            case 12:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA));
                break;
            case 13:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_BLUE));
                break;
            case 14:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE));
                break;
            case 15:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE));
                break;
            case 16:
                m_textOverlay.setFontColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD));
                break;
            }
        }
    }

    private static Component[] createDirectionLabels()
    {
        Direction[] dirs = Direction.values();
        Component[] strs = new Component[dirs.length];
        for (int i = 0; i < dirs.length; ++i)
        {
            strs[i] = Component.literal(dirs[i].getName().substring(0, 1).toUpperCase()).withStyle(ChatFormatting.BOLD);
        }
        return strs;
    }

    private static TextColor[] createRainbowColors()
    {
    	TextColor[] colors = new TextColor[30];
        colors[0] = TextColor.fromLegacyFormat(ChatFormatting.RED);
        colors[1] = TextColor.fromLegacyFormat(ChatFormatting.RED);
        colors[2] = TextColor.fromLegacyFormat(ChatFormatting.RED);
        colors[3] = TextColor.fromLegacyFormat(ChatFormatting.DARK_RED);
        colors[4] = TextColor.fromLegacyFormat(ChatFormatting.DARK_RED);
        colors[5] = TextColor.fromLegacyFormat(ChatFormatting.DARK_RED);
        colors[6] = TextColor.fromLegacyFormat(ChatFormatting.GOLD);
        colors[7] = TextColor.fromLegacyFormat(ChatFormatting.GOLD);
        colors[8] = TextColor.fromLegacyFormat(ChatFormatting.GOLD);
        colors[9] = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);
        colors[10] = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);
        colors[11] = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);
        colors[12] = TextColor.fromLegacyFormat(ChatFormatting.GREEN);
        colors[13] = TextColor.fromLegacyFormat(ChatFormatting.GREEN);
        colors[14] = TextColor.fromLegacyFormat(ChatFormatting.GREEN);
        colors[15] = TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN);
        colors[16] = TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN);
        colors[17] = TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN);
        colors[18] = TextColor.fromLegacyFormat(ChatFormatting.BLUE);
        colors[19] = TextColor.fromLegacyFormat(ChatFormatting.BLUE);
        colors[20] = TextColor.fromLegacyFormat(ChatFormatting.BLUE);
        colors[21] = TextColor.fromLegacyFormat(ChatFormatting.DARK_BLUE);
        colors[22] = TextColor.fromLegacyFormat(ChatFormatting.DARK_BLUE);
        colors[23] = TextColor.fromLegacyFormat(ChatFormatting.DARK_BLUE);
        colors[24] = TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE);
        colors[25] = TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE);
        colors[26] = TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE);
        colors[27] = TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
        colors[28] = TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
        colors[29] = TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
        return colors;
    }

    private static final Component[] DirectionLabels = createDirectionLabels();
    private static final TextColor[] RainbowColors = createRainbowColors();
    private byte m_state;
    private int m_rainbowTickCounter;
    private StateChangeHandler m_handler;
    private ResourceLocation m_disabledTexture;
    private GuiLabel m_textOverlay;
}
