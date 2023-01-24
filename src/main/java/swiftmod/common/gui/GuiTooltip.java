package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, Component text, Font font)
    {
        super(screen, width, height, text, font);
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text)
    {
        super(screen, x, y, width, height, text);
        m_requestFocusOnPress = false;
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, Component text, Font font)
    {
        super(screen, x, y, width, height, text, font);
        m_requestFocusOnPress = false;
    }

    public void setTooltipForItem(ItemStack itemStack, boolean advancedUsageTooltip)
    {
        TooltipFlag flag = TooltipFlag.Default.NORMAL;
        if (advancedUsageTooltip)
            flag = TooltipFlag.Default.ADVANCED;
        setTooltipForItem(itemStack, flag);
    }

    public void setTooltipForItem(ItemStack itemStack, TooltipFlag tooltipFlags)
    {
        setText(itemStack.getTooltipLines(getPlayer(), tooltipFlags));
    }

    @Override
    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (numLines() > 0 && containsMouse(mouseX, mouseY))
        {
            getScreen().renderComponentTooltip(new PoseStack(), m_text, mouseX, mouseY);
            
            /*
            RenderSystem.disableDepthTest();

            // All the other tooltip code ends up calling this same function...
            Minecraft mc = Minecraft.getInstance();
            Font defaultFontRenderer = mc.font;
            //int drawX = mouseX - leftAbsolute();
            //int drawY = mouseY - topAbsolute();
            GuiUtils.drawHoveringText(new PoseStack(), m_text, mouseX, mouseY, getScreen().windowWidth(), getScreen().windowHeight(), -1,
                    m_font != null ? m_font : defaultFontRenderer);

            RenderSystem.enableDepthTest();
            */
        }
    }
}
