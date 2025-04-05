package swiftmod.common.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTooltip extends GuiMultiLineTextWidget
{
    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, width, height, (Component)null);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, Font font)
    {
        this(screen, width, height, null, font);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, (Component)null);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, Font font)
    {
        this(screen, x, y, width, height, null, font);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, Component text)
    {
        super(screen, width, height, text);
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, Component text, Font font)
    {
        super(screen, width, height, text, font);
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height, text);
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height, text, font);
        m_fontColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        m_requestFocusOnPress = false;
    }

    public void setTooltipForItem(ItemStack itemStack)
    {
    	setText(getScreen().getTooltipForItem(itemStack));
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (numLines() > 0 && containsMouse(mouseX, mouseY))
        {
        	graphics.renderComponentTooltip(m_font, m_text, mouseX - leftAbsolute(), mouseY - topAbsolute());
        }
    }
}
