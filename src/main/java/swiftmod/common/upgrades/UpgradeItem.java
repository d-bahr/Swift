package swiftmod.common.upgrades;

import swiftmod.common.ItemBase;

public class UpgradeItem extends ItemBase
{
    protected UpgradeItem(UpgradeType type)
    {
        this(type, 64);
    }

    protected UpgradeItem(UpgradeType type, int stackSize)
    {
        super(64);
        m_upgradeType = type;
    }

    public UpgradeType getType()
    {
        return m_upgradeType;
    }

    protected UpgradeType m_upgradeType;
}
