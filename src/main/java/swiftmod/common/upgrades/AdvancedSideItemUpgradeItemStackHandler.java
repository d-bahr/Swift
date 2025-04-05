package swiftmod.common.upgrades;

public class AdvancedSideItemUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public AdvancedSideItemUpgradeItemStackHandler()
    {
        super(3);
    }

    @Override
    public int getMaxUpgrades(UpgradeType type)
    {
        switch (type)
        {
        case SpeedUpgrade:
            return 19;
        case SpeedDowngrade:
            return 64;
        case StackUpgrade:
            return 64;
        case UltimateStackUpgrade:
            return 1;
        case BasicItemFilterUpgrade:
            return 1;
        case WildcardFilterUpgrade:
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
        case SpeedUpgrade:
            return 0;
        case SpeedDowngrade:
            return 0;
        case StackUpgrade:
            return 1;
        case UltimateStackUpgrade:
            return 1;
        case BasicItemFilterUpgrade:
            return 2;
        case WildcardFilterUpgrade:
            return 2;
        default:
            return -1;
        }
    }
}
