package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class BasicItemPipeContainerScreen extends PipeContainerScreen<BasicItemPipeContainer>
{
    public BasicItemPipeContainerScreen(BasicItemPipeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }
}
