package swiftmod.common.gui;

import java.util.function.BiPredicate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.SwiftKeyBindings;

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
    public void playDownSound(SoundHandler handler)
    {
        // Don't call super -- suppress all normal button sounds.
    }

    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        ItemStack itemInCursor = getPlayer().inventory.getCarried();
        boolean updated = false;
        boolean isSneaking = SwiftKeyBindings.isSneakKeyPressed();
        boolean isSprinting = SwiftKeyBindings.isSprintKeyPressed();

        // Left button = add
        // Right button = subtract
        if (button == MouseButton.Left)
        {
            if (isSneaking)
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
            else if (isSprinting)
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
            if (isSneaking)
            {
                // Remove a stack.
                if (!m_itemStack.isEmpty())
                {
                    decrQuantity(m_itemStack.getMaxStackSize());
                    updated = true;
                }
            }
            else if (isSprinting)
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

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);

        if (!m_itemStack.isEmpty() && m_quantity > 0)
        {
            int left = leftAbsolute();
            int top = topAbsolute();

            MatrixStack matrix = new MatrixStack();
            if (m_showQuantity && m_quantity > 1)
            {
                Minecraft minecraft = Minecraft.getInstance();
                FontRenderer fr = minecraft.font;

                String s = String.valueOf(m_quantity);
                float r = 1.0f;
                if (s.length() > 2)
                {
                    r = 2.0f / s.length();
                    matrix.scale(r, r, 1.0f);
                }
                matrix.translate(0.0, 0.0, 200.0);
                IRenderTypeBuffer.Impl b = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
                fr.drawInBatch(s, (float) (left + 17) / r - fr.width(s), (float) (top + 16) / r - 7, 0x00FFFFFF,
                        true, matrix.last().pose(), b, false, 0, 15728880);
                b.endBatch();
            }

            if (m_itemStack.getItem().showDurabilityBar(m_itemStack))
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();
                double health = m_itemStack.getItem().getDurabilityForDisplay(m_itemStack);
                int i = Math.round(13.0F - (float) health * 13.0F);
                int j = m_itemStack.getItem().getRGBDurabilityForDisplay(m_itemStack);
                fillRect(bufferbuilder, left + 2, top + 13, 13, 2, 0, 0, 0, 0xFF);
                fillRect(bufferbuilder, left + 2, top + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 0xFF);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    // Copied from ItemRenderer.
    private void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
            int alpha)
    {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().end();
    }

    private int m_slot;
    private boolean m_showQuantity;
    private int m_quantity;
    private int m_maxQuantity;
    private BiPredicate<Integer, ItemStack> m_filterValidCallback;
    private FilterUpdatedCallback m_filterUpdatedCallback;
}
