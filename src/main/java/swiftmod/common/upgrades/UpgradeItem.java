package swiftmod.common.upgrades;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.ItemBase;
import swiftmod.common.Swift;
import swiftmod.pipes.PipeTileEntity;

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

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
    	Level level = context.getLevel();
    	BlockEntity entity = level.getBlockEntity(context.getClickedPos());
    	ItemStack stack = context.getItemInHand();
    	if (entity instanceof PipeTileEntity<?,?,?>)
    	{
    		PipeTileEntity<?,?,?> pipeEntity = (PipeTileEntity<?,?,?>) entity;
	    	Direction dir = context.getClickedFace();
    		if (pipeEntity.tryAddUpgrade(stack, dir))
                return InteractionResult.SUCCESS;
    	}
    	return InteractionResult.PASS;
    }

    protected UpgradeType m_upgradeType;
}
