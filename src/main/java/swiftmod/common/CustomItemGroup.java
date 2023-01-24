package swiftmod.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CustomItemGroup extends CreativeModeTab
{
	// This is basically just a higher-level base class of Block;
	// Block can be used instead if porting becomes an issue in the future.
    private ItemLike icon;

    public CustomItemGroup(String label, ItemLike icon)
    {
        super(label);
        this.icon = icon;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(icon);
    }
}
