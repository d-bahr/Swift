package swiftmod.common.upgrades;

public class AdvancedSideItemUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public AdvancedSideItemUpgradeItemStackHandler()
    {
        super(2);
    }

    @Override
    public int getMaxUpgrades(UpgradeType type)
    {
        switch (type)
        {
        case BasicItemFilterUpgrade:
            return 1;
        case WildcardFilterUpgrade:
            return 1;
        case SideUpgrade:
            return 1;
        default:
            return 0;
        }
    }

    @Override
    public int getSlotForUpgrade(UpgradeType type)
    {
        switch (type)
        {
        case BasicItemFilterUpgrade:
            return 0;
        case WildcardFilterUpgrade:
            return 0;
        case SideUpgrade:
            return 1;
        default:
            return -1;
        }
    }
}
