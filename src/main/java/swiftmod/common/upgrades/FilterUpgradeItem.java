package swiftmod.common.upgrades;

import swiftmod.common.SwiftUtils;

public class FilterUpgradeItem extends UpgradeItem
{
    protected FilterUpgradeItem(UpgradeType type)
    {
        super(type);
    }

    protected FilterUpgradeItem(UpgradeType type, String registryName)
    {
        super(type, registryName);
    }

    protected FilterUpgradeItem(UpgradeType type, int stackSize)
    {
        super(type, stackSize);
    }

    protected FilterUpgradeItem(UpgradeType type, int stackSize, String registryName)
    {
        super(type, stackSize, registryName);
    }

    public static final String NBT_TAG = SwiftUtils.tagName("filter");
}
