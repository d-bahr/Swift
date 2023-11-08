package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Notification;
import swiftmod.common.Swift;
import swiftmod.common.channels.ChannelOwner;
import swiftmod.common.channels.ChannelSpec;

@OnlyIn(Dist.CLIENT)
public class ChannelSelectionWidget extends GuiWidget
{
    @FunctionalInterface
    public interface ChannelCallback
    {
        void onChannel(ChannelSpec channel);
    }

    public ChannelSelectionWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public ChannelSelectionWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, 156, 8);
    }

    public ChannelSelectionWidget(GuiContainerScreen<?> screen, int x, int y, int numScrollPanelRows)
    {
        this(screen, x, y, 156, numScrollPanelRows);
    }

    public ChannelSelectionWidget(GuiContainerScreen<?> screen, int x, int y, int width, int numScrollPanelRows)
    {
        super(screen, x, y, width, 148, Component.empty());

        final int verticalMargin = 2;
        final int horizontalMargin = 2;

        m_privateChannelNames = new ArrayList<Component>();
        m_publicChannelNames = new ArrayList<Component>();
        m_showPrivateChannels = true;

        m_publicButton = new GuiTextButton(screen, 0, 0, width / 2 - horizontalMargin, 20, BIG_BUTTON_TEXTURE,
                BIG_BUTTON_HIGHLIGHTED_TEXTURE, Component.translatable("swift.text.public"),
                this::onPublicButtonClick);
        m_publicButton.active = true;
        m_publicButton.setBackgroundInactiveTexture(BIG_BUTTON_INACTIVE_TEXTURE);
        addChild(m_publicButton);

        m_privateButton = new GuiTextButton(screen, m_publicButton.right() + horizontalMargin * 2, m_publicButton.top(),
                m_publicButton.width(), 20, BIG_BUTTON_TEXTURE, BIG_BUTTON_HIGHLIGHTED_TEXTURE,
                Component.translatable("swift.text.private"), this::onPrivateButtonClick);
        m_privateButton.active = false;
        m_privateButton.setBackgroundInactiveTexture(BIG_BUTTON_INACTIVE_TEXTURE);
        addChild(m_privateButton);

        m_panel = new GuiTextScrollPanel(screen, m_publicButton.left(), m_privateButton.bottom() + verticalMargin,
                width, numScrollPanelRows);
        m_panel.setItemSelectedHandler(this::onChannelItemSelected);
        addChild(m_panel);

        GuiButton deleteButton = new GuiTextureButton(screen, m_privateButton.right() - 20,
                m_panel.bottom() + verticalMargin, 20, 20, DELETE_BUTTON_TEXTURE,
                this::onDeleteButtonClick);
        deleteButton.setTooltip(Component.literal("Delete channel"));
        addChild(deleteButton);

        GuiButton addButton = new GuiTextureButton(screen, deleteButton.left() - 20, deleteButton.getY(), 20, 20,
                ADD_BUTTON_TEXTURE, this::onAddButtonClick);
        addButton.setTooltip(Component.literal("Add channel"));
        addChild(addButton);

        m_textField = new GuiTextField(screen, 0, addButton.getY(), addButton.left() - 2, 20, Component.empty());
        m_textField.setTextColor(-1);
        m_textField.setDisabledTextColour(-1);
        m_textField.setEnableBackgroundDrawing(false);
        m_textField.setMaxStringLength(16);
        m_textField.setScale(1.15f);
        addChild(m_textField);
        screen.setInitialFocus(m_textField);

        GuiButton setButton = new GuiTextButton(screen, m_publicButton.left(), m_textField.bottom() + verticalMargin,
                m_publicButton.getWidth(), 20, BIG_BUTTON_TEXTURE, BIG_BUTTON_HIGHLIGHTED_TEXTURE,
                Component.translatable("swift.text.set"), this::onSetButtonClick);
        addChild(setButton);

        GuiButton unsetButton = new GuiTextButton(screen, m_privateButton.left(), setButton.top(),
                m_privateButton.getWidth(), 20, BIG_BUTTON_TEXTURE, BIG_BUTTON_HIGHLIGHTED_TEXTURE,
                Component.translatable("swift.text.unset"), this::onUnsetButtonClick);
        addChild(unsetButton);
        
        // Easiest to do this at the end...
        setHeight(unsetButton.bottom() + verticalMargin);

        updateChannelsList();

        m_channelAddCallback = null;
        m_channelDeleteCallback = null;
        m_channelSetCallback = null;
        m_channelUnsetCallback = null;
    }
    
    public void requestTextFieldFocus()
    {
    	m_textField.requestFocus();
    }

    public void setAddChannelCallback(ChannelCallback callback)
    {
        m_channelAddCallback = callback;
    }

    public void setDeleteChannelCallback(ChannelCallback callback)
    {
        m_channelDeleteCallback = callback;
    }

    public void setSetChannelCallback(ChannelCallback callback)
    {
        m_channelSetCallback = callback;
    }

    public void setUnsetChannelCallback(Notification callback)
    {
        m_channelUnsetCallback = callback;
    }

    public void setPrivateChannels(SortedSet<String> channels)
    {
        m_privateChannelNames.clear();
        for (String s : channels)
            m_privateChannelNames.add(Component.literal(s));
    }

    public void setPrivateChannels(List<String> channels)
    {
        m_privateChannelNames.clear();
        for (String s : channels)
            m_privateChannelNames.add(Component.literal(s));
        m_privateChannelNames.sort((a, b) ->
        {
            return a.getString().compareTo(b.getString());
        });
    }

    public void setPublicChannels(SortedSet<String> channels)
    {
        m_publicChannelNames.clear();
        for (String s : channels)
            m_publicChannelNames.add(Component.literal(s));
    }

    public void setPublicChannels(List<String> channels)
    {
        m_publicChannelNames.clear();
        for (String s : channels)
            m_publicChannelNames.add(Component.literal(s));
        m_publicChannelNames.sort((a, b) ->
        {
            return a.getString().compareTo(b.getString());
        });
    }

    public void setCurrentChannel(ChannelSpec channelSpec, boolean displayNow)
    {
        m_currentChannel = channelSpec;
        if (displayNow)
        {
            if (m_currentChannel != null)
                m_showPrivateChannels = m_currentChannel.owner.isPrivate();
            else
                m_showPrivateChannels = false;
        }

        updateChannelsList();
    }

    private void updateChannelsList()
    {
        m_publicButton.active = m_showPrivateChannels;
        m_privateButton.active = !m_showPrivateChannels;

        List<Component> channelNames;

        if (m_showPrivateChannels)
            channelNames = m_privateChannelNames;
        else
            channelNames = m_publicChannelNames;

        m_panel.setText(channelNames);
        if (m_currentChannel != null)
        {
            boolean isChannelPrivate = m_currentChannel.owner.isPrivate();
            if (isChannelPrivate == m_showPrivateChannels)
            {
                for (int i = 0; i < channelNames.size(); ++i)
                {
                    if (channelNames.get(i).getString().equals(m_currentChannel.name))
                    {
                        m_panel.setSelected(i);
                        return;
                    }
                }
            }
        }

        m_panel.clearSelection();
    }

    private void onPrivateButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        m_showPrivateChannels = true;
        updateChannelsList();
    }

    private void onPublicButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        m_showPrivateChannels = false;
        updateChannelsList();
    }

    private void onAddButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        Component textComponent = m_textField.getText();
        String text = textComponent.getString();
        if (!text.isEmpty())
        {
            ChannelSpec spec;

            if (m_showPrivateChannels)
            {
                spec = new ChannelSpec(new ChannelOwner(getPlayer().getUUID()), text);

                boolean inserted = false;
                for (int i = 0; i < m_privateChannelNames.size(); ++i)
                {
                    int cmp = text.compareTo(m_privateChannelNames.get(i).getString());
                    if (cmp == 0)
                    {
                        // Already exists
                        return;
                    }
                    else if (cmp < 0)
                    {
                        m_privateChannelNames.add(i, textComponent);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted)
                    m_privateChannelNames.add(textComponent);
            }
            else
            {
                spec = new ChannelSpec(ChannelOwner.Public, text);

                boolean inserted = false;
                for (int i = 0; i < m_publicChannelNames.size(); ++i)
                {
                    int cmp = text.compareTo(m_publicChannelNames.get(i).getString());
                    if (cmp == 0)
                    {
                        // Already exists
                        return;
                    }
                    else if (cmp < 0)
                    {
                        m_publicChannelNames.add(i, textComponent);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted)
                    m_publicChannelNames.add(textComponent);
            }

            updateChannelsList();

            if (m_channelAddCallback != null)
                m_channelAddCallback.onChannel(spec);
        }
    }

    private void onDeleteButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        String text = m_textField.getText().getString();
        if (!text.isEmpty())
        {
            ChannelSpec spec;
            if (m_showPrivateChannels)
            {
                spec = new ChannelSpec(new ChannelOwner(getPlayer().getUUID()), text);
                for (int i = 0; i < m_privateChannelNames.size(); ++i)
                {
                    if (text.compareTo(m_privateChannelNames.get(i).getString()) == 0)
                    {
                        m_privateChannelNames.remove(i);
                        break;
                    }
                }
            }
            else
            {
                spec = new ChannelSpec(ChannelOwner.Public, text);
                for (int i = 0; i < m_publicChannelNames.size(); ++i)
                {
                    if (text.compareTo(m_publicChannelNames.get(i).getString()) == 0)
                    {
                        m_publicChannelNames.remove(i);
                        break;
                    }
                }
            }

            boolean unsetChannel = false;
            if (m_currentChannel != null)
            {
                boolean isChannelPrivate = m_currentChannel.owner.isPrivate();
                if (m_showPrivateChannels == isChannelPrivate && m_currentChannel.name == text)
                {
                    m_currentChannel = null;
                    unsetChannel = true;
                }
            }

            updateChannelsList();

            if (unsetChannel)
            {
                if (m_channelUnsetCallback != null)
                    m_channelUnsetCallback.invoke();
            }

            if (m_channelDeleteCallback != null)
                m_channelDeleteCallback.onChannel(spec);
        }
    }

    private void onSetButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        Component textComponent = m_textField.getText();
        String text = textComponent.getString();
        if (!text.isEmpty())
        {
            ChannelSpec spec;
            
            boolean channelAdded = false;

            if (m_showPrivateChannels)
            {
                spec = new ChannelSpec(new ChannelOwner(getPlayer().getUUID()), text);

                boolean inserted = false;
                for (int i = 0; i < m_privateChannelNames.size(); ++i)
                {
                    int cmp = text.compareTo(m_privateChannelNames.get(i).getString());
                    if (cmp == 0)
                    {
                        // Already exists
                        inserted = true;
                        break;
                    }
                    else if (cmp < 0)
                    {
                        m_privateChannelNames.add(i, textComponent);
                        inserted = true;
                        channelAdded = true;
                        break;
                    }
                }
                if (!inserted)
                {
                    m_privateChannelNames.add(textComponent);
                    channelAdded = true;
                }
            }
            else
            {
                spec = new ChannelSpec(ChannelOwner.Public, text);

                boolean inserted = false;
                for (int i = 0; i < m_publicChannelNames.size(); ++i)
                {
                    int cmp = text.compareTo(m_publicChannelNames.get(i).getString());
                    if (cmp == 0)
                    {
                        // Already exists
                        inserted = true;
                        break;
                    }
                    else if (cmp < 0)
                    {
                        m_publicChannelNames.add(i, textComponent);
                        inserted = true;
                        channelAdded = true;
                        break;
                    }
                }
                if (!inserted)
                {
                    m_publicChannelNames.add(textComponent);
                    channelAdded = true;
                }
            }

            m_currentChannel = spec;
            updateChannelsList();

            if (channelAdded)
            {
                if (m_channelAddCallback != null)
                    m_channelAddCallback.onChannel(spec);
            }
            if (m_channelSetCallback != null)
                m_channelSetCallback.onChannel(spec);
        }
    }

    private void onUnsetButtonClick(GuiWidget button, MouseButton mouseButton)
    {
        m_currentChannel = null;
        updateChannelsList();

        if (m_channelUnsetCallback != null)
            m_channelUnsetCallback.invoke();
    }

    private void onChannelItemSelected(int index, Component name)
    {
        if (name != null)
            m_textField.setText(name.getString());
        else
            m_textField.clearText();
    }

    /*
     * @Override public boolean onKeyPress(int keyCode, int scanCode, int modifiers) { if
     * (InputMappings.getInputByCode(keyCode, scanCode) ==
     * InputMappings.getInputByName("key.keyboard.escape")) getPlayer().closeScreen();
     * 
     * return !m_textField.keyPressed(keyCode, scanCode, modifiers) && !m_textField.canWrite() ?
     * super.keyPressed(keyCode, scanCode, modifiers) : true; }
     */

    protected static final ResourceLocation ADD_BUTTON_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/add_button.png");
    protected static final ResourceLocation DELETE_BUTTON_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/subtract_button.png");
    protected static final ResourceLocation BIG_BUTTON_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/big_button.png");
    protected static final ResourceLocation BIG_BUTTON_HIGHLIGHTED_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/big_button_highlighted.png");
    protected static final ResourceLocation BIG_BUTTON_INACTIVE_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/big_button_inactive.png");

    protected List<Component> m_privateChannelNames;
    protected List<Component> m_publicChannelNames;
    protected ChannelSpec m_currentChannel;
    protected GuiButton m_publicButton;
    protected GuiButton m_privateButton;
    protected GuiTextField m_textField;
    protected GuiTextScrollPanel m_panel;
    protected boolean m_showPrivateChannels;
    protected ChannelCallback m_channelAddCallback;
    protected ChannelCallback m_channelDeleteCallback;
    protected ChannelCallback m_channelSetCallback;
    protected Notification m_channelUnsetCallback;
}
