package swiftmod.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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
        super(screen, x, y, width, height, Component.empty());
        m_requestFocusOnPress = false;
        m_texture = texture;
    }

    public void setTexture(ResourceLocation texture)
    {
        m_texture = texture;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(graphics, mouseX, mouseY, partialTicks);

        if (m_texture != null)
            drawTexture(graphics, m_texture, getX(), getY(), width, height);
    }

    public static void drawTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width,
            int height)
    {
        drawTexture(graphics, texture, x, y, width, height, 0, 0);
    }

    public static void drawTexture(GuiGraphics graphics, ResourceLocation texture,
            int x, int y, int width, int height, int horizontalMargin, int verticalMargin)
    {
        if (texture != null)
        {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            graphics.blit(texture, x + horizontalMargin, y + verticalMargin, 0.0f, 0.0f, width, height,
                    width - horizontalMargin * 2, height - verticalMargin * 2);
        }
    }

    private ResourceLocation m_texture;
}
