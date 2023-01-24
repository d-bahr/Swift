package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedItemPipeContainerScreen extends AbstractAdvancedItemPipeContainerScreen<AdvancedItemPipeContainer>
{
    public AdvancedItemPipeContainerScreen(AdvancedItemPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
    }
}
