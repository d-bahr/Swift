package swiftmod.common.channels;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public class ItemChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public ItemChannelManager()
    {
        super(NAME, ChannelData::new, ChannelData::new);
    }

    public static ItemChannelManager getManager()
    {
        return getManager(ItemChannelManager::new, NAME);
    }

    public static ItemChannelManager getManager(World world)
    {
        return getManager(world, ItemChannelManager::new, NAME);
    }

    public static ItemChannelManager getManager(RegistryKey<World> world)
    {
        return getManager(world, ItemChannelManager::new, NAME);
    }

    private static String NAME = "swift.ItemChannels";
}
