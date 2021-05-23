package swiftmod.common.upgrades;

public class BasicSideUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public BasicSideUpgradeItemStackHandler()
    {
        super(0);
    }

    @Override
    public int getMaxUpgrades(UpgradeType type)
    {
        return 0;
    }

    @Override
    public int getSlotForUpgrade(UpgradeType type)
    {
        return 0;
    }
}
