package swiftmod.common.channels;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import swiftmod.pipes.networks.PipeChannelNetwork;

public class BaseChannelManager extends OwnerBasedChannelManager<PipeChannelNetwork>
{
    public BaseChannelManager()
    {
    	super(NAME, PipeChannelNetwork::create, PipeChannelNetwork::decode);
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

    private static BaseChannelManager createAndLoad(CompoundTag nbt, HolderLookup.Provider provider)
    {
    	BaseChannelManager manager = new BaseChannelManager();
    	manager.load(provider, nbt);
    	return manager;
    }

    private static String NAME = "swift.BaseChannels";
}
