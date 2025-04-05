package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.BigItemStack;
import swiftmod.common.MouseButton;
import swiftmod.common.Notification;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;

@OnlyIn(Dist.CLIENT)
public class BasicItemFilterWidget extends GuiWidget
{
    @FunctionalInterface
    public interface FilterChangedCallback
    {
        void onFilterChanged(int slot, ItemStack itemStack, int quantity);
    }

    public BasicItemFilterWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public BasicItemFilterWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, 162, 54, BACKGROUND_TEXTURE);
    }

    public BasicItemFilterWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height, ResourceLocation backgroundTexture)
    {
        super(screen, x, y, width, height, Component.empty());

        m_backgroundTexture = new GuiTexture(screen, 0, 0, width, height, backgroundTexture);
        addChild(m_backgroundTexture);

        // Place buttons in front of the background.
        final double foregroundZ = 1.0;

        m_whiteBlackListButton = new WhiteBlackListButton(screen, GUI_WHITELIST_X, GUI_WHITELIST_Y, null);
        m_whiteBlackListButton.setZ(foregroundZ);
        addChild(m_whiteBlackListButton);

        m_matchCountButton = new MatchCountButton(screen, GUI_MATCH_COUNT_X, GUI_MATCH_COUNT_Y, this::onMatchCountChanged);
        m_matchCountButton.setZ(foregroundZ);
        addChild(m_matchCountButton);

        m_matchDamageButton = new MatchDamageButton(screen, GUI_MATCH_DAMAGE_X, GUI_MATCH_DAMAGE_Y, null);
        m_matchDamageButton.setZ(foregroundZ);
        addChild(m_matchDamageButton);

        m_matchModButton = new MatchModButton(screen, GUI_MATCH_MOD_X, GUI_MATCH_MOD_Y, null);
        m_matchModButton.setZ(foregroundZ);
        addChild(m_matchModButton);

        m_matchNBTButton = new MatchNBTButton(screen, GUI_MATCH_NBT_X, GUI_MATCH_NBT_Y, null);
        m_matchNBTButton.setZ(foregroundZ);
        addChild(m_matchNBTButton);

        m_matchOreDictionaryButton = new MatchOreDictionaryButton(screen, GUI_MATCH_ORE_DICTIONARY_X,
                GUI_MATCH_ORE_DICTIONARY_Y, null);
        m_matchOreDictionaryButton.setZ(foregroundZ);
        addChild(m_matchOreDictionaryButton);

        m_filterSlots = new ArrayList<GhostItemSlot>();

        GuiDeleteButton deleteButton = new GuiDeleteButton(screen, GUI_DELETE_X, GUI_DELETE_Y, this::onDeleteButtonPressed);
        deleteButton.setTooltip(Component.literal("Remove all filters"));
        addChild(deleteButton);
        
        GuiTexture infoTexture = new GuiTexture(screen, GUI_INFO_X, GUI_INFO_Y, 16, 16, INFO_TEXTURE);
        addChild(infoTexture);
        
        GuiTooltip infoTooltip = new GuiTooltip(screen, GUI_INFO_X, GUI_INFO_Y, 16, 16);
        List<Component> tooltip = new ArrayList<Component>();
        tooltip.add(Component.literal("Left click on a slot to add or increment a filter."));
        tooltip.add(Component.literal("Right click on a slot to remove or decrement a filter."));
        tooltip.add(Component.literal("Shift + click to add/remove one stack."));
        tooltip.add(Component.literal("Ctrl + click to add/remove one item."));
        tooltip.add(Component.literal("Max quantity: " + MAX_SLOT_QUANTITY + " items"));
        infoTooltip.setText(tooltip);
        infoTooltip.setZ(1000.0f);
        addChild(infoTooltip);

        m_matchCountChangedCallback = null;
        m_filterChangedCallback = null;
        m_deleteButtonCallback = null;
    }

    public void setBackgroundTexture(ResourceLocation backgroundTexture, int width, int height)
    {
        m_backgroundTexture.setTexture(backgroundTexture);
        m_backgroundTexture.setWidth(width);
        m_backgroundTexture.setHeight(width);
    }

    public void setWhiteBlackListChangedCallback(WhiteBlackListButton.StateChangeHandler callback)
    {
        m_whiteBlackListButton.setStateChangedHandler(callback);
    }

    public void setMatchCountChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchCountChangedCallback = callback;
    }

    public void setMatchDamageChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchDamageButton.setStateChangedHandler(callback);
    }

    public void setMatchModChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchModButton.setStateChangedHandler(callback);
    }

    public void setMatchNBTChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchNBTButton.setStateChangedHandler(callback);
    }

    public void setMatchOreDictionaryChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchOreDictionaryButton.setStateChangedHandler(callback);
    }

    public void setDeleteFilterCallback(Notification callback)
    {
        m_deleteButtonCallback = callback;
    }

    public void setFilterChangedCallback(FilterChangedCallback callback)
    {
        m_filterChangedCallback = callback;
    }

    public void setWhiteBlackListState(WhiteListState state)
    {
        m_whiteBlackListButton.setState(state);
    }
    
    public WhiteListState getWhiteBlackListState()
    {
        return m_whiteBlackListButton.getState();
    }

    public void setMatchCount(boolean match)
    {
        m_matchCountButton.setState(match);

        for (int i = 0; i < m_filterSlots.size(); ++i)
            m_filterSlots.get(i).showQuantity(match);
    }
    
    public boolean getMatchCount()
    {
        return m_matchCountButton.getState();
    }

    public void setMatchDamage(boolean match)
    {
        m_matchDamageButton.setState(match);
    }
    
    public boolean getMatchDamage()
    {
        return m_matchDamageButton.getState();
    }

    public void setMatchMod(boolean match)
    {
        m_matchModButton.setState(match);
    }
    
    public boolean getMatchMod()
    {
        return m_matchModButton.getState();
    }

    public void setMatchNBT(boolean match)
    {
        m_matchNBTButton.setState(match);
    }
    
    public boolean getMatchNBT()
    {
        return m_matchNBTButton.getState();
    }

    public void setMatchOreDictionary(boolean match)
    {
        m_matchOreDictionaryButton.setState(match);
    }
    
    public boolean getMatchOreDictionary()
    {
        return m_matchOreDictionaryButton.getState();
    }

    public void setFilters(List<BigItemStack> filters)
    {
        int numFilterSlots = filters.size();
        int filterColumns = numFilterSlots > FILTER_SLOTS_PER_ROW ? FILTER_SLOTS_PER_ROW : numFilterSlots;
        int filterRows = (numFilterSlots + FILTER_SLOTS_PER_ROW) / FILTER_SLOTS_PER_ROW;

        if (filters.size() == m_filterSlots.size())
        {
            for (int i = 0; i < m_filterSlots.size(); ++i)
            {
                GhostItemSlot slot = m_filterSlots.get(i);
                slot.setItemStack(filters.get(i).getItemStack());
                slot.setQuantity(filters.get(i).getCount());
            }
        }
        else
        {
            for (int i = 0; i < m_filterSlots.size(); ++i)
                removeChild(m_filterSlots.get(i));

            m_filterSlots.clear();

            for (int i = 0; i < filterRows; ++i)
            {
                for (int j = 0; j < filterColumns; ++j)
                {
                    int slot = i * filterColumns + j;
                    if (slot >= numFilterSlots)
                        break;
    
                    int x = GUI_FILTER_X + SwiftGui.INVENTORY_SLOT_WIDTH * j;
                    int y = GUI_FILTER_Y + SwiftGui.INVENTORY_SLOT_HEIGHT * i;
                    GhostItemSlot filterSlot = new GhostItemSlot(getScreen(), x + 1, y + 1);
                    filterSlot.setIsFilterValidCallback(this::isFilterValid);
                    filterSlot.setFilterUpdatedCallback(this::onFilterChanged);
                    filterSlot.setZ(1.0);
                    filterSlot.setSlot(slot);
                    filterSlot.setMaxQuantity(MAX_SLOT_QUANTITY);
                    filterSlot.showQuantity(m_matchCountButton.getState());
                    if (i < filters.size())
                    {
                        filterSlot.setItemStack(filters.get(slot).getItemStack());
                        filterSlot.setQuantity(filters.get(slot).getCount());
                    }
                    m_filterSlots.add(filterSlot);
                    addChild(filterSlot);
                }
            }
        }
    }
    
    protected boolean isFilterValid(int slot, ItemStack stack)
    {
        // Make sure the same item stack doesn't already exist in another slot.
        for (int i = 0; i < m_filterSlots.size(); ++i)
        {
            if (i == slot)
                continue;

            ItemStack s = m_filterSlots.get(i).getItemStack();
            if (m_matchNBTButton.getState() || m_matchDamageButton.getState())
            {
            	// When matching any sort of NBT, allow multiple items of the same
            	// type but with different NBT; this allows sorting e.g. items
            	// of different durabilities.
            	
            	// TODO: Maybe this? ItemStack.isSameItemSameComponents(s, stack)
	            if (ItemStack.isSameItemSameComponents(s, stack))
	            {
	                // Item already exists; abort.
	                return false;
	            }
            }
            else
            {
            	// When ignoring NBT, check item type only, since durability
            	// and such doesn't matter.
                if (stack.getItem() == s.getItem())
	            {
	                // Item already exists; abort.
	                return false;
	            }
            }
        }

        return true;
    }

    protected void onFilterChanged(int slot, ItemStack stack, int quantity)
    {
        if (m_filterChangedCallback != null)
            m_filterChangedCallback.onFilterChanged(slot, stack, quantity);
    }

    protected void onMatchCountChanged(GuiBooleanStateButton button, boolean matchCount)
    {
        for (int i = 0; i < m_filterSlots.size(); ++i)
            m_filterSlots.get(i).showQuantity(matchCount);

        if (m_matchCountChangedCallback != null)
            m_matchCountChangedCallback.onStateChanged(button, matchCount);
    }
    
    protected void onDeleteButtonPressed(GuiWidget widget, MouseButton mouseButton)
    {
        for (int i = 0; i < m_filterSlots.size(); ++i)
        {
            GhostItemSlot slot = m_filterSlots.get(i);
            slot.setItemStack(ItemStack.EMPTY);
            slot.setQuantity(0);
        }

        if (m_deleteButtonCallback != null)
            m_deleteButtonCallback.invoke();
    }

    protected static final int GUI_WHITELIST_X = 1;
    protected static final int GUI_WHITELIST_Y = 1;

    protected static final int GUI_MATCH_COUNT_X = GUI_WHITELIST_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_COUNT_Y = GUI_WHITELIST_Y;

    protected static final int GUI_MATCH_DAMAGE_X = GUI_MATCH_COUNT_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_DAMAGE_Y = GUI_MATCH_COUNT_Y;

    protected static final int GUI_MATCH_MOD_X = GUI_MATCH_DAMAGE_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_MOD_Y = GUI_MATCH_DAMAGE_Y;

    protected static final int GUI_MATCH_NBT_X = GUI_MATCH_MOD_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_NBT_Y = GUI_MATCH_MOD_Y;

    protected static final int GUI_MATCH_ORE_DICTIONARY_X = GUI_MATCH_NBT_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_ORE_DICTIONARY_Y = GUI_MATCH_NBT_Y;

    protected static final int GUI_INFO_X = 145;
    protected static final int GUI_INFO_Y = GUI_MATCH_ORE_DICTIONARY_Y;

    protected static final int GUI_DELETE_X = GUI_INFO_X - SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_DELETE_Y = GUI_INFO_Y;

    protected static final int GUI_FILTER_X = 0;
    protected static final int GUI_FILTER_Y = 18;
    
    protected static final int FILTER_SLOTS_PER_ROW = 9;

    protected static final int MAX_SLOT_QUANTITY = 99999;

    protected static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/basic_filter_upgrade.png");

    protected static final ResourceLocation INFO_TEXTURE = ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME,
            "textures/gui/info.png");

    protected GuiTexture m_backgroundTexture;
    protected WhiteBlackListButton m_whiteBlackListButton;
    protected MatchCountButton m_matchCountButton;
    protected MatchDamageButton m_matchDamageButton;
    protected MatchModButton m_matchModButton;
    protected MatchNBTButton m_matchNBTButton;
    protected MatchOreDictionaryButton m_matchOreDictionaryButton;
    protected ArrayList<GhostItemSlot> m_filterSlots;
    protected GuiBooleanStateButton.StateChangeHandler m_matchCountChangedCallback;
    protected FilterChangedCallback m_filterChangedCallback;
    protected Notification m_deleteButtonCallback;
}
