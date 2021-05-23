package swiftmod.common;

import net.minecraft.util.ResourceLocation;

// TODO: Deprecated; remove
public class FilterPage extends ContainerScreenPage
{
    public FilterPage()
    {
        setBackgroundTexture(BASE_TEXTURE);
    }

    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Swift.MOD_NAME, "textures/gui/filter.png");
}
