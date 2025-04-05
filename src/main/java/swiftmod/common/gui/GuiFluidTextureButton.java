package swiftmod.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class GuiFluidTextureButton extends GuiButton
{
    public GuiFluidTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, FluidStack fluidStack)
    {
        super(screen, x, y, width, height);
        m_xTexMargin = xTexMargin;
        m_yTexMargin = yTexMargin;
        m_fluidStack = fluidStack.copy();
    }

    public GuiFluidTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, FluidStack fluidStack, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        m_xTexMargin = xTexMargin;
        m_yTexMargin = yTexMargin;
        m_fluidStack = fluidStack.copy();
    }

    public void setMargin(int x, int y)
    {
        m_xTexMargin = x;
        m_yTexMargin = y;
    }

    public void setFluidStack(FluidStack fluidStack)
    {
        m_fluidStack = fluidStack.copy();
    }

    public FluidStack getFluidStack()
    {
        return m_fluidStack;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);
        if (!m_fluidStack.isEmpty())
        {
            int left = left() + m_xTexMargin;
            int top = top() + m_yTexMargin;
            int w = width - (m_xTexMargin * 2);
            int h = height - (m_yTexMargin * 2);
            renderFluid(graphics, m_fluidStack, left, top, w, h, alpha);
        }
    }

    public void renderFluid(GuiGraphics graphics, FluidStack fluidStack, int x, int y, int width, int height)
    {
        renderFluid(graphics, fluidStack, x, y, width, height, 0.0f);
    }

    public void renderFluid(GuiGraphics graphics, FluidStack fluidStack, int x, int y, int width, int height, float alpha)
    {
        if (!fluidStack.isEmpty())
        {
            Minecraft minecraft = Minecraft.getInstance();
            IClientFluidTypeExtensions attrs = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation fluidTexture = attrs.getStillTexture();
            TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidTexture);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            int color = attrs.getTintColor();
            float r = ((color >> 16) & 0xFF) / 256.0f;
            float g = ((color >> 8) & 0xFF) / 256.0f;
            float b = (color & 0xFF) / 256.0f;
            float a = ((color >> 24) & 0xFF) / 256.0f;
            RenderSystem.setShaderColor(r, g, b, a);
            graphics.blit(x, y, 0, width, height, sprite);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    protected FluidStack m_fluidStack;
    protected int m_xTexMargin;
    protected int m_yTexMargin;
}
