package swiftmod.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTexture extends GuiWidget
{
    public GuiTexture(GuiContainerScreen<?> screen, int width, int height, ResourceLocation resourceLocation)
    {
        this(screen, 0, 0, width, height, resourceLocation);
    }

    public GuiTexture(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation texture)
    {
        super(screen, x, y, width, height, TextComponent.EMPTY);
        m_requestFocusOnPress = false;
        m_texture = texture;
    }

    public void setTexture(ResourceLocation texture)
    {
        m_texture = texture;
    }

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);

        if (m_texture != null)
            drawTexture(m_texture, matrixStack, x, y, width, height);
    }

    public static void drawTexture(ResourceLocation texture, PoseStack matrixStack, int x, int y, int width,
            int height)
    {
        drawTexture(texture, matrixStack, x, y, width, height, 0, 0);
    }

    public static void drawTexture(ResourceLocation texture, PoseStack matrixStack,
            int x, int y, int width, int height, int horizontalMargin, int verticalMargin)
    {
        if (texture != null)
        {
        	RenderSystem.setShaderTexture(0, texture);
            RenderSystem.enableDepthTest();
            blit(matrixStack, x + horizontalMargin, y + verticalMargin, 0.0f, 0.0f, width, height,
                    width - horizontalMargin * 2, height - verticalMargin * 2);
        }
    }

    private ResourceLocation m_texture;
}
