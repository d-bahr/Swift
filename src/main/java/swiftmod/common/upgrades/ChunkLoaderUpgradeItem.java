package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import swiftmod.common.SwiftTextUtils;

public class ChunkLoaderUpgradeItem extends UpgradeItem
{
    public ChunkLoaderUpgradeItem()
    {
        super(UpgradeType.ChunkLoaderUpgrade);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Keeps the chunk containing the pipe loaded.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "chunk_loader_upgrade";
}
