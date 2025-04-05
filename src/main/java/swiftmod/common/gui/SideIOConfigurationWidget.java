package swiftmod.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Color;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;

@OnlyIn(Dist.CLIENT)
public class SideIOConfigurationWidget extends GuiWidget
{
	public enum FilterType
	{
		None,
		Item,
		Fluid
	}
	
    public SideIOConfigurationWidget(GuiContainerScreen<?> screen, FilterType type)
    {
        this(screen, type, 0, 0);
    }

    public SideIOConfigurationWidget(GuiContainerScreen<?> screen, FilterType type, int x, int y)
    {
        super(screen, x, y, 176, 96, Component.empty());
		
		m_speedUpgradeSlotTexture = new GuiTexture(screen, SPEED_UPGRADE_SLOT_X, SPEED_UPGRADE_SLOT_Y,
				SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT, SPEED_UPGRADE_SLOT_TEXTURE);
		m_stackUpgradeSlotTexture = new GuiTexture(screen, STACK_UPGRADE_SLOT_X, STACK_UPGRADE_SLOT_Y,
				SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT, STACK_UPGRADE_SLOT_TEXTURE);
		
		if (type == FilterType.Item)
		{
			m_filterUpgradeSlotTexture = new GuiTexture(screen, FILTER_UPGRADE_SLOT_X, FILTER_UPGRADE_SLOT_Y,
					SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT, ITEM_FILTER_UPGRADE_SLOT_TEXTURE);
		}
		else
		{
			m_filterUpgradeSlotTexture = new GuiTexture(screen, FILTER_UPGRADE_SLOT_X, FILTER_UPGRADE_SLOT_Y,
					SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT, FLUID_FILTER_UPGRADE_SLOT_TEXTURE);
			m_filterUpgradeSlotTexture.visible = type == FilterType.Fluid;
		}
		
        m_transferDirectionButton = new TransferDirectionButton(screen, 18, 37, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT);
        m_redstoneButton = new RedstoneButton(screen, 36, 37, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT);
        m_colorSelectionButton = new ColorSelectionButton(screen, 54, 37, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT);
        m_priorityButton = new GuiTextureButton(screen, 72, 37, SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT, PRIORITY_BUTTON_TEXTURE);
        m_priorityButton.setTooltip(Component.literal("Change priority"));

        m_filterSettingsButton = new GuiSettingsButton(screen, 108, 37);
        m_filterSettingsButton.setTooltip(Component.literal("Change filter settings"));
        
        addChild(m_speedUpgradeSlotTexture);
        addChild(m_stackUpgradeSlotTexture);
        addChild(m_filterUpgradeSlotTexture);
        addChild(m_colorSelectionButton);
        addChild(m_transferDirectionButton);
        addChild(m_redstoneButton);
        addChild(m_priorityButton);
        addChild(m_filterSettingsButton);
    }
    
    public void setFilterType(FilterType type)
    {
    	if (type == FilterType.Item)
    	{
    		m_filterUpgradeSlotTexture.setTexture(ITEM_FILTER_UPGRADE_SLOT_TEXTURE);
    		m_filterUpgradeSlotTexture.visible = true;
    	}
    	else if (type == FilterType.Fluid)
    	{
    		m_filterUpgradeSlotTexture.setTexture(FLUID_FILTER_UPGRADE_SLOT_TEXTURE);
    		m_filterUpgradeSlotTexture.visible = true;
    	}
    	else
    	{
    		m_filterUpgradeSlotTexture.visible = false;
    	}
    }

    public void setColorChangedCallback(ColorSelectionButton.ColorChangeHandler callback)
    {
    	m_colorSelectionButton.setColorChangeHandler(callback);
    }

    public void setTransferDirectionChangedCallback(TransferDirectionButton.StateChangeHandler callback)
    {
    	m_transferDirectionButton.setStateChangedHandler(callback);
    }

    public void setRedstoneChangedCallback(RedstoneButton.StateChangeHandler callback)
    {
    	m_redstoneButton.setStateChangedHandler(callback);
    }
    
    public void setFilterOpenedCallback(IClickable callback)
    {
    	m_filterSettingsButton.setClickCallback(callback);
    }
    
    public void setPriorityOpenedCallback(IClickable callback)
    {
    	m_priorityButton.setClickCallback(callback);
    }

    public void setColor(Color color)
    {
    	m_colorSelectionButton.setColor(color);
    }

    public Color getColor()
    {
    	return m_colorSelectionButton.getColor();
    }

    public void setTransferDirection(TransferDirection dir)
    {
    	m_transferDirectionButton.setState(dir);
    }

    public TransferDirection getTransferDirection()
    {
        return m_transferDirectionButton.getState();
    }

    public void setRedstoneControl(RedstoneControl rc)
    {
    	m_redstoneButton.setState(rc);
    }

    public RedstoneControl getRedstoneControl()
    {
        return m_redstoneButton.getState();
    }
    
    public void setFilterSettingsButtonVisible(boolean visible)
    {
    	m_filterSettingsButton.visible = visible;
    }

    protected GuiTexture m_speedUpgradeSlotTexture;
    protected GuiTexture m_stackUpgradeSlotTexture;
    protected GuiTexture m_filterUpgradeSlotTexture;
    protected ColorSelectionButton m_colorSelectionButton;
    protected TransferDirectionButton m_transferDirectionButton;
    protected RedstoneButton m_redstoneButton;
    protected GuiTextureButton m_priorityButton;
    protected GuiSettingsButton m_filterSettingsButton;

    public static final int SPEED_UPGRADE_SLOT_X = 144;
    public static final int SPEED_UPGRADE_SLOT_Y = 0;

    public static final int STACK_UPGRADE_SLOT_X = SPEED_UPGRADE_SLOT_X;
    public static final int STACK_UPGRADE_SLOT_Y = SPEED_UPGRADE_SLOT_Y + 18;

    public static final int FILTER_UPGRADE_SLOT_X = 89;
    public static final int FILTER_UPGRADE_SLOT_Y = 36;
    
    protected static final ResourceLocation SPEED_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/speed_upgrade_slot.png");

    protected static final ResourceLocation STACK_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/stack_upgrade_slot.png");

    protected static final ResourceLocation ITEM_FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/item_filter_upgrade_slot.png");

    protected static final ResourceLocation FLUID_FILTER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/fluid_filter_upgrade_slot.png");

    protected static final ResourceLocation PRIORITY_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_button.png");
}
