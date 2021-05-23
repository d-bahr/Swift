package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class AdvancedFluidPipeContainerScreen extends AbstractAdvancedFluidPipeContainerScreen<AdvancedFluidPipeContainer>
{
    public AdvancedFluidPipeContainerScreen(AdvancedFluidPipeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }
}
