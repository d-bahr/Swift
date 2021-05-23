package swiftmod.common.channels;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public class FluidChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public FluidChannelManager()
    {
        super(NAME, ChannelData::new, ChannelData::new);
    }

    public static FluidChannelManager getManager()
    {
        return getManager(FluidChannelManager::new, NAME);
    }

    public static FluidChannelManager getManager(World world)
    {
        return getManager(world, FluidChannelManager::new, NAME);
    }

    public static FluidChannelManager getManager(RegistryKey<World> world)
    {
        return getManager(world, FluidChannelManager::new, NAME);
    }
    
    private static String NAME = "swift.FluidChannels";
}
