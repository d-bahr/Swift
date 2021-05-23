package swiftmod.common.gui;

import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.PlayerInventoryContainer;
import swiftmod.common.Swift;

@OnlyIn(Dist.CLIENT)
public class GuiPlayerInventory extends GuiTexture
{
    public GuiPlayerInventory(GuiContainerScreen<?> screen, int x, int y)
    {
        this(screen, x, y, DEFAULT_TEXTURE);
    }

    public GuiPlayerInventory(GuiContainerScreen<?> screen, int x, int y, ResourceLocation texture)
    {
        super(screen, x, y, 162, 76, texture);
    }

    public void show()
    {
        super.show();
        Container c = getScreen().getMenu();
        if (c instanceof PlayerInventoryContainer)
            ((PlayerInventoryContainer)c).enablePlayerInventorySlots(true);
    }

    public void hide()
    {
        super.hide();
        Container c = getScreen().getMenu();
        if (c instanceof PlayerInventoryContainer)
            ((PlayerInventoryContainer)c).enablePlayerInventorySlots(false);
    }

    public static final ResourceLocation DEFAULT_TEXTURE =
            new ResourceLocation(Swift.MOD_NAME, "textures/gui/player_inventory.png");
}
