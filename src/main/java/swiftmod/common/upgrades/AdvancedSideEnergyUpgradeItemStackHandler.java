package swiftmod.common.upgrades;

public class AdvancedSideEnergyUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public AdvancedSideEnergyUpgradeItemStackHandler()
    {
        super(2);
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
        default:
            return -1;
        }
    }
}
