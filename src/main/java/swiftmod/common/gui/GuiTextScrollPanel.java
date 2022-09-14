package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiTextScrollPanel extends GuiMultiLineTextWidget
{
    @FunctionalInterface
    public interface ItemSelectedHandler
    {
        void select(int index, Component name);
    }

    public GuiTextScrollPanel(GuiContainerScreen<?> screen, int x, int y, int width, int numRows)
    {
        this(screen, x, y, width, numRows, DEFAULT_ROW_HEIGHT);
    }

    public GuiTextScrollPanel(GuiContainerScreen<?> screen, int x, int y, int width, int numRows, int rowHeight)
    {
        super(screen, x, y, width, numRows * rowHeight + 2);
        int barXShift = width - 6;
        int barYShift = 2;
        barX = x + barXShift;
        barY = y + barYShift;
        barWidth = 4;
        barHeight = 4;
        maxBarHeight = height - 4;
        elementHeight = rowHeight;
        m_itemSelectHandler = null;
        m_playClickOnPress = false;
    }

    public void setItemSelectedHandler(ItemSelectedHandler handler)
    {
        m_itemSelectHandler = handler;
    }

    /*
     * Check every tick to see if dragging stops on parent; we might not get the event
     * if the mouse moves off the component while dragging.
     */
    @Override
    public void tick()
    {
        super.tick();
        if (!getScreen().isDragging())
            isDragging = false;
    }

    @Override
    protected boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        int leftAbs = leftAbsolute();
        int topAbs = topAbsolute();
        int barXAbs = leftAbs - x + barX;
        int barYAbs = topAbs - y + barY;
        if (mouseX >= barXAbs && mouseX <= barXAbs + barWidth && mouseY >= barYAbs && mouseY <= barYAbs + maxBarHeight)
        {
            if (needsScrollBars())
            {
                double diff = mouseY - barYAbs;
                scroll = Math.min(Math.max(diff / getMax(), 0), 1);
                // Mark that we are dragging so that we can continue to "drag" even if our mouse goes off of being
                // over the element
                isDragging = true;
            }
            else
            {
                scroll = 0;
            }

            playDownSound(Minecraft.getInstance().getSoundManager());
        }

        if (mouseX >= leftAbs + 1 && mouseX < barXAbs - 1 && mouseY >= topAbs + 1 && mouseY < topAbs + height - 1)
        {
            int index = getIndexAtStartOfScroll();
            clearSelection(true);
            for (int i = 0; i < getFocusedElements(); i++)
            {
                if (index + i < numLines())
                {
                    int shiftedY = topAbs + 1 + elementHeight * i;
                    if (mouseY >= shiftedY && mouseY <= shiftedY + elementHeight)
                    {
                        setSelected(index + i, true);
                        playDownSound(Minecraft.getInstance().getSoundManager());
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected boolean onMouseRelease(MouseButton button, double mouseX, double mouseY)
    {
        isDragging = false;
        return true;
    }

    @Override
    protected boolean onMouseDrag(MouseButton button, double mouseX, double mouseY, double dragX, double dragY)
    {
        if (needsScrollBars() && isDragging)
        {
            int topAbs = topAbsolute();
            int barYAbs = topAbs - y + barY;
            scroll = Math.min(Math.max((mouseY - barYAbs) / getMax(), 0), 1);
        }
        return true;
    }

    protected boolean needsScrollBars()
    {
        return numLines() > getFocusedElements();
    }

    private int getMax()
    {
        return maxBarHeight - barHeight;
    }

    protected int getScroll()
    {
        // Calculate thumb position along scrollbar
        int max = getMax();
        return Math.max(Math.min((int) (scroll * max), max), 0);
    }

    public int getIndexAtStartOfScroll()
    {
        if (needsScrollBars())
        {
            int size = numLines() - getFocusedElements();
            return (int) ((size + 0.5) * scroll);
        }
        return 0;
    }

    public boolean adjustScroll(double delta)
    {
        if (delta != 0 && needsScrollBars())
        {
            int elements = numLines() - getFocusedElements();
            if (elements > 0)
            {
                if (delta > 0)
                {
                    delta = 1;
                }
                else
                {
                    delta = -1;
                }
                scroll = (float) (scroll - delta / elements);
                if (scroll < 0.0F)
                {
                    scroll = 0.0F;
                }
                else if (scroll > 1.0F)
                {
                    scroll = 1.0F;
                }
                return true;
            }
        }
        return false;
    }

    protected int getFocusedElements()
    {
        return (height - 2) / elementHeight;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double delta)
    {
        return isMouseOver(mouseX, mouseY) && adjustScroll(delta);
    }

    public boolean hasSelection()
    {
        return selected != -1;
    }

    public void setSelected(int index)
    {
        setSelected(index, true);
    }

    protected void setSelected(int index, boolean raiseEvent)
    {
        if (index >= 0 && index < m_text.size())
        {
            selected = index;
            if (raiseEvent && m_itemSelectHandler != null)
                m_itemSelectHandler.select(index, m_text.get(index));
        }
        else
        {
            clearSelection(raiseEvent);
        }
    }

    public int getSelection()
    {
        return selected;
    }

    public void clearSelection()
    {
        clearSelection(true);
    }

    public void clearSelection(boolean raiseEvent)
    {
        this.selected = -1;
        if (raiseEvent && m_itemSelectHandler != null)
            m_itemSelectHandler.select(-1, null);
    }

    protected void onTextChanged()
    {
        setSelected(selected, true);

        if (!needsScrollBars())
            scroll = 0;
    }

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        // Draw the background
        RenderSystem.setShaderTexture(0, BACKGROUND);

        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, 0.0f, 0.0f, width, height, width, height);

        RenderSystem.setShaderTexture(0, SCROLL_LIST);
        // Draw Scroll
        // Top border
        blit(matrixStack, barX - 1, barY - 1, 0, 0, SCROLL_BAR_WIDTH, 1, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
        // Middle border
        blit(matrixStack, barX - 1, barY, 6, maxBarHeight, 0, 1, SCROLL_BAR_WIDTH, 1, SCROLL_BAR_WIDTH,
                SCROLL_BAR_HEIGHT);
        // Bottom border
        blit(matrixStack, barX - 1, y + maxBarHeight + 2, 0, 0, SCROLL_BAR_WIDTH, 1, SCROLL_BAR_WIDTH,
                SCROLL_BAR_HEIGHT);
        // Scroll bar
        blit(matrixStack, barX, barY + getScroll(), 0, 2, barWidth, barHeight, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
        // Draw the elements
        drawElements(matrixStack, mouseX, mouseY, partialTicks);

        // Draw the foreground
        if (!m_text.isEmpty())
        {
            // Render the text into the entries
            int scrollIndex = getIndexAtStartOfScroll();
            int focusedElements = getFocusedElements();
            int numLines = numLines();
            for (int i = 0; i < focusedElements; i++)
            {
                int index = scrollIndex + i;
                if (index < numLines)
                {
                    drawScaledTextScaledBound(matrixStack, m_text.get(index), x + 2, y + 2 + elementHeight * i,
                            0x00FFFFFF, barX - x - 2, 0.8F);
                }
            }
        }
    }

    public void drawElements(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        // Draw Selected
        int scrollIndex = getIndexAtStartOfScroll();
        if (selected != -1 && selected >= scrollIndex && selected <= scrollIndex + getFocusedElements() - 1)
        {
            blit(matrix, x + 1, y + 1 + (selected - scrollIndex) * elementHeight, barX - x - 2, elementHeight, 4, 2, 2,
                    2, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
        }
    }

    public void drawScaledTextScaledBound(PoseStack matrix, Component text, float x, float y, int color,
            float maxX, float textScale)
    {
        float width = getStringWidth(text) * textScale;
        float scale = Math.min(1, maxX / width) * textScale;
        drawTextWithScale(matrix, text, x, y, color, scale);
    }

    private void drawTextWithScale(PoseStack matrix, Component text, float x, float y, int color, float scale)
    {
        float yAdd = 4 - (scale * 8) / 2F;
        matrix.pushPose();
        matrix.translate(x, y + yAdd, 0);
        matrix.scale(scale, scale, scale);
        drawString(matrix, text, 0, 0, color);
        matrix.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private int getStringWidth(Component component)
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.font.width(component);
    }

    private int drawString(PoseStack matrix, Component component, int x, int y, int color)
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.font.draw(matrix, component, x, y, color);
    }

    protected double scroll;
    private boolean isDragging;
    protected final int barX;
    protected final int barY;
    protected final int barWidth;
    protected final int barHeight;
    protected final int maxBarHeight;
    protected final int elementHeight;

    private int selected = -1;
    private ItemSelectedHandler m_itemSelectHandler;

    protected static final ResourceLocation BACKGROUND = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/inner_screen.png");
    protected static final ResourceLocation SCROLL_LIST = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/scroll_list.png");
    protected static final int SCROLL_BAR_WIDTH = 6;
    protected static final int SCROLL_BAR_HEIGHT = 6;

    public static final int DEFAULT_ROW_HEIGHT = 10;
}
