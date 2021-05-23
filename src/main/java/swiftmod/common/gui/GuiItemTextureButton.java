package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiItemTextureButton extends GuiButton
{
    public GuiItemTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, ItemStack itemStack)
    {
        super(screen, x, y, width, height);
        m_xTexMargin = xTexMargin;
        m_yTexMargin = yTexMargin;
        m_itemStack = itemStack.copy();
    }

    public GuiItemTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, ItemStack itemStack, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        m_xTexMargin = xTexMargin;
        m_yTexMargin = yTexMargin;
        m_itemStack = itemStack.copy();
    }

    public void setMargin(int x, int y)
    {
        m_xTexMargin = x;
        m_yTexMargin = y;
    }

    public void setItemStack(ItemStack itemStack)
    {
        m_itemStack = itemStack.copy();
    }

    public ItemStack getItemStack()
    {
        return m_itemStack;
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
        renderItem(m_itemStack, leftAbsolute(), topAbsolute(), width - m_xTexMargin, height - m_yTexMargin);
    }

    public static void renderItem(ItemStack stack, int x, int y, int xScale, int yScale)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        TextureManager textureManager = minecraft.getTextureManager();
        IBakedModel bakedmodel = renderer.getModel(stack, null, minecraft.player);
        RenderSystem.pushMatrix();
        textureManager.bind(PlayerContainer.BLOCK_ATLAS);
        textureManager.getTexture(PlayerContainer.BLOCK_ATLAS).setBlurMipmap(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float) x, (float) y, 100.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(xScale, yScale, xScale);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedmodel.isGui3d();
        if (flag)
        {
            RenderHelper.setupForFlatItems();
        }

        renderer.render(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, renderTypeBuffer,
                15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        renderTypeBuffer.endBatch();
        RenderSystem.enableDepthTest();
        if (flag)
        {
            RenderHelper.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    protected ItemStack m_itemStack;
    protected int m_xTexMargin;
    protected int m_yTexMargin;
}
