package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);
        doRenderItem(graphics);
    }
    
    protected void doRenderItem(GuiGraphics graphics)
    {
        renderItem(graphics, m_itemStack, getX(), getY(), width - m_xTexMargin, height - m_yTexMargin);
    }

    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, int xScale, int yScale)
    {
    	Minecraft minecraft = Minecraft.getInstance();
    	ItemRenderer renderer = minecraft.getItemRenderer();
        BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + 8.0f, y + 8.0f, 150.0f);
	    pose.mulPose((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
	    pose.scale(xScale, yScale, xScale);
	    boolean flag = !bakedmodel.usesBlockLight();
	    if (flag)
	        Lighting.setupForFlatItems();
	    renderer.render(stack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
	    graphics.flush();
	    if (flag)
	        Lighting.setupFor3DItems();
	    pose.popPose();
    }

    protected ItemStack m_itemStack;
    protected int m_xTexMargin;
    protected int m_yTexMargin;
}
