package swiftmod.common.gui;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
    public GuiContainerScreen(T container, Inventory inv, Component title)
    {
        this(container, inv, title, null);
    }

    public GuiContainerScreen(T container, Inventory inv, Component title,
            ResourceLocation backgroundTexture)
    {
        super(container, inv, title);
        createBaseComponents(inv, width, height, backgroundTexture);
    }

    public GuiContainerScreen(T container, Inventory inv, Component title, int width, int height)
    {
        this(container, inv, title, width, height, null);
    }

    public GuiContainerScreen(T container, Inventory inv, Component title, int width, int height,
            ResourceLocation backgroundTexture)
    {
        super(container, inv, title);
        imageWidth = width;
        imageHeight = height;
        createBaseComponents(inv, width, height, backgroundTexture);
    }

    private void createBaseComponents(Inventory inv, int width, int height, ResourceLocation backgroundTexture)
    {
        m_playerInventory = new GuiPlayerInventory(this, 7, 0);
        m_playerInventory.setY(height - m_playerInventory.height() - 8);

        m_playerInventoryLabel = new GuiLabel(this, 8, 0, m_playerInventory.width(), SwiftGui.TEXT_HEIGHT,
                inv.getDisplayName());
        m_playerInventoryLabel.setY(m_playerInventory.top() - m_playerInventoryLabel.height() - 2);

        m_backgroundTexture = new GuiTexture(this, 0, 0, width, height, backgroundTexture);
    }

    public void add(GuiWidget widget)
    {
    	addRenderableWidget(widget);
    }

    public void remove(GuiWidget widget)
    {
        unfocusWorker(widget);
        removeWidget(widget);
    }

    private void unfocusWorker(GuiWidget widget)
    {
        widget.setFocused(false);
        if (widget == m_focusedWidget)
            m_focusedWidget = null;
        List<GuiWidget> children = widget.getChildren();
        for (int i = 0; i < children.size(); ++i)
            unfocusWorker(children.get(i));
    }

    @Override
    public final void init()
    {
        super.init();

        add(m_playerInventory);
        add(m_playerInventoryLabel);

        m_backgroundTexture.setPosition(getGuiLeft(), getGuiTop());

        earlyInit();

        for (int i = 0; i < renderables.size(); ++i)
        {
            Renderable w = renderables.get(i);
            if (w instanceof GuiWidget)
                ((GuiWidget) w).init();
        }

        lateInit();
    }

    public void earlyInit()
    {
    }

    public void lateInit()
    {
    }
    
    public void setWidth(int width)
    {
        imageWidth = width;
    }
    
    public void setHeight(int height)
    {
        imageHeight = height;
    }

    public void setSize(int width, int height)
    {
        imageWidth = width;
        imageHeight = height;
    }

    public void setBackgroundTexture(ResourceLocation texture)
    {
        m_backgroundTexture.setTexture(texture);
    }

    protected void showPlayerInventory(boolean show)
    {
        showPlayerInventory(show, show);
    }

    protected void showPlayerInventory(boolean showInventory, boolean showLabel)
    {
        if (showInventory)
            m_playerInventory.show();
        else
            m_playerInventory.hide();
        m_playerInventoryLabel.visible = showLabel;
    }

    public int top()
    {
        return getGuiTop();
    }

    public int bottom()
    {
        return top() + height();
    }

    public int left()
    {
        return getGuiLeft();
    }

    public int right()
    {
        return left() + width();
    }

    public int width()
    {
        return imageWidth;
    }

    public int height()
    {
        return imageHeight;
    }

    public int windowWidth()
    {
        return width;
    }

    public int windowHeight()
    {
        return height;
    }

    @Override
    public void containerTick()
    {
        for (int i = 0; i < renderables.size(); ++i)
        {
            Renderable w = renderables.get(i);
            if (w instanceof GuiWidget)
                ((GuiWidget) w).tick();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        m_backgroundTexture.draw(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (GuiEventListener listener : children())
        {
            if (listener instanceof GuiWidget)
            {
                if (((GuiWidget)listener).mousePressed(mouseX, mouseY, button))
                {
                    if (button == 0)
                        setDragging(true);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        for (int i = 0; i < renderables.size(); ++i)
        {
            Renderable w = renderables.get(i);
            if (w instanceof GuiWidget)
            {
                if (((GuiWidget) w).mouseDragged(mouseX, mouseY, button, dragX, dragY))
                    return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (m_focusedWidget != null)
        {
            if (m_focusedWidget.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if (m_focusedWidget != null)
        {
            if (m_focusedWidget.keyReleased(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public final boolean charTyped(char codePoint, int modifiers)
    {
        if (m_focusedWidget != null)
        {
            if (m_focusedWidget.charTyped(codePoint, modifiers))
                return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    public void requestFocus(GuiWidget widget)
    {
        for (int i = 0; i < renderables.size(); ++i)
        {
            Renderable w = renderables.get(i);
            if (w instanceof GuiWidget)
                requestFocusWorker(widget, (GuiWidget)w);
        }
    }

    private void requestFocusWorker(GuiWidget targetWidget, GuiWidget candidateWidget)
    {
        if (targetWidget == candidateWidget)
        {
            m_focusedWidget = targetWidget;
            candidateWidget.setFocused(true);
        }
        else
        {
            candidateWidget.setFocused(false);
        }
        List<GuiWidget> children = candidateWidget.getChildren();
        for (int i = 0; i < children.size(); ++i)
            requestFocusWorker(targetWidget, children.get(i));
    }

    public GuiWidget getFocusedWidget()
    {
        return m_focusedWidget;
    }

    public void clearFocus()
    {
        if (m_focusedWidget != null)
        {
            m_focusedWidget.setFocused(false);
            m_focusedWidget = null;
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener)
    {
    	if (m_focusedWidget != listener)
    	{
    		if (m_focusedWidget != null)
    			m_focusedWidget.setFocused(false);
    		if (listener instanceof GuiWidget)
    		{
	    		m_focusedWidget = (GuiWidget)listener;
	    		if (m_focusedWidget != null)
	    			m_focusedWidget.setFocused(false);
    		}
    	}
    }
    
    @Override
    public void setInitialFocus(GuiEventListener listener)
    {
    	super.setInitialFocus(listener);
    }

    public List<Component> getTooltipForItem(ItemStack itemStack)
    {
    	return getTooltipFromItem(getMinecraft(), itemStack);
    }

    // TODO: Combine to a GuiLabeledPlayerInventory class.
    protected GuiTexture m_backgroundTexture;
    protected GuiPlayerInventory m_playerInventory;
    protected GuiLabel m_playerInventoryLabel;
    protected GuiWidget m_focusedWidget;
}
