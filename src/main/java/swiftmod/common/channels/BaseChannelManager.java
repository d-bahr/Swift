package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class BaseChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public BaseChannelManager()
    {
    	super(NAME, ChannelData::new, ChannelData::new);
    }

    public static BaseChannelManager getManager()
    {
        return getManager(BaseChannelManager::createAndLoad, BaseChannelManager::new, NAME);
    }

    public static BaseChannelManager getManager(Level world)
    {
        return getManager(world, BaseChannelManager::createAndLoad, BaseChannelManager::new, NAME);
    }

    public static BaseChannelManager getManager(ResourceKey<Level> world)
    {
        return getManager(world, BaseChannelManager::createAndLoad, BaseChannelManager::new, NAME);
    }

    private static BaseChannelManager createAndLoad(CompoundTag nbt)
    {
    	BaseChannelManager manager = new BaseChannelManager();
    	manager.load(nbt);
    	return manager;
    }

    private static String NAME = "swift.BaseChannels";
}
