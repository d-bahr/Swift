package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class AdvancedItemPipeContainerScreen extends AbstractAdvancedItemPipeContainerScreen<AdvancedItemPipeContainer>
{
    public AdvancedItemPipeContainerScreen(AdvancedItemPipeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }
}
