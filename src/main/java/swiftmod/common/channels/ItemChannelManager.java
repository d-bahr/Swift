package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ItemChannelManager extends OwnerBasedChannelManager<ChannelData>
{
    public ItemChannelManager()
    {
    	super(NAME, ChannelData::new, ChannelData::new);
    }

    public static ItemChannelManager getManager()
    {
        return getManager(ItemChannelManager::createAndLoad, ItemChannelManager::new, NAME);
    }

    public static ItemChannelManager getManager(Level world)
    {
        return getManager(world, ItemChannelManager::createAndLoad, ItemChannelManager::new, NAME);
    }

    public static ItemChannelManager getManager(ResourceKey<Level> world)
    {
        return getManager(world, ItemChannelManager::createAndLoad, ItemChannelManager::new, NAME);
    }

    private static ItemChannelManager createAndLoad(CompoundTag nbt)
    {
    	ItemChannelManager manager = new ItemChannelManager();
    	manager.load(nbt);
    	return manager;
    }

    private static String NAME = "swift.ItemChannels";
}
