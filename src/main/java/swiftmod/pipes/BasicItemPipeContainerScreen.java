package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicItemPipeContainerScreen extends PipeContainerScreen<BasicItemPipeContainer>
{
    public BasicItemPipeContainerScreen(BasicItemPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
    }
}
