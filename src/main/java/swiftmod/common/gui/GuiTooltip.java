package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class GuiTooltip extends GuiMultiLineTextWidget
{
    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height)
    {
        this(screen, width, height, (ITextComponent)null);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, FontRenderer font)
    {
        this(screen, width, height, null, font);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height)
    {
        this(screen, x, y, width, height, (ITextComponent)null);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, FontRenderer font)
    {
        this(screen, x, y, width, height, null, font);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, ITextComponent text)
    {
        super(screen, width, height, text);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, width, height, text, font);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text)
    {
        super(screen, x, y, width, height, text);
    }

    public GuiTooltip(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text, FontRenderer font)
    {
        super(screen, x, y, width, height, text, font);
    }

    public void setTooltipForItem(ItemStack itemStack, boolean advancedUsageTooltip)
    {
        ITooltipFlag.TooltipFlags flag = ITooltipFlag.TooltipFlags.NORMAL;
        if (advancedUsageTooltip)
            flag = ITooltipFlag.TooltipFlags.ADVANCED;
        setTooltipForItem(itemStack, flag);
    }

    public void setTooltipForItem(ItemStack itemStack, ITooltipFlag tooltipFlags)
    {
        setText(itemStack.getTooltipLines(getPlayer(), tooltipFlags));
    }

    @Override
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (numLines() > 0 && containsMouse(mouseX, mouseY))
        {
            RenderSystem.disableDepthTest();

            // All the other tooltip code ends up calling this same function...
            Minecraft mc = Minecraft.getInstance();
            FontRenderer defaultFontRenderer = mc.font;
            //int drawX = mouseX - leftAbsolute();
            //int drawY = mouseY - topAbsolute();
            GuiUtils.drawHoveringText(new MatrixStack(), m_text, mouseX, mouseY, getScreen().windowWidth(), getScreen().windowHeight(), -1,
                    m_font != null ? m_font : defaultFontRenderer);

            RenderSystem.enableDepthTest();
        }
    }
}
