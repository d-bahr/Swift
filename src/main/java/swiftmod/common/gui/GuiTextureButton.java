package swiftmod.common.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextureButton extends GuiButton
{
    public GuiTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation texture)
    {
        this(screen, x, y, width, height, 0, 0, texture);
    }

    public GuiTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, ResourceLocation texture)
    {
        super(screen, x, y, width, height);
        m_foregroundTexture = new GuiTexture(screen, xTexMargin, yTexMargin, width, height, texture);
        addChild(m_foregroundTexture);
    }

    public GuiTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation texture, IClickable onClick)
    {
        this(screen, x, y, width, height, 0, 0, texture, onClick);
    }

    public GuiTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, ResourceLocation texture, IClickable onClick)
    {
        super(screen, x, y, width, height, onClick);
        m_foregroundTexture = new GuiTexture(screen, xTexMargin, yTexMargin, width - xTexMargin * 2,
                height - yTexMargin * 2, texture);
        addChild(m_foregroundTexture);
    }

    public void setMargin(int x, int y)
    {
        m_foregroundTexture.x = x;
        m_foregroundTexture.y = y;
        m_foregroundTexture.setWidth(width - x * 2);
        m_foregroundTexture.setHeight(height - y * 2);
    }

    public void setForegroundTexture(ResourceLocation texture)
    {
        m_foregroundTexture.setTexture(texture);
    }

    private GuiTexture m_foregroundTexture;
}
