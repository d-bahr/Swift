package swiftmod.common.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import swiftmod.common.DataCache;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.ChannelOwner;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.ChannelType;

public class ChannelConfigurationDataCache implements DataCache
{
    public ChannelConfigurationDataCache()
    {
        currentChannels = new String[ChannelType.NUM_TYPES];
        currentOwners = new ChannelOwner[ChannelType.NUM_TYPES];
        
        for (int i = 0; i < ChannelType.NUM_TYPES; ++i)
        {
        	currentChannels[i] = "";
        	currentOwners[i] = ChannelOwner.Public;
        }
    }

    public void write(CompoundTag nbt, boolean serializeChannels)
    {
        for (int c = 0; c < ChannelType.NUM_TYPES; ++c)
        {
        	CompoundTag nested = new CompoundTag();
        	nested.putString(TAG_CURRENT_CHANNEL, currentChannels[c]);
        	currentOwners[c].write(nested);
        	
	        nbt.put(TAG_CHANNEL_TYPE[c], nested);
        }
    }

    public void write(CompoundTag nbt)
    {
        write(nbt, true);
    }

    public void read(CompoundTag nbt)
    {
        for (int c = 0; c < ChannelType.NUM_TYPES; ++c)
        {
            currentChannels[c] = "";
        	currentOwners[c] = ChannelOwner.Public;
        }

        for (int c = 0; c < TAG_CHANNEL_TYPE.length; ++c)
        {
        	CompoundTag nested = nbt.getCompound(TAG_CHANNEL_TYPE[c]);
        	if (nested == null)
        		continue;
        	
        	currentChannels[c] = nested.getString(TAG_CURRENT_CHANNEL);
        	currentOwners[c].read(nested);
        }
    }

    public void write(FriendlyByteBuf buffer)
    {
        for (int c = 0; c < ChannelType.NUM_TYPES; ++c)
        {
        	buffer.writeUtf(currentChannels[c]);
        	currentOwners[c].write(buffer);
        }
    }

    public void read(FriendlyByteBuf buffer)
    {
        for (int c = 0; c < ChannelType.NUM_TYPES; ++c)
        {
        	currentChannels[c] = buffer.readUtf(32767);
        	currentOwners[c].read(buffer);
        }
    }

    public void setChannel(ChannelSpec spec)
    {
    	int index = spec.type.getIndex();
		currentChannels[index] = spec.name;
		currentOwners[index] = spec.owner;
    }
    
    public void clearAllChannels()
    {
    	clearChannel(ChannelType.Items);
    	clearChannel(ChannelType.Fluids);
    	clearChannel(ChannelType.Energy);
    }

    public void clearChannel(ChannelType type)
    {
    	int index = type.getIndex();
		currentChannels[index] = "";
		currentOwners[index] = ChannelOwner.Public;
    }

    public ChannelSpec getChannel(int index)
    {
    	return new ChannelSpec(ChannelType.fromIndex(index), currentOwners[index], currentChannels[index]);
    }

    public ChannelSpec getChannel(ChannelType type)
    {
    	int index = type.getIndex();
    	return new ChannelSpec(type, currentOwners[index], currentChannels[index]);
    }

    public String currentChannels[];
    public ChannelOwner currentOwners[];

    public static final String TAG_CHANNEL_TYPE[] =
	{
		SwiftUtils.tagName("items"),
		SwiftUtils.tagName("fluids"),
		SwiftUtils.tagName("energy")
	};
    public static final String TAG_CURRENT_CHANNEL = SwiftUtils.tagName("channel");
    public static final String TAG_IS_PUBLIC = SwiftUtils.tagName("public");
}
