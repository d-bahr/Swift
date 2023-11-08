package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLabel extends GuiTextWidget
{
    public GuiLabel(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height, text);
        m_requestFocusOnPress = false;
        m_verticalAlignment = GuiVerticalAlignment.Top;
        m_horizontalAlignment = GuiHorizontalAlignment.Left;
    }

    public GuiLabel(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height, text, font);
        m_requestFocusOnPress = false;
        m_verticalAlignment = GuiVerticalAlignment.Top;
        m_horizontalAlignment = GuiHorizontalAlignment.Left;
    }

    public void setVerticalAlignment(GuiVerticalAlignment alignment)
    {
        m_verticalAlignment = alignment;
    }

    public GuiVerticalAlignment getVerticalAlignment()
    {
        return m_verticalAlignment;
    }

    public void setHorizontalAlignment(GuiHorizontalAlignment alignment)
    {
        m_horizontalAlignment = alignment;
    }

    public GuiHorizontalAlignment getHorizontalAlignment()
    {
        return m_horizontalAlignment;
    }

    public void setAlignment(GuiVerticalAlignment verticalAlignment, GuiHorizontalAlignment horizontalAlignment)
    {
        setVerticalAlignment(verticalAlignment);
        setHorizontalAlignment(horizontalAlignment);
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);

        float drawX = 0;
        float drawY = 0;

        switch (m_verticalAlignment)
        {
        default:
        case Top:
            drawY = getY();
            break;
        case Middle:
            drawY = getY() + (height - SwiftGui.TEXT_HEIGHT * m_fontScale) / 2.0f;
            break;
        case Bottom:
            drawY = getY() + height - SwiftGui.TEXT_HEIGHT * m_fontScale;
            break;
        }

        Minecraft mc = Minecraft.getInstance();
        float stringWidth = mc.font.width(getText()) * m_fontScale;

        switch (m_horizontalAlignment)
        {
        default:
        case Left:
            drawX = getX();
            break;
        case Center:
            drawX = getX() + (width - stringWidth) / 2.0f;
            break;
        case Right:
            drawX = getX() + width - stringWidth;
            break;
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(drawX, drawY, 0.0);
        pose.scale(m_fontScale, m_fontScale, 1.0f);
        pose.translate(-drawX, -drawY, 0.0);
        drawText(graphics, drawX, drawY);
        pose.popPose();
    }

    protected GuiVerticalAlignment m_verticalAlignment;
    protected GuiHorizontalAlignment m_horizontalAlignment;
}
