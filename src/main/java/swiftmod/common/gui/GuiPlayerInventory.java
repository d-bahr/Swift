package swiftmod.common.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        AbstractContainerMenu c = getScreen().getMenu();
        if (c instanceof PlayerInventoryContainer)
            ((PlayerInventoryContainer)c).enablePlayerInventorySlots(true);
    }

    public void hide()
    {
        super.hide();
        AbstractContainerMenu c = getScreen().getMenu();
        if (c instanceof PlayerInventoryContainer)
            ((PlayerInventoryContainer)c).enablePlayerInventorySlots(false);
    }

    public static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "textures/gui/player_inventory.png");
}
