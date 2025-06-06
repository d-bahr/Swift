package swiftmod.common.gui;

import java.util.Optional;
import java.util.function.BiPredicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import swiftmod.common.MouseButton;

@OnlyIn(Dist.CLIENT)
public class GhostFluidSlot extends GuiFluidTextureButton
{
    @FunctionalInterface
    public interface FilterUpdatedCallback
    {
        void invoke(int slot, FluidStack fluidStack);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, 0, 0, FluidStack.EMPTY);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y, FluidStack itemStack)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, 0, 0, itemStack);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y, int xTexMargin, int yTexMargin)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, xTexMargin, yTexMargin, FluidStack.EMPTY);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y, int xTexMargin, int yTexMargin, FluidStack itemStack)
    {
        this(screen, x, y, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, xTexMargin, yTexMargin, itemStack);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin, int yTexMargin)
    {
        this(screen, x, y, width, height, xTexMargin, yTexMargin, FluidStack.EMPTY);
    }

    public GhostFluidSlot(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin, int yTexMargin,
            FluidStack fluidStack)
    {
        super(screen, x, y, width, height, xTexMargin, yTexMargin, fluidStack);
        m_tooltip = new GuiTooltip(screen, 0, 0, width, height);
        addChild(m_tooltip);
        m_slot = 0;
        m_showQuantity = true;
        m_maxQuantity = 64;
        m_drawBackground = false;
        setIsFilterValidCallback(null);

        m_playClickOnPress = false;
    }

    public void setIsFilterValidCallback(BiPredicate<Integer, FluidStack> callback)
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

    public void setFluidStack(FluidStack fluidStack)
    {
        if (fluidStack == FluidStack.EMPTY || m_filterValidCallback.test(m_slot, fluidStack))
        {
            super.setFluidStack(fluidStack);
            updateTooltip();
        }
    }

    public void setQuantity(int quantity)
    {
        if (!m_fluidStack.isEmpty())
        {
            m_fluidStack.setAmount(Math.min(quantity, m_showQuantity ? m_maxQuantity : 1_000));
            updateTooltip();
        }
    }

    public int getQuantity()
    {
        return m_fluidStack.getAmount();
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
        if (!m_fluidStack.isEmpty())
        {
            m_fluidStack.setAmount(Math.min(getQuantity() + amount, m_showQuantity ? m_maxQuantity : 1_000));
            updateTooltip();
        }
    }

    public void decrQuantity(int amount)
    {
        int quantity = getQuantity() - amount;
        if (quantity <= 0)
            m_fluidStack = FluidStack.EMPTY;
        else
            m_fluidStack.setAmount(quantity);
        updateTooltip();
    }

    public void clearFluidStack()
    {
        m_fluidStack = FluidStack.EMPTY;
        updateTooltip();
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

        FluidStack fluidStack = FluidStack.EMPTY;

        Optional<FluidStack> optionalFluidStack = FluidUtil.getFluidContained(itemInCursor);
        if (optionalFluidStack.isPresent())
            fluidStack = optionalFluidStack.get();

        boolean updated = false;
        boolean shift = Screen.hasShiftDown();
        boolean control = Screen.hasControlDown();

        // Left button = add
        // Right button = subtract
        if (button == MouseButton.Left)
        {
            if (shift && control)
            {
                // Add 1 mB.
                if (!m_fluidStack.isEmpty() && !fluidStack.isEmpty())
                {
                    if (fluidStack.getFluid() == m_fluidStack.getFluid())
                    {
                        incrQuantity(1);
                        updated = true;
                    }
                    else
                    {
                        setFluidStack(fluidStack);
                        setQuantity(1);
                        updated = true;
                    }
                }
                else if (!m_fluidStack.isEmpty())
                {
                    incrQuantity(1);
                    updated = true;
                }
                else if (!fluidStack.isEmpty())
                {
                    setFluidStack(fluidStack);
                    setQuantity(1);
                    updated = true;
                }
            }
            else if (shift)
            {
                // Add 16 buckets.
                if (!m_fluidStack.isEmpty() && !fluidStack.isEmpty())
                {
                    if (fluidStack.getFluid() == m_fluidStack.getFluid())
                    {
                        incrQuantity(16_000);
                        updated = true;
                    }
                    else
                    {
                        setFluidStack(fluidStack);
                        setQuantity(16_000);
                        updated = true;
                    }
                }
                else if (!m_fluidStack.isEmpty())
                {
                    incrQuantity(16_000);
                    updated = true;
                }
                else if (!fluidStack.isEmpty())
                {
                    setFluidStack(fluidStack);
                    setQuantity(16_000);
                    updated = true;
                }
            }
            else if (control)
            {
                // Add 100 mB.
                if (!m_fluidStack.isEmpty() && !fluidStack.isEmpty())
                {
                    if (fluidStack.getFluid() == m_fluidStack.getFluid())
                    {
                        incrQuantity(100);
                        updated = true;
                    }
                    else
                    {
                        setFluidStack(fluidStack);
                        setQuantity(100);
                        updated = true;
                    }
                }
                else if (!m_fluidStack.isEmpty())
                {
                    incrQuantity(100);
                    updated = true;
                }
                else if (!fluidStack.isEmpty())
                {
                    setFluidStack(fluidStack);
                    setQuantity(100);
                    updated = true;
                }
            }
            else
            {
                // Add 1 bucket.
                if (!m_fluidStack.isEmpty() && !fluidStack.isEmpty())
                {
                    if (fluidStack.getFluid() == m_fluidStack.getFluid())
                    {
                        incrQuantity(1_000);
                        updated = true;
                    }
                    else
                    {
                        setFluidStack(fluidStack);
                        setQuantity(1_000);
                        updated = true;
                    }
                }
                else if (!m_fluidStack.isEmpty())
                {
                    incrQuantity(1_000);
                    updated = true;
                }
                else if (!fluidStack.isEmpty())
                {
                    setFluidStack(fluidStack);
                    setQuantity(1_000);
                    updated = true;
                }
            }
        }
        else
        {
            if (shift && control)
            {
                // Remove 1 mb.
                decrQuantity(1);
                updated = true;
            }
            else if (shift)
            {
                // Remove 16 buckets.
                decrQuantity(16_000);
                updated = true;
            }
            else if (control)
            {
                // Remove 100 mb.
                decrQuantity(100);
                updated = true;
            }
            else
            {
                // Remove 1 bucket.
                decrQuantity(1_000);
                updated = true;
            }
        }

        if (updated && m_filterUpdatedCallback != null)
            m_filterUpdatedCallback.invoke(m_slot, m_fluidStack);

        return true;
    }
    
    private void updateTooltip()
    {
        if (getQuantity() == 0)
        {
            m_tooltip.clearText();
        }
        else
        {
            int quantity = getQuantity();
            String quantityStr;
            if (quantity >= 1000)
                quantityStr = Integer.toString(quantity / 1000) + "." + String.format("%03d", quantity % 1000) + " B";
            else
                quantityStr = Integer.toString(quantity) + " mB";
            m_tooltip.setText(Component.literal("Quantity: " + quantityStr));
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);

        if (!m_fluidStack.isEmpty())
        {
            int left = leftAbsolute();
            int top = topAbsolute();

            PoseStack matrix = new PoseStack();
            if (m_showQuantity)
            {
                Minecraft minecraft = Minecraft.getInstance();
                Font fr = minecraft.font;

                String s;
                int amount = m_fluidStack.getAmount();
                if (amount >= 1000)
                    s = String.valueOf(amount / 1000) + "B";
                else
                    s = String.valueOf(amount) + "mB";
                float r = 1.0f;
                if (s.length() > 2)
                {
                    r = 2.5f / s.length();
                    matrix.scale(r, r, 1.0f);
                }
                matrix.translate(0.0, 0.0, 200.0);
                MultiBufferSource.BufferSource b = graphics.bufferSource();
                fr.drawInBatch(s, (float) (left + 17) / r - fr.width(s), (float) (top + 16) / r - 7, 0x00FFFFFF,
                        true, matrix.last().pose(), b, Font.DisplayMode.NORMAL, 0, 15728880);
                b.endBatch();
            }
        }
    }

    private GuiTooltip m_tooltip;
    private int m_slot;
    private boolean m_showQuantity;
    private int m_maxQuantity;
    private BiPredicate<Integer, FluidStack> m_filterValidCallback;
    private FilterUpdatedCallback m_filterUpdatedCallback;
}
