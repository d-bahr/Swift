package swiftmod.common.upgrades;

import swiftmod.common.SwiftUtils;

public class FilterUpgradeItem extends UpgradeItem
{
    protected FilterUpgradeItem(UpgradeType type)
    {
        super(type);
    }

    protected FilterUpgradeItem(UpgradeType type, int stackSize)
    {
        super(type, stackSize);
    }

    public static final String NBT_TAG = SwiftUtils.tagName("filter");
}
