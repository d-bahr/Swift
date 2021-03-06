package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicItemPipeContainerScreen extends PipeContainerScreen<BasicItemPipeContainer>
{
    public BasicItemPipeContainerScreen(BasicItemPipeContainer c, PlayerInventory inv, ITextComponent title)
    {
        super(c, inv, title);
    }
}
