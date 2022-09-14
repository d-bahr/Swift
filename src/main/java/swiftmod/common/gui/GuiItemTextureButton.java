package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);
        renderItem(getItemRenderer(), m_itemStack, leftAbsolute(), topAbsolute(), width - m_xTexMargin, height - m_yTexMargin);
    }

    public static void renderItem(ItemRenderer renderer, ItemStack stack, int x, int y, int xScale, int yScale)
    {
    	Minecraft minecraft = Minecraft.getInstance();
    	minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        BakedModel model = renderer.getModel(stack, null, null, 0);
	    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
	    RenderSystem.enableBlend();
	    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    PoseStack posestack = RenderSystem.getModelViewStack();
	    posestack.pushPose();
	    posestack.translate((double)x, (double)y, 100.0F);
	    posestack.translate(8.0D, 8.0D, 0.0D);
	    posestack.scale(1.0F, -1.0F, 1.0F);
	    posestack.scale(xScale, yScale, xScale);
	    RenderSystem.applyModelViewMatrix();
	    PoseStack posestack1 = new PoseStack();
	    MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
	    boolean flag = !model.usesBlockLight();
	    if (flag) {
	       Lighting.setupForFlatItems();
	    }
	
	    renderer.render(stack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, model);
	    multibuffersource$buffersource.endBatch();
	    RenderSystem.enableDepthTest();
	    if (flag) {
	       Lighting.setupFor3DItems();
	    }
	
	    posestack.popPose();
	    RenderSystem.applyModelViewMatrix();
    }

    protected ItemStack m_itemStack;
    protected int m_xTexMargin;
    protected int m_yTexMargin;
}
