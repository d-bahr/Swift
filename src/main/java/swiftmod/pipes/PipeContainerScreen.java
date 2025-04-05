package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.Color;
import swiftmod.common.MouseButton;
import swiftmod.common.RedstoneControl;
import swiftmod.common.SlotBase;
import swiftmod.common.Swift;
import swiftmod.common.SwiftItems;
import swiftmod.common.TransferDirection;
import swiftmod.common.gui.BasicFluidFilterWidget;
import swiftmod.common.gui.BasicItemFilterWidget;
import swiftmod.common.gui.ColorSelectionButton;
import swiftmod.common.gui.DirectionSelectionWidget;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.GuiLabeledPanel;
import swiftmod.common.gui.GuiPanel;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.GuiTexture;
import swiftmod.common.gui.GuiWidget;
import swiftmod.common.gui.PriorityWidget;
import swiftmod.common.gui.RedstoneButton;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.gui.SwiftGui;
import swiftmod.common.gui.TransferDirectionButton;
import swiftmod.common.upgrades.UpgradeType;

@OnlyIn(Dist.CLIENT)
public abstract class PipeContainerScreen<T extends PipeContainer> extends GuiContainerScreen<T>
{
    public PipeContainerScreen(T c, Inventory inv, Component title, SideIOConfigurationWidget.FilterType filterType)
    {
        super(c, inv, title, 176, 179, DEFAULT_BACKGROUND_TEXTURE);

        m_init = true;
        m_filterType = filterType;
        m_selectedDirection = Direction.NORTH;
        
        menu.setFilterSlotChangedCallback(this::onFilterSlotChanged);

        m_panelStack = new GuiPanelStack(this, 0, 0, width(), height());
        m_panelStack.setStackChangedCallback(this::onPageChanged);
        m_panelStack.setDefaultBackButton(5, 5);

        m_basePanel = new GuiLabeledPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, 90, title);
        m_directionalConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, 90);
        m_basicFilterConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, 90);
        m_wildcardFilterConfigPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, PRIORITY_HEIGHT - BASE_PANEL_Y);
        m_priorityPanel = new GuiPanel(this, BASE_PANEL_X, BASE_PANEL_Y, width() - BASE_PANEL_X * 2, PRIORITY_HEIGHT - (BASE_PANEL_Y * 2));
        m_chunkLoaderSlotTexture = new GuiTexture(this, CHUNK_LOADER_UPGRADE_SLOT_X, CHUNK_LOADER_UPGRADE_SLOT_Y,
        		SwiftGui.INVENTORY_SLOT_WIDTH, SwiftGui.INVENTORY_SLOT_HEIGHT, CHUNK_LOADER_UPGRADE_SLOT_TEXTURE);
        m_chunkLoaderSlotTexture.setZ(1);

        m_directionSelectionWidget = new DirectionSelectionWidget(this, 55, 18);
        m_directionSelectionWidget.setDirectionButtonPressCallback(this::onDirectionButtonPressed);

    	m_sideIOConfigurationWidget = new SideIOConfigurationWidget(this, m_filterType);
    	m_sideIOConfigurationWidget.setTransferDirectionChangedCallback(this::onTransferDirectionChanged);
    	m_sideIOConfigurationWidget.setRedstoneChangedCallback(this::onRedstoneControlChanged);
    	m_sideIOConfigurationWidget.setColorChangedCallback(this::onColorChanged);
    	m_sideIOConfigurationWidget.setFilterOpenedCallback(this::openFilterSettings);
    	m_sideIOConfigurationWidget.setPriorityOpenedCallback(this::openPrioritySettings);

        initBasePanel(title);
        initDirectionalConfigPanel();
        initPriorityPanel();

        m_panelStack.push(m_basePanel);

        // UI is offset by one pixel for some dumb reason.
        m_playerInventory.setX(PLAYER_INVENTORY_OFFSET_X - 1);
        m_playerInventory.setY(PLAYER_INVENTORY_OFFSET_Y - 1);

        // TODO: Really need to fix this up in GuiContainerScreen so it doesn't need to be done here.
        m_playerInventoryLabel.setX(m_playerInventory.getX() + 1);
        m_playerInventoryLabel.setY(m_playerInventory.top() - m_playerInventoryLabel.height() - 2);
    }

    protected void initBasePanel(Component title)
    {
        m_basePanel.addChild(m_directionSelectionWidget);
    }

    protected void initDirectionalConfigPanel()
    {
        m_directionalConfigPanel.addChild(m_sideIOConfigurationWidget);
    }
    
    protected void initPriorityPanel()
    {
    	m_priorityWidget = new PriorityWidget(this);
    	m_priorityWidget.setSaveHandler(this::savePriority);
    	m_priorityPanel.addChild(m_priorityWidget);
    }

    protected abstract void showBasicFilterWidget();
    
    protected abstract void showWildcardFilterWidget();

    protected void openFilterSettings(GuiWidget widget, MouseButton button)
    {
    	if (button == MouseButton.Left)
    	{
	        if (getMenu().containsUpgrade(getCurrentIndex(), UpgradeType.WildcardFilterUpgrade))
	        {
	        	showWildcardFilterWidget();
	        }
	        else
	        {
	        	showBasicFilterWidget();
	        }
    	}
    }

    protected void openPrioritySettings(GuiWidget widget, MouseButton button)
    {
    	if (button == MouseButton.Left)
    	{
    		int priority = getMenu().getCache().getPriority(getCurrentIndex());
    		m_priorityWidget.setValue(priority, true);
            m_panelStack.push(m_priorityPanel);
            m_priorityWidget.requestTextFieldFocus();
    	}
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        add(m_panelStack);
        add(m_chunkLoaderSlotTexture);

        for (Direction dir : Direction.values())
        	setDirectionWidget(dir);
    }
    
    private void setDirectionWidget(Direction dir)
    {
    	ItemStack stack = menu.getNeighbor(dir);
        m_directionSelectionWidget.setItemForDirection(dir, stack);
        if (stack != null)
        {
	    	Item item = stack.getItem();
	        m_directionSelectionWidget.setDirectionEnabled(dir, item != SwiftItems.s_wormholeItem.get());
        }
    }

    @Override
    public void lateInit()
    {
        if (m_init)
        {
            Direction dir = menu.getStartingDirection();
            if (dir != null)
                onDirectionButtonPressed(dir);
            m_init = false;
        }
    }

    protected void onPageChanged(GuiPanelStack stack)
    {
        if (stack.topPanel() == m_basePanel)
        {
            menu.enableBaseUpgradeSlots(true);
            menu.disableSideUpgradeSlots();
            m_chunkLoaderSlotTexture.visible = true;
            m_backgroundTexture.setHeight(DEFAULT_HEIGHT);
            m_backgroundTexture.setTexture(DEFAULT_BACKGROUND_TEXTURE);
            showPlayerInventory(true);
            setHeight(DEFAULT_HEIGHT);
        }
        else if (stack.topPanel() == m_directionalConfigPanel)
        {
            menu.enableBaseUpgradeSlots(true);
            menu.enableSideUpgradeSlots(getCurrentIndex(), true);
            m_chunkLoaderSlotTexture.visible = true;
            m_backgroundTexture.setHeight(DEFAULT_HEIGHT);
            m_backgroundTexture.setTexture(DEFAULT_BACKGROUND_TEXTURE);
            showPlayerInventory(true);
            setHeight(DEFAULT_HEIGHT);
        }
        else if (stack.topPanel() == m_basicFilterConfigPanel)
        {
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            m_chunkLoaderSlotTexture.visible = false;
            m_backgroundTexture.setHeight(DEFAULT_HEIGHT);
            m_backgroundTexture.setTexture(DEFAULT_BACKGROUND_TEXTURE);
            showPlayerInventory(true);
            setHeight(DEFAULT_HEIGHT);
        }
        else if (stack.topPanel() == m_wildcardFilterConfigPanel)
        {
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            m_chunkLoaderSlotTexture.visible = false;
            m_backgroundTexture.setHeight(PRIORITY_HEIGHT);
            m_backgroundTexture.setTexture(PRIORITY_BACKGROUND_TEXTURE);
            showPlayerInventory(false);
            setHeight(PRIORITY_HEIGHT);
        }
        else if (stack.topPanel() == m_priorityPanel)
        {
            menu.enableBaseUpgradeSlots(false);
            menu.disableSideUpgradeSlots();
            m_chunkLoaderSlotTexture.visible = false;
            m_backgroundTexture.setHeight(PRIORITY_HEIGHT);
            m_backgroundTexture.setTexture(PRIORITY_BACKGROUND_TEXTURE);
            showPlayerInventory(false);
            setHeight(PRIORITY_HEIGHT);
        }
    }

    protected void onDirectionButtonPressed(Direction direction)
    {
		Direction oldDirection = m_selectedDirection;
        m_selectedDirection = direction;
        
        int index = getCurrentIndex();
    	
        RedstoneControl redstoneControl = menu.getCache().getRedstoneControl(index);
        m_sideIOConfigurationWidget.setRedstoneControl(redstoneControl);

        TransferDirection transferDirection = menu.getCache().getTransferDirection(index);
        m_sideIOConfigurationWidget.setTransferDirection(transferDirection);

        Color color = menu.getCache().getColor(index);
        m_sideIOConfigurationWidget.setColor(color);
        
        m_sideIOConfigurationWidget.setFilterSettingsButtonVisible(
        		menu.containsUpgradeInSlot(index, UpgradeType.WildcardFilterUpgrade));

        m_panelStack.push(m_directionalConfigPanel);
        onDirectionChanged(oldDirection, direction);
    }
    
    protected void onDirectionChanged(Direction oldDirection, Direction newDirection)
    {
    }

    protected void onRedstoneControlChanged(RedstoneButton button, RedstoneControl newRedstoneControl)
    {
        menu.setRedstoneControl(getCurrentIndex(), newRedstoneControl);
    }

    protected void onTransferDirectionChanged(TransferDirectionButton button, TransferDirection newTransferDirection)
    {
        menu.setTransferDirection(getCurrentIndex(), newTransferDirection);
    }

    protected void onColorChanged(ColorSelectionButton button, Color newColor)
    {
        menu.setColor(getCurrentIndex(), newColor);
    }

    protected void onFilterSlotChanged(SlotBase slot, int transferIndex)
    {
        if (transferIndex == getCurrentIndex())
            m_sideIOConfigurationWidget.setFilterSettingsButtonVisible(slot.hasItem());
    }
    
    protected void savePriority(PriorityWidget widget, int value)
    {
        menu.setPriority(getCurrentIndex(), value);
    }
    
    protected void revertPriority(PriorityWidget widget)
    {
    	widget.setValue(menu.getPriority(getCurrentIndex()));
    }
    
    protected abstract int getCurrentIndex();

    protected GuiPanelStack m_panelStack;
    protected GuiPanel m_basePanel;
    protected GuiPanel m_basicFilterConfigPanel;
    protected GuiPanel m_wildcardFilterConfigPanel;
    protected GuiPanel m_directionalConfigPanel;
    protected GuiPanel m_priorityPanel;
    protected DirectionSelectionWidget m_directionSelectionWidget;
    protected SideIOConfigurationWidget m_sideIOConfigurationWidget;
    protected BasicItemFilterWidget m_basicItemFilterWidget;
    protected BasicFluidFilterWidget m_basicFluidFilterWidget;
    protected PriorityWidget m_priorityWidget;
    protected GuiTexture m_chunkLoaderSlotTexture;
    protected Direction m_selectedDirection;
    protected boolean m_init;
    protected final SideIOConfigurationWidget.FilterType m_filterType;
    
    public static final int DEFAULT_HEIGHT = 179;
    public static final int PRIORITY_HEIGHT = 146;

    public static final int PLAYER_INVENTORY_OFFSET_X = 8;
    public static final int PLAYER_INVENTORY_OFFSET_Y = 97;

    public static final int BASE_PANEL_X = 7;
    public static final int BASE_PANEL_Y = 8;

    public static final int SIDE_UPGRADE_PANEL_X = BASE_PANEL_X;
    public static final int SIDE_UPGRADE_PANEL_Y = BASE_PANEL_Y;

    /*public static final int SIDE_UPGRADE_PANEL_SLOT_START_X = 0;
    public static final int SIDE_UPGRADE_PANEL_SLOT_START_Y = 44;

    public static final int SIDE_UPGRADE_PANEL_SLOT_WIDTH = 18;
    public static final int SIDE_UPGRADE_PANEL_SLOT_HEIGHT = 18;

    public static final int SIDE_UPGRADE_PANEL_SLOT_OFFSET_X = 54;
    public static final int SIDE_UPGRADE_PANEL_SLOT_OFFSET_Y = 18;*/
    
    public static final int CHUNK_LOADER_UPGRADE_SLOT_X = 151;
    public static final int CHUNK_LOADER_UPGRADE_SLOT_Y = 71;

    protected static final ResourceLocation DEFAULT_BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/pipe.png");

    protected static final ResourceLocation PRIORITY_BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/priority_background.png");

    protected static final ResourceLocation CHUNK_LOADER_UPGRADE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/chunk_loader_upgrade_slot.png");
}
