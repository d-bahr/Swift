package swiftmod.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
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
        super(screen, x, y, width, height, StringTextComponent.EMPTY);
        m_texture = texture;
    }

    public void setTexture(ResourceLocation texture)
    {
        m_texture = texture;
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);

        if (m_texture != null)
            drawTexture(m_texture, matrixStack, x, y, width, height);
    }

    public static void drawTexture(ResourceLocation texture, MatrixStack matrixStack, int x, int y, int width,
            int height)
    {
        Minecraft mc = Minecraft.getInstance();
        drawTexture(mc.getTextureManager(), texture, matrixStack, x, y, width, height, 0, 0);
    }

    public static void drawTexture(Minecraft mc, ResourceLocation texture, MatrixStack matrixStack, int x, int y,
            int width, int height)
    {
        drawTexture(mc.getTextureManager(), texture, matrixStack, x, y, width, height, 0, 0);
    }

    public static void drawTexture(TextureManager textureManager, ResourceLocation texture, MatrixStack matrixStack,
            int x, int y, int width, int height)
    {
        drawTexture(textureManager, texture, matrixStack, x, y, width, height, 0, 0);
    }

    public static void drawTexture(ResourceLocation texture, MatrixStack matrixStack, int x, int y, int width,
            int height, int horizontalMargin, int verticalMargin)
    {
        Minecraft mc = Minecraft.getInstance();
        drawTexture(mc.getTextureManager(), texture, matrixStack, x, y, width, height, horizontalMargin,
                verticalMargin);
    }

    public static void drawTexture(Minecraft mc, ResourceLocation texture, MatrixStack matrixStack, int x, int y,
            int width, int height, int horizontalMargin, int verticalMargin)
    {
        drawTexture(mc.getTextureManager(), texture, matrixStack, x, y, width, height, horizontalMargin,
                verticalMargin);
    }

    public static void drawTexture(TextureManager textureManager, ResourceLocation texture, MatrixStack matrixStack,
            int x, int y, int width, int height, int horizontalMargin, int verticalMargin)
    {
        if (texture != null)
        {
            textureManager.bind(texture);
            RenderSystem.enableDepthTest();
            blit(matrixStack, x + horizontalMargin, y + verticalMargin, 0.0f, 0.0f, width, height,
                    width - horizontalMargin * 2, height - verticalMargin * 2);
        }
    }

    private ResourceLocation m_texture;
}
