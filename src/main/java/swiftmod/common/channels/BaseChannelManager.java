package swiftmod.common.channels;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public class BaseChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public BaseChannelManager()
    {
        super(NAME, ChannelData::new, ChannelData::new);
    }

    public static BaseChannelManager getManager()
    {
        return getManager(BaseChannelManager::new, NAME);
    }

    public static BaseChannelManager getManager(World world)
    {
        return getManager(world, BaseChannelManager::new, NAME);
    }

    public static BaseChannelManager getManager(RegistryKey<World> world)
    {
        return getManager(world, BaseChannelManager::new, NAME);
    }

    private static String NAME = "swift.BaseChannels";
}
