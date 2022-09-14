package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FluidChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public FluidChannelManager()
    {
    	super(NAME, ChannelData::new, ChannelData::new);
    }

    public static FluidChannelManager getManager()
    {
        return getManager(FluidChannelManager::createAndLoad, FluidChannelManager::new, NAME);
    }

    public static FluidChannelManager getManager(Level world)
    {
        return getManager(world, FluidChannelManager::createAndLoad, FluidChannelManager::new, NAME);
    }

    public static FluidChannelManager getManager(ResourceKey<Level> world)
    {
        return getManager(world, FluidChannelManager::createAndLoad, FluidChannelManager::new, NAME);
    }

    private static FluidChannelManager createAndLoad(CompoundTag nbt)
    {
    	FluidChannelManager manager = new FluidChannelManager();
    	manager.load(nbt);
    	return manager;
    }
    
    private static String NAME = "swift.FluidChannels";
}
