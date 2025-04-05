package swiftmod.common.gui;

import java.util.function.BiPredicate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class GhostItemSlot extends GuiItemTextureButton
{
    @FunctionalInterface
    public interface FilterUpdatedCallback
    {
        void invoke(int slot, ItemStack itemStack, int quantity);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, 0, 0, ItemStack.EMPTY);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y, ItemStack itemStack)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, 0, 0, itemStack);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y, int xTexMargin, int yTexMargin)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, xTexMargin, yTexMargin, ItemStack.EMPTY);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y, int xTexMargin, int yTexMargin, ItemStack itemStack)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, xTexMargin, yTexMargin, itemStack);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin, int yTexMargin)
    {
        this(screen, x, y, width, height, xTexMargin, yTexMargin, ItemStack.EMPTY);
    }

    public GhostItemSlot(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin, int yTexMargin,
            ItemStack itemStack)
    {
        super(screen, x, y, width, height, xTexMargin, yTexMargin, itemStack);
        m_slot = 0;
        m_showQuantity = true;
        m_quantity = 0;
        m_maxQuantity = 64;
        m_drawBackground = false;
        setIsFilterValidCallback(null);

        m_playClickOnPress = false;
    }

    public void setIsFilterValidCallback(BiPredicate<Integer, ItemStack> callback)
    {
        if (callback == null)
            m_filterValidCallback = (slot, stack) -> true;
        else
            m_filterValidCallback = callback;
    }

    public void setFilterUpdatedCallback(FilterUpdatedCallback callback)
    {
        m_filterUpdatedCallback = callback;
    }

    public void setSlot(int slot)
    {
        m_slot = slot;
    }

    public int getSlot()
    {
        return m_slot;
    }

    public void setItemStack(ItemStack itemStack)
    {
        if (itemStack == ItemStack.EMPTY || m_filterValidCallback.test(m_slot, itemStack))
        {
            super.setItemStack(itemStack);
            setQuantity(itemStack.getCount());
        }
    }

    public void setQuantity(int quantity)
    {
        m_quantity = Math.min(quantity, m_showQuantity ? m_maxQuantity : 1);
    }

    public int getQuantity()
    {
        return m_quantity;
    }

    public void setMaxQuantity(int quantity)
    {
        m_maxQuantity = quantity;
    }

    public int getMaxQuantity()
    {
        return m_maxQuantity;
    }

    public void incrQuantity(int amount)
    {
        m_quantity = Math.min(m_quantity + amount, m_showQuantity ? m_maxQuantity : 1);
    }

    public void decrQuantity(int amount)
    {
        m_quantity -= amount;
        if (m_quantity <= 0)
        {
            m_itemStack = ItemStack.EMPTY;
            m_quantity = 0;
        }
    }

    public void clearItemStack()
    {
        m_itemStack = ItemStack.EMPTY;
        m_quantity = 0;
    }

    public void showQuantity(boolean show)
    {
        m_showQuantity = show;
    }

    @Override
    public void playDownSound(SoundManager manager)
    {
        // Don't call super -- suppress all normal button sounds.
    }

    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        ItemStack itemInCursor = this.getScreen().getMenu().getCarried();
        boolean updated = false;
        boolean shift = Screen.hasShiftDown();
        boolean control = Screen.hasControlDown();

        // Left button = add
        // Right button = subtract
        if (button == MouseButton.Left)
        {
            if (shift)
            {
                // Add a stack.
                if (!m_itemStack.isEmpty() && !itemInCursor.isEmpty())
                {
                    if (itemInCursor.getItem() == m_itemStack.getItem())
                    {
                        incrQuantity(m_itemStack.getMaxStackSize());
                        updated = true;
                    }
                    else
                    {
                        setItemStack(itemInCursor);
                        setQuantity(itemInCursor.getMaxStackSize());
                        updated = true;
                    }
                }
                else if (!m_itemStack.isEmpty())
                {
                    incrQuantity(m_itemStack.getMaxStackSize());
                    updated = true;
                }
                else if (!itemInCursor.isEmpty())
                {
                    setItemStack(itemInCursor);
                    setQuantity(itemInCursor.getMaxStackSize());
                    updated = true;
                }
            }
            else if (control)
            {
                // Add a single item.
                if (!m_itemStack.isEmpty() && !itemInCursor.isEmpty())
                {
                    if (itemInCursor.getItem() == m_itemStack.getItem())
                    {
                        incrQuantity(1);
                        updated = true;
                    }
                    else
                    {
                        setItemStack(itemInCursor);
                        setQuantity(1);
                        updated = true;
                    }
                }
                else if (!m_itemStack.isEmpty())
                {
                    incrQuantity(1);
                    updated = true;
                }
                else if (!itemInCursor.isEmpty())
                {
                    setItemStack(itemInCursor);
                    setQuantity(1);
                    updated = true;
                }
            }
            else
            {
                // Add a number of items equal to the current stack size.
                if (!m_itemStack.isEmpty() && !itemInCursor.isEmpty())
                {
                    if (itemInCursor.getItem() == m_itemStack.getItem())
                    {
                        incrQuantity(itemInCursor.getCount());
                        updated = true;
                    }
                    else
                    {
                        setItemStack(itemInCursor);
                        updated = true;
                    }
                }
                else if (!m_itemStack.isEmpty())
                {
                    incrQuantity(m_itemStack.getMaxStackSize() / 2);
                    updated = true;
                }
                else if (!itemInCursor.isEmpty())
                {
                    setItemStack(itemInCursor);
                    updated = true;
                }
            }
        }
        else
        {
            if (shift)
            {
                // Remove a stack.
                if (!m_itemStack.isEmpty())
                {
                    decrQuantity(m_itemStack.getMaxStackSize());
                    updated = true;
                }
            }
            else if (control)
            {
                // Remove a single item.
                decrQuantity(1);
                updated = true;
            }
            else
            {
                // Remove a number of items equal to the current stack size.
                if (!itemInCursor.isEmpty())
                {
                    decrQuantity(itemInCursor.getCount());
                    updated = true;
                }
                else if (!m_itemStack.isEmpty())
                {
                    decrQuantity(m_itemStack.getMaxStackSize() / 2);
                    updated = true;
                }
            }
        }

        if (updated && m_filterUpdatedCallback != null)
            m_filterUpdatedCallback.invoke(m_slot, m_itemStack, m_quantity);

        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);

        if (!m_itemStack.isEmpty() && m_quantity > 0)
        {
            int left = leftAbsolute();
            int top = topAbsolute();

            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

        	//renderer.renderAndDecorateFakeItem(m_itemStack, left, top);

            /* Note: this would be a valid way to render items:
        	 * renderer.renderGuiItemDecorations(font, m_itemStack, left, top);
        	 * except that it will always render the quantity, and we don't want that behavior
        	 * unless quantity comparison is explicitly requested. Also, with our custom method,
        	 * we can render high quantities without it bleeding over into adjacent slots by
        	 * making the text smaller.
             */
            if (m_showQuantity && m_quantity > 1)
            {
                PoseStack matrix = new PoseStack();
                String s = String.valueOf(m_quantity);
                float r = 1.0f;
                int maxLengthForShrinking = 4;
                if (s.length() > 2)
                {
                    r = 2.0f / Math.min(maxLengthForShrinking, s.length());
                    matrix.scale(r, r, 1.0f);
                }
                matrix.translate(0.0, 0.0, 200.0);
                
                MultiBufferSource.BufferSource b = graphics.bufferSource();
                font.drawInBatch(s, (float) (left + 17) / r - font.width(s), (float) (top + 16) / r - 7, 0x00FFFFFF,
                        true, matrix.last().pose(), b, Font.DisplayMode.NORMAL, 0, 15728880);
                b.endBatch();
            }

            if (m_itemStack.isBarVisible())
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                Tesselator tessellator = Tesselator.getInstance();
                int i = m_itemStack.getBarWidth();
                int j = m_itemStack.getBarColor();
                fillRect(tessellator, left + 2, top + 13, 13, 2, 0, 0, 0, 0xFF);
                fillRect(tessellator, left + 2, top + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 0xFF);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }
        }
    }

    // Copied from ItemRenderer.
    private void fillRect(Tesselator tessellator, int x, int y, int width, int height, int red, int green, int blue, int alpha)
    {
    	RenderSystem.setShader(GameRenderer::getPositionColorShader);
    	BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    	bufferBuilder.addVertex(x + 0, y + 0, 0.0f).setColor(red, green, blue, alpha);
    	bufferBuilder.addVertex(x + 0, y + height, 0.0f).setColor(red, green, blue, alpha);
    	bufferBuilder.addVertex(x + width, y + height, 0.0f).setColor(red, green, blue, alpha);
    	bufferBuilder.addVertex(x + width, y + 0, 0.0f).setColor(red, green, blue, alpha);
	    BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private int m_slot;
    private boolean m_showQuantity;
    private int m_quantity;
    private int m_maxQuantity;
    private BiPredicate<Integer, ItemStack> m_filterValidCallback;
    private FilterUpdatedCallback m_filterUpdatedCallback;
}
