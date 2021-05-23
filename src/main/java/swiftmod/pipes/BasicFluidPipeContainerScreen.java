package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class BasicFluidPipeContainerScreen extends PipeContainerScreen<BasicFluidPipeContainer>
{
    public BasicFluidPipeContainerScreen(BasicFluidPipeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }
}
