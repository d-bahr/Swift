package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

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

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
        if (!m_fluidStack.isEmpty())
        {
            int left = left() + m_xTexMargin;
            int top = top() + m_yTexMargin;
            int w = width - (m_xTexMargin * 2);
            int h = height - (m_yTexMargin * 2);
            renderFluid(matrixStack, m_fluidStack, left, top, w, h, alpha);
        }
    }

    public void renderFluid(PoseStack matrixStack, FluidStack fluidStack, int x, int y, int width, int height)
    {
        renderFluid(matrixStack, fluidStack, x, y, width, height, 0.0f);
    }

    public void renderFluid(PoseStack matrixStack, FluidStack fluidStack, int x, int y, int width, int height, float alpha)
    {
        if (!fluidStack.isEmpty())
        {
            Minecraft minecraft = Minecraft.getInstance();
            FluidAttributes attrs = fluidStack.getFluid().getAttributes();
            ResourceLocation fluidTexture = attrs.getStillTexture(fluidStack);
            TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidTexture);
            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            int color = attrs.getColor();
            float r = ((color >> 16) & 0xFF) / 256.0f;
            float g = ((color >> 8) & 0xFF) / 256.0f;
            float b = (color & 0xFF) / 256.0f;
            float a = ((color >> 24) & 0xFF) / 256.0f;
            RenderSystem.setShaderColor(r, g, b, a); // TODO: Might need to be RenderSystem.setShaderColor instead.
            blit(matrixStack, x, y, 0, width, height, sprite);
            //RenderSystem.clearCurrentColor();
        }
    }

    protected FluidStack m_fluidStack;
    protected int m_xTexMargin;
    protected int m_yTexMargin;
}
