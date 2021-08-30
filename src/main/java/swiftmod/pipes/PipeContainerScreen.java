package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;
import swiftmod.common.gui.DirectionSelectionWidget;
import swiftmod.common.gui.GuiContainerScreen;
import swiftmod.common.gui.GuiLabeledPanel;
import swiftmod.common.gui.GuiPanel;
import swiftmod.common.gui.GuiPanelStack;
import swiftmod.common.gui.RedstoneButton;
import swiftmod.common.gui.SwiftGui;
import swiftmod.common.gui.TransferDirectionButton;

@OnlyIn(Dist.CLIENT)
public class PipeContainerScreen<T extends PipeContainer> extends GuiContainerScreen<T>
{
    public PipeContainerScreen(T c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title, 176, 179, BACKGROUND_TEXTURE);

        m_selectedDirection = Direction.NORTH;

        m_panelStack = new GuiPanelStack(this, 0, 0, width(), height());
        m_panelStack.setStackChangedCallback(this::onPageChanged);
        m_panelStack.setDefaultBackButton(5, 5);

        m_basePanel = new GuiLabeledPanel(this, BASE_PANEL_X, BASE_PANEL_Y, 120, 90, title);
        m_directionalConfigPanel = new GuiPanel(this, SIDE_UPGRADE_PANEL_X, SIDE_UPGRADE_PANEL_Y, 120, 90);

        initBasePanel(title);
        initDirectionalConfigPanel();

        m_panelStack.push(m_basePanel);

        // UI is offset by one pixel for some dumb reason.
        m_playerInventory.x = PLAYER_INVENTORY_OFFSET_X - 1;
        m_playerInventory.y = PLAYER_INVENTORY_OFFSET_Y - 1;

        // TODO: Really need to fix this up in GuiContainerScreen so it doesn't need to be done here.
        m_playerInventoryLabel.x = m_playerInventory.x + 1;
        m_playerInventoryLabel.y = m_playerInventory.top() - m_playerInventoryLabel.height() - 2;
        
        m_init = true;
    }

    protected void initBasePanel(ITextComponent title)
    {
        m_directonSelectionWidget = new DirectionSelectionWidget(this, 55, 18);
        m_directonSelectionWidget.setDirectionButtonPressCallback(this::onDirectionButtonPressed);

        m_basePanel.addChild(m_directonSelectionWidget);
    }

    protected void initDirectionalConfigPanel()
    {
        m_transferDirectionButton = new TransferDirectionButton(this, 1, 27, SwiftGui.BUTTON_WIDTH,
                SwiftGui.BUTTON_HEIGHT, this::onTransferDirectionChanged);

        m_redstoneButton = new RedstoneButton(this, m_transferDirectionButton.right() + 2,
                m_transferDirectionButton.top(), SwiftGui.BUTTON_WIDTH, SwiftGui.BUTTON_HEIGHT,
                this::onRedstoneControlChanged);

        m_directionalConfigPanel.addChild(m_transferDirectionButton);
        m_directionalConfigPanel.addChild(m_redstoneButton);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();

        RedstoneControl redstoneControl = menu.getCache().getRedstoneControl(m_selectedDirection);
        m_redstoneButton.setState(redstoneControl);

        TransferDirection transferDirection = menu.getCache().getTransferDirection(m_selectedDirection);
        m_transferDirectionButton.setState(transferDirection);

        add(m_panelStack);

        m_directonSelectionWidget.setItemForDirection(Direction.NORTH, menu.getNeighbor(Direction.NORTH));
        m_directonSelectionWidget.setItemForDirection(Direction.SOUTH, menu.getNeighbor(Direction.SOUTH));
        m_directonSelectionWidget.setItemForDirection(Direction.EAST, menu.getNeighbor(Direction.EAST));
        m_directonSelectionWidget.setItemForDirection(Direction.WEST, menu.getNeighbor(Direction.WEST));
        m_directonSelectionWidget.setItemForDirection(Direction.UP, menu.getNeighbor(Direction.UP));
        m_directonSelectionWidget.setItemForDirection(Direction.DOWN, menu.getNeighbor(Direction.DOWN));
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
            showPlayerInventory(true);
        }
        else if (stack.topPanel() == m_directionalConfigPanel)
        {
            menu.enableBaseUpgradeSlots(true);
            menu.enableSideUpgradeSlots(m_selectedDirection, true);
            showPlayerInventory(true);
        }
    }

    private void onDirectionButtonPressed(Direction direction)
    {
        m_selectedDirection = direction;
        m_redstoneButton.setState(menu.getCache().getRedstoneControl(m_selectedDirection));
        m_transferDirectionButton.setState(menu.getCache().getTransferDirection(m_selectedDirection));
        m_panelStack.push(m_directionalConfigPanel);
        onDirectionChanged(direction);
    }
    
    protected void onDirectionChanged(Direction direction)
    {
    }

    protected void onRedstoneControlChanged(RedstoneButton button, RedstoneControl newRedstoneControl)
    {
        // This should really never happen, but just in case...
        if (m_selectedDirection == null)
            return;

        menu.setRedstoneControl(m_selectedDirection, newRedstoneControl);
    }

    protected void onTransferDirectionChanged(TransferDirectionButton button, TransferDirection newTransferDirection)
    {
        // This should really never happen, but just in case...
        if (m_selectedDirection == null)
            return;

        menu.setTransferDirection(m_selectedDirection, newTransferDirection);
    }

    protected GuiPanelStack m_panelStack;
    protected GuiPanel m_basePanel;
    protected GuiPanel m_directionalConfigPanel;
    protected DirectionSelectionWidget m_directonSelectionWidget;
    protected Direction m_selectedDirection;
    protected RedstoneButton m_redstoneButton;
    protected TransferDirectionButton m_transferDirectionButton;
    protected boolean m_init;

    public static final int PLAYER_INVENTORY_OFFSET_X = 8;
    public static final int PLAYER_INVENTORY_OFFSET_Y = 97;

    public static final int BASE_PANEL_X = 7;
    public static final int BASE_PANEL_Y = 8;

    public static final int UPGRADE_PANEL_X = 130;
    public static final int UPGRADE_PANEL_Y = 25;

    public static final int SIDE_UPGRADE_PANEL_X = BASE_PANEL_X;
    public static final int SIDE_UPGRADE_PANEL_Y = BASE_PANEL_Y;

    public static final int SIDE_UPGRADE_PANEL_SLOT_START_X = 0;
    public static final int SIDE_UPGRADE_PANEL_SLOT_START_Y = 44;

    public static final int SIDE_UPGRADE_PANEL_SLOT_WIDTH = 18;
    public static final int SIDE_UPGRADE_PANEL_SLOT_HEIGHT = 18;

    public static final int SIDE_UPGRADE_PANEL_SLOT_OFFSET_X = 54;
    public static final int SIDE_UPGRADE_PANEL_SLOT_OFFSET_Y = 18;

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/pipe.png");
}
