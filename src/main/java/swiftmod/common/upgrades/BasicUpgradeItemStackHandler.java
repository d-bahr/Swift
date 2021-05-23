package swiftmod.common.upgrades;

public class BasicUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public BasicUpgradeItemStackHandler()
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
