package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import swiftmod.common.gui.SideIOConfigurationWidget;

@OnlyIn(Dist.CLIENT)
public abstract class AdvancedPipeContainerScreen<T extends PipeContainer> extends PipeContainerScreen<T>
{
    public AdvancedPipeContainerScreen(T c, Inventory inv, Component title, SideIOConfigurationWidget.FilterType filterType)
    {
        super(c, inv, title, filterType);
    }

    @Override
    public void earlyInit()
    {
        super.earlyInit();
    }

    @Override
    public void lateInit()
    {
        super.lateInit();
    }
}
