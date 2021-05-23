package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import swiftmod.common.SwiftTextUtils;

public class ChunkLoaderUpgradeItem extends UpgradeItem
{
    public ChunkLoaderUpgradeItem()
    {
        super(UpgradeType.ChunkLoaderUpgrade, REGISTRY_NAME);
    }

    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Keeps the chunk containing the pipe loaded.", SwiftTextUtils.AQUA)));
    }

    public static final String REGISTRY_NAME = "chunk_loader_upgrade";
}
