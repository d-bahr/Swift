package swiftmod.common.upgrades;

import swiftmod.common.ItemBase;
import swiftmod.common.Swift;

public class UpgradeItem extends ItemBase
{
    protected UpgradeItem(UpgradeType type)
    {
        this(type, 64);
    }

    protected UpgradeItem(UpgradeType type, String registryName)
    {
        this(type, 64, registryName);
    }

    protected UpgradeItem(UpgradeType type, int stackSize)
    {
        super(64, Swift.ITEM_GROUP);
        m_upgradeType = type;
    }

    protected UpgradeItem(UpgradeType type, int stackSize, String registryName)
    {
        super(stackSize, Swift.ITEM_GROUP);
        setRegistryName(Swift.MOD_NAME, registryName);
        m_upgradeType = type;
    }

    public UpgradeType getType()
    {
        return m_upgradeType;
    }

    protected UpgradeType m_upgradeType;
}
