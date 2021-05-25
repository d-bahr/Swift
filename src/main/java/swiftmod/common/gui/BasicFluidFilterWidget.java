package swiftmod.common.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.MouseButton;
import swiftmod.common.Notification;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;

@OnlyIn(Dist.CLIENT)
public class BasicFluidFilterWidget extends GuiWidget
{
    @FunctionalInterface
    public interface FilterChangedCallback
    {
        void onFilterChanged(int slot, FluidStack fluidStack);
    }

    public BasicFluidFilterWidget(GuiContainerScreen<?> screen)
    {
        this(screen, 0, 0);
    }

    public BasicFluidFilterWidget(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, 162, 54, BACKGROUND_TEXTURE);
    }

    public BasicFluidFilterWidget(GuiContainerScreen<?> screen, int x, int y, int width, int height,
            ResourceLocation backgroundTexture)
    {
        super(screen, x, y, width, height, StringTextComponent.EMPTY);

        m_backgroundTexture = new GuiTexture(screen, 0, 0, width, height, backgroundTexture);
        addChild(m_backgroundTexture);

        // Place buttons in front of the background.
        final double foregroundZ = 1.0;

        m_whiteBlackListButton = new WhiteBlackListButton(screen, GUI_WHITELIST_X, GUI_WHITELIST_Y, null);
        m_whiteBlackListButton.setZ(foregroundZ);
        addChild(m_whiteBlackListButton);

        m_matchCountButton = new MatchCountButton(screen, GUI_MATCH_AMOUNT_X, GUI_MATCH_AMOUNT_Y, MATCH_AMOUNT_TEXTURE,
                IGNORE_AMOUNT_TEXTURE, this::onMatchCountChanged);
        m_matchCountButton.setMatchTooltip(new StringTextComponent("Match fluid amount"));
        m_matchCountButton.setIgnoreTooltip(new StringTextComponent("Ignore fluid amount"));
        m_matchCountButton.setZ(foregroundZ);
        addChild(m_matchCountButton);

        m_matchModButton = new MatchModButton(screen, GUI_MATCH_MOD_X, GUI_MATCH_MOD_Y, null);
        m_matchModButton.setZ(foregroundZ);
        addChild(m_matchModButton);

        m_matchOreDictionaryButton = new MatchOreDictionaryButton(screen, GUI_MATCH_ORE_DICTIONARY_X,
                GUI_MATCH_ORE_DICTIONARY_Y, null);
        m_matchOreDictionaryButton.setZ(foregroundZ);
        addChild(m_matchOreDictionaryButton);

        m_filterSlots = new ArrayList<GhostFluidSlot>();

        GuiDeleteButton deleteButton = new GuiDeleteButton(screen, GUI_DELETE_X, GUI_DELETE_Y,
                this::onDeleteButtonPressed);
        deleteButton.setTooltip(new StringTextComponent("Remove all filters"));
        addChild(deleteButton);

        GuiTexture infoTexture = new GuiTexture(screen, GUI_INFO_X, GUI_INFO_Y, 16, 16, INFO_TEXTURE);
        addChild(infoTexture);

        GuiTooltip infoTooltip = new GuiTooltip(screen, GUI_INFO_X, GUI_INFO_Y, 16, 16);
        List<ITextComponent> tooltip = new ArrayList<ITextComponent>();
        tooltip.add(new StringTextComponent("Left click on a slot to add or increment a filter."));
        tooltip.add(new StringTextComponent("Right click on a slot to remove or decrement a filter."));
        tooltip.add(new StringTextComponent("Shift + click to add/remove 16 buckets."));
        tooltip.add(new StringTextComponent("Ctrl + click to add/remove 100 mB."));
        tooltip.add(new StringTextComponent("Shift + Ctrl + click to add/remove 1 mB."));
        tooltip.add(new StringTextComponent("Max quantity: 999 buckets"));
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

    public void setMatchModChangedCallback(GuiBooleanStateButton.StateChangeHandler callback)
    {
        m_matchModButton.setStateChangedHandler(callback);
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

    public void setMatchMod(boolean match)
    {
        m_matchModButton.setState(match);
    }

    public boolean getMatchMod()
    {
        return m_matchModButton.getState();
    }

    public void setMatchOreDictionary(boolean match)
    {
        m_matchOreDictionaryButton.setState(match);
    }

    public boolean getMatchOreDictionary()
    {
        return m_matchOreDictionaryButton.getState();
    }

    public void setFilters(List<FluidStack> filters)
    {
        int numFilterSlots = filters.size();
        int filterColumns = numFilterSlots > FILTER_SLOTS_PER_ROW ? FILTER_SLOTS_PER_ROW : numFilterSlots;
        int filterRows = (numFilterSlots + FILTER_SLOTS_PER_ROW) / FILTER_SLOTS_PER_ROW;

        if (filters.size() == m_filterSlots.size())
        {
            for (int i = 0; i < m_filterSlots.size(); ++i)
            {
                GhostFluidSlot slot = m_filterSlots.get(i);
                slot.setFluidStack(filters.get(i));
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
                    GhostFluidSlot filterSlot = new GhostFluidSlot(getScreen(), x + 1, y + 1);
                    filterSlot.setIsFilterValidCallback(this::isFilterValid);
                    filterSlot.setFilterUpdatedCallback(this::onFilterChanged);
                    filterSlot.setZ(1.0);
                    filterSlot.setSlot(slot);
                    filterSlot.setMaxQuantity(999_000);
                    filterSlot.showQuantity(m_matchCountButton.getState());
                    if (i < filters.size())
                    {
                        filterSlot.setFluidStack(filters.get(slot));
                    }
                    m_filterSlots.add(filterSlot);
                    addChild(filterSlot);
                }
            }
        }
    }

    protected boolean isFilterValid(int slot, FluidStack stack)
    {
        // Make sure the same item stack doesn't already exist in another slot.
        for (int i = 0; i < m_filterSlots.size(); ++i)
        {
            if (i == slot)
                continue;

            FluidStack s = m_filterSlots.get(i).getFluidStack();
            if (stack.getFluid() == s.getFluid())
            {
                // Item already exists; abort.
                return false;
            }
        }

        return true;
    }

    protected void onFilterChanged(int slot, FluidStack stack)
    {
        if (m_filterChangedCallback != null)
            m_filterChangedCallback.onFilterChanged(slot, stack);
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
            GhostFluidSlot slot = m_filterSlots.get(i);
            slot.setFluidStack(FluidStack.EMPTY);
            slot.setQuantity(0);
        }

        if (m_deleteButtonCallback != null)
            m_deleteButtonCallback.invoke();
    }

    protected static final int GUI_WHITELIST_X = 1;
    protected static final int GUI_WHITELIST_Y = 1;

    protected static final int GUI_MATCH_AMOUNT_X = GUI_WHITELIST_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_AMOUNT_Y = GUI_WHITELIST_Y;

    protected static final int GUI_MATCH_MOD_X = GUI_MATCH_AMOUNT_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_MOD_Y = GUI_MATCH_AMOUNT_Y;

    protected static final int GUI_MATCH_ORE_DICTIONARY_X = GUI_MATCH_MOD_X + SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_MATCH_ORE_DICTIONARY_Y = GUI_MATCH_MOD_Y;

    protected static final int GUI_INFO_X = 145;
    protected static final int GUI_INFO_Y = GUI_MATCH_ORE_DICTIONARY_Y;

    protected static final int GUI_DELETE_X = GUI_INFO_X - SwiftGui.INVENTORY_SLOT_WIDTH;
    protected static final int GUI_DELETE_Y = GUI_INFO_Y;

    protected static final int GUI_FILTER_X = 0;
    protected static final int GUI_FILTER_Y = 18;

    protected static final int FILTER_SLOTS_PER_ROW = 9;

    protected static final ResourceLocation MATCH_AMOUNT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/match_fluid_amount.png");

    protected static final ResourceLocation IGNORE_AMOUNT_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/ignore_fluid_amount.png");

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/basic_filter_upgrade.png");

    protected static final ResourceLocation INFO_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/info.png");

    protected GuiTexture m_backgroundTexture;
    protected WhiteBlackListButton m_whiteBlackListButton;
    protected MatchCountButton m_matchCountButton;
    protected MatchModButton m_matchModButton;
    protected MatchOreDictionaryButton m_matchOreDictionaryButton;
    protected ArrayList<GhostFluidSlot> m_filterSlots;
    protected GuiBooleanStateButton.StateChangeHandler m_matchCountChangedCallback;
    protected FilterChangedCallback m_filterChangedCallback;
    protected Notification m_deleteButtonCallback;
}
