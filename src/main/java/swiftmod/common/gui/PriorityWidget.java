package swiftmod.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class PriorityWidget extends GuiWidget
{
    @FunctionalInterface
    public interface SaveHandler
    {
        public void onSave(PriorityWidget widget, int value);
    }
    
    public PriorityWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0);
    }

    public PriorityWidget(GuiContainerScreen<?> screen, int value)
    {
        super(screen, DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.empty());
        setMinimum(Integer.MIN_VALUE);
        setMaximum(Integer.MAX_VALUE);
        m_saveHandler = null;
        
        int buttonCombinedWidth = ONE_BUTTON_WIDTH + TEN_BUTTON_WIDTH + HUNDRED_BUTTON_WIDTH + THOUSAND_BUTTON_WIDTH + (3 * BUTTON_MARGIN);
        int oneButtonX = (width() - buttonCombinedWidth) / 2;
        int tenButtonX = oneButtonX + ONE_BUTTON_WIDTH + BUTTON_MARGIN;
        int hundredButtonX = tenButtonX + TEN_BUTTON_WIDTH + BUTTON_MARGIN;
        int thousandButtonX = hundredButtonX + HUNDRED_BUTTON_WIDTH + BUTTON_MARGIN;
        
        m_label = new GuiLabel(screen, 0, 2, width(), 30, Component.translatable("swift.text.priority"));
        m_label.setAlignment(GuiVerticalAlignment.Top, GuiHorizontalAlignment.Center);
        
        m_plusOneButton = new GuiTextButton(screen, oneButtonX, BUTTON_TOP_ROW_Y, ONE_BUTTON_WIDTH, BUTTON_HEIGHT,
        		ONE_BUTTON_TEXTURE, ONE_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("+1"), this::addOne);
        m_plusTenButton = new GuiTextButton(screen, tenButtonX, BUTTON_TOP_ROW_Y, TEN_BUTTON_WIDTH, BUTTON_HEIGHT,
        		TEN_BUTTON_TEXTURE, TEN_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("+10"), this::addTen);
        m_plusHundredButton = new GuiTextButton(screen, hundredButtonX, BUTTON_TOP_ROW_Y, HUNDRED_BUTTON_WIDTH, BUTTON_HEIGHT,
        		HUNDRED_BUTTON_TEXTURE, HUNDRED_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("+100"), this::addHundred);
        m_plusThousandButton = new GuiTextButton(screen, thousandButtonX, BUTTON_TOP_ROW_Y, THOUSAND_BUTTON_WIDTH, BUTTON_HEIGHT,
        		THOUSAND_BUTTON_TEXTURE, THOUSAND_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("+1000"), this::addThousand);
        
        m_minusOneButton = new GuiTextButton(screen, oneButtonX, BUTTON_BOTTOM_ROW_Y, ONE_BUTTON_WIDTH, BUTTON_HEIGHT,
        		ONE_BUTTON_TEXTURE, ONE_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("-1"), this::subtractOne);
        m_minusTenButton = new GuiTextButton(screen, tenButtonX, BUTTON_BOTTOM_ROW_Y, TEN_BUTTON_WIDTH, BUTTON_HEIGHT,
        		TEN_BUTTON_TEXTURE, TEN_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("-10"), this::subtractTen);
        m_minusHundredButton = new GuiTextButton(screen, hundredButtonX, BUTTON_BOTTOM_ROW_Y, HUNDRED_BUTTON_WIDTH, BUTTON_HEIGHT,
        		HUNDRED_BUTTON_TEXTURE, HUNDRED_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("-100"), this::subtractHundred);
        m_minusThousandButton = new GuiTextButton(screen, thousandButtonX, BUTTON_BOTTOM_ROW_Y, THOUSAND_BUTTON_WIDTH, BUTTON_HEIGHT,
        		THOUSAND_BUTTON_TEXTURE, THOUSAND_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("-1000"), this::subtractThousand);
        
        m_textField = new GuiTextField(screen,
        		(width() - TEXT_FIELD_WIDTH) / 2, TEXT_FIELD_Y,
        		TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT,
        		Component.literal(Integer.toString(value)));
        m_textField.setTextColor(-1);
        m_textField.setDisabledTextColour(-1);
        m_textField.setEnableBackgroundDrawing(false);
        m_textField.setMaxStringLength(11);
        m_textField.setScale(1.15f);
        m_textField.setValidator(this::canSetText);
        m_textField.setResponder(this::onTextChanged);
        
        int saveRevertCombinedWidth = SAVE_BUTTON_WIDTH + REVERT_BUTTON_WIDTH + BUTTON_MARGIN;
        int saveButtonX = (width() - saveRevertCombinedWidth) / 2;
        int revertButtonX = saveButtonX + SAVE_BUTTON_WIDTH + BUTTON_MARGIN;
        
        m_saveButton = new GuiTextButton(screen, saveButtonX, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, BUTTON_HEIGHT,
        		SAVE_BUTTON_TEXTURE, SAVE_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("Save"), this::onSave);
        m_saveButton.setBackgroundInactiveTexture(SAVE_BUTTON_INACTIVE_TEXTURE);
        m_revertButton = new GuiTextButton(screen, revertButtonX, REVERT_BUTTON_Y, REVERT_BUTTON_WIDTH, BUTTON_HEIGHT,
        		REVERT_BUTTON_TEXTURE, REVERT_BUTTON_HIGHLIGHTED_TEXTURE, Component.literal("Revert"), this::onRevert);
        m_revertButton.setBackgroundInactiveTexture(REVERT_BUTTON_INACTIVE_TEXTURE);
        
        setValue(value);
        
        addChild(m_label);
        addChild(m_plusOneButton);
        addChild(m_plusTenButton);
        addChild(m_plusHundredButton);
        addChild(m_plusThousandButton);
        addChild(m_minusOneButton);
        addChild(m_minusTenButton);
        addChild(m_minusHundredButton);
        addChild(m_minusThousandButton);
        addChild(m_textField);
        addChild(m_saveButton);
        addChild(m_revertButton);
    }
    
    public void setSaveHandler(SaveHandler handler)
    {
    	m_saveHandler = handler;
    }
    
    public void requestTextFieldFocus()
    {
    	m_textField.requestFocus();
    }
    
    public void setMinimum(int minimum)
    {
    	m_minimum = minimum;
    }
    
    public int getMinimum()
    {
    	return m_minimum;
    }
    
    public void setMaximum(int maximum)
    {
    	m_maximum = maximum;
    }
    
    public int getMaximum()
    {
    	return m_maximum;
    }
    
    public void setValue(int value)
    {
    	setValue(value, false);
    }
    
    public void setValue(int value, boolean setAsBaseValue)
    {
    	int valueClamped;
    	if (value <= m_minimum)
    		valueClamped = m_minimum;
    	else if (value >= m_maximum)
    		valueClamped = m_maximum;
    	else
    		valueClamped = value;
    	
    	if (setAsBaseValue)
    		m_baseValue = valueClamped;
    	
    	m_textField.setText(Integer.toString(valueClamped));
    }
    
    public int getValue()
    {
    	String text = m_textField.getText().getString();
    	if (text.isEmpty() || text.equals("-"))
    		return 0;
    	else
    		return Integer.parseInt(m_textField.getText().getString());
    }
    
    public void addOne(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(1);
    }
    
    public void addTen(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(10);
    }
    
    public void addHundred(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(100);
    }
    
    public void addThousand(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(1000);
    }
    
    public void subtractOne(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(-1);
    }
    
    public void subtractTen(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(-10);
    }
    
    public void subtractHundred(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(-100);
    }
    
    public void subtractThousand(GuiWidget widget, MouseButton mouseButton)
    {
    	addWorker(-1000);
    }
    
    public void addWorker(int value)
    {
    	int v = getValue();
    	v += value;
    	setValue(v);
    }
    
    public void onSave(GuiWidget widget, MouseButton mouseButton)
    {
    	int value = getValue();
    	m_baseValue = value;
    	// onTextChanged() is not called in this case so we must set .active manually.
    	m_saveButton.active = false;
    	m_revertButton.active = false;
    	if (m_saveHandler != null)
    		m_saveHandler.onSave(this, value);
    }
    
    public void onRevert(GuiWidget widget, MouseButton mouseButton)
    {
    	setValue(m_baseValue);
    }
    
    private boolean canSetText(String text)
    {
    	// Allows clearing the entire field and entering a negative number manually.
    	if (text.isEmpty() || text.equals("-"))
    		return true;
    	try
    	{
    		int x = Integer.parseInt(text);
    		return x <= m_maximum && x >= m_minimum;
    	}
    	catch (NumberFormatException ex)
    	{
    		return false;
    	}
    }
    
    private void onTextChanged(String text)
    {
    	// Don't allow a "number" to be saved if one isn't fully entered.
    	if (text.isEmpty() || text.equals("-"))
    	{
        	m_saveButton.active = false;
        	m_revertButton.active = false;
    	}
    	else
    	{
	    	int value = getValue();
	    	boolean changed = value != m_baseValue;
	    	m_saveButton.active = changed;
	    	m_revertButton.active = changed;
    	}
    }
    
    protected GuiLabel m_label;
    
    protected GuiTextButton m_plusOneButton;
    protected GuiTextButton m_plusTenButton;
    protected GuiTextButton m_plusHundredButton;
    protected GuiTextButton m_plusThousandButton;

    protected GuiTextButton m_minusOneButton;
    protected GuiTextButton m_minusTenButton;
    protected GuiTextButton m_minusHundredButton;
    protected GuiTextButton m_minusThousandButton;

    protected GuiTextButton m_saveButton;
    protected GuiTextButton m_revertButton;
    
    protected GuiTextField m_textField;
    
    protected SaveHandler m_saveHandler;
    
    protected int m_minimum;
    protected int m_maximum;
    protected int m_baseValue;
    
    public static final int DEFAULT_WIDTH = 162;
    public static final int DEFAULT_HEIGHT = 160;
    
    public static final int TEXT_FIELD_WIDTH = 100;
    public static final int TEXT_FIELD_HEIGHT = 20;

    public static final int ONE_BUTTON_WIDTH = 22;
    public static final int TEN_BUTTON_WIDTH = 28;
    public static final int HUNDRED_BUTTON_WIDTH = 34;
    public static final int THOUSAND_BUTTON_WIDTH = 40;

    public static final int BUTTON_HEIGHT = 20;

    public static final int BUTTON_MARGIN = 6;

    public static final int ROW_MARGIN = 9;
    
    public static final int BUTTON_TOP_ROW_Y = 22;
    public static final int TEXT_FIELD_Y = BUTTON_TOP_ROW_Y + BUTTON_HEIGHT + ROW_MARGIN;
    public static final int BUTTON_BOTTOM_ROW_Y = TEXT_FIELD_Y + TEXT_FIELD_HEIGHT + ROW_MARGIN;
    
    public static final int SAVE_BUTTON_WIDTH = 34;
    public static final int SAVE_BUTTON_Y = BUTTON_BOTTOM_ROW_Y + BUTTON_HEIGHT + ROW_MARGIN;
    
    public static final int REVERT_BUTTON_WIDTH = 46;
    public static final int REVERT_BUTTON_Y = SAVE_BUTTON_Y;

    protected static final ResourceLocation ONE_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/one_button.png");
    protected static final ResourceLocation ONE_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/one_button_highlighted.png");

    protected static final ResourceLocation TEN_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/ten_button.png");
    protected static final ResourceLocation TEN_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/ten_button_highlighted.png");

    protected static final ResourceLocation HUNDRED_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/hundred_button.png");
    protected static final ResourceLocation HUNDRED_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/hundred_button_highlighted.png");

    protected static final ResourceLocation THOUSAND_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/thousand_button.png");
    protected static final ResourceLocation THOUSAND_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/thousand_button_highlighted.png");

    protected static final ResourceLocation SAVE_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/save_button.png");
    protected static final ResourceLocation SAVE_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/save_button_highlighted.png");
    protected static final ResourceLocation SAVE_BUTTON_INACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/save_button_inactive.png");

    protected static final ResourceLocation REVERT_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/revert_button.png");
    protected static final ResourceLocation REVERT_BUTTON_HIGHLIGHTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/revert_button_highlighted.png");
    protected static final ResourceLocation REVERT_BUTTON_INACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/priority_panel/revert_button_inactive.png");
}
