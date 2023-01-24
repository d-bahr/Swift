package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicFluidPipeContainerScreen extends PipeContainerScreen<BasicFluidPipeContainer>
{
    public BasicFluidPipeContainerScreen(BasicFluidPipeContainer c, Inventory inv, Component title)
    {
        super(c, inv, title);
    }
}
