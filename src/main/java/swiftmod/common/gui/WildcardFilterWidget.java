package swiftmod.common.gui;

import java.util.Collection;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class WildcardFilterWidget extends GuiWidget
{
    @FunctionalInterface
    public interface FilterCallback
    {
        void invoke(String filter);
    }

    public WildcardFilterWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public WildcardFilterWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, 156, 8);
    }

    public WildcardFilterWidget(GuiContainerScreen<?> screen, int x, int y, int numScrollPanelRows)
    {
        this(screen, x, y, 156, numScrollPanelRows);
    }

    public WildcardFilterWidget(GuiContainerScreen<?> screen, int x, int y, int width, int numScrollPanelRows)
    {
        super(screen, x, y, width, 104, Component.empty());

        final int verticalMargin = 2;

        m_panel = new GuiTextScrollPanel(screen, 0, 0, width, numScrollPanelRows);
        m_panel.setItemSelectedHandler(this::onFilterSelected);
        addChild(m_panel);

        GuiButton deleteButton = new GuiTextureButton(screen, m_panel.right() - 20,
                m_panel.bottom() + verticalMargin, 20, 20, DELETE_BUTTON_TEXTURE,
                this::onDeleteButtonClick);
        deleteButton.setTooltip(Component.literal("Delete filter"));
        addChild(deleteButton);

        GuiButton addButton = new GuiTextureButton(screen, deleteButton.left() - 20, deleteButton.getY(), 20, 20,
                ADD_BUTTON_TEXTURE, this::onAddButtonClick);
        addButton.setTooltip(Component.literal("Add filter"));
        addChild(addButton);

        m_textField = new GuiTextField(screen, 0, addButton.getY(), addButton.left() - 2, 20, Component.empty());
        m_textField.setTextColor(-1);
        m_textField.setDisabledTextColour(-1);
        m_textField.setEnableBackgroundDrawing(false);
        m_textField.setMaxStringLength(16);
        m_textField.setScale(1.15f);
        addChild(m_textField);
        screen.setInitialFocus(m_textField);

        // Easiest to do this at the end...
        setHeight(addButton.bottom());

        m_filterAddCallback = null;
        m_filterRemoveCallback = null;
    }
    
    public void requestTextFieldFocus()
    {
    	m_textField.requestFocus();
    }

    public void setAddCallback(FilterCallback callback)
    {
        m_filterAddCallback = callback;
    }

    public void setRemoveCallback(FilterCallback callback)
    {
        m_filterRemoveCallback = callback;
    }

    public void setFilters(Collection<String> filters)
    {
        m_panel.clearText();
        for (String f : filters)
            m_panel.addText(Component.literal(f));
    }

    private void onAddButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        Component textComponent = m_textField.getText();
        String text = textComponent.getString();
        if (!text.isEmpty())
        {
            List<Component> filters = m_panel.getText();
            for (int i = 0; i < filters.size(); ++i)
            {
                if (text.equals(filters.get(i).getString()))
                {
                    // Already exists.
                    return;
                }
            }

            m_panel.addText(textComponent);

            if (m_filterAddCallback != null)
                m_filterAddCallback.invoke(text);
        }
    }

    private void onDeleteButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        Component textComponent = m_textField.getText();
        String text = textComponent.getString();
        if (!text.isEmpty())
        {
            boolean removed = m_panel.removeText(textComponent);
            m_panel.clearSelection();

            if (removed && m_filterRemoveCallback != null)
                m_filterRemoveCallback.invoke(text);
        }
    }

    private void onFilterSelected(int index, Component name)
    {
        if (name != null)
            m_textField.setText(name.getString());
        else
            m_textField.clearText();
    }

    protected static final ResourceLocation ADD_BUTTON_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/add_button.png");
    protected static final ResourceLocation DELETE_BUTTON_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/subtract_button.png");

    protected GuiTextField m_textField;
    protected GuiTextScrollPanel m_panel;
    protected FilterCallback m_filterAddCallback;
    protected FilterCallback m_filterRemoveCallback;
}
