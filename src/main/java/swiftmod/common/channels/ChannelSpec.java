package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import swiftmod.common.SwiftUtils;

public class ChannelSpec
{
    public ChannelSpec()
    {
    	type = ChannelType.Items;
        owner = new ChannelOwner();
        name = "";
    }
    
    public ChannelSpec(ChannelType t)
    {
    	type = t;
        owner = new ChannelOwner();
        name = "";
    }

    public ChannelSpec(ChannelType t, ChannelOwner o, String n)
    {
    	type = t;
        owner = o;
        name = n;
    }

    public ChannelSpec(CompoundTag nbt)
    {
        owner = new ChannelOwner();
        read(nbt);
    }

    public ChannelSpec(FriendlyByteBuf buffer)
    {
        owner = new ChannelOwner();
        read(buffer);
    }

    public void write(CompoundTag nbt)
    {
    	ChannelType.write(nbt, type);
        owner.write(nbt);
        nbt.putString(SwiftUtils.tagName("channel_name"), name);
    }

    public void read(CompoundTag nbt)
    {
    	type = ChannelType.read(nbt);
        owner.read(nbt);
        name = nbt.getString(SwiftUtils.tagName("channel_name"));
    }

    public void write(FriendlyByteBuf buffer)
    {
    	ChannelType.write(buffer, type);
        owner.write(buffer);
        buffer.writeUtf(name);
    }

    public void read(FriendlyByteBuf buffer)
    {
    	type = ChannelType.read(buffer);
        owner.read(buffer);
        name = buffer.readUtf(32767);
    }

    public ChannelKey getKey()
    {
        return new ChannelKey(name, type);
    }
    
    public boolean isValid()
    {
    	return !name.isEmpty();
    }
    
    public ChannelType getType()
    {
    	return type;
    }
    
    public ChannelOwner getOwner()
    {
    	return owner;
    }
    
    public String getName()
    {
    	return name;
    }
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ChannelSpec> STREAM_CODEC =
    		StreamCodec.composite(ChannelType.STREAM_CODEC, ChannelSpec::getType,
    				ChannelOwner.STREAM_CODEC, ChannelSpec::getOwner,
    				ByteBufCodecs.STRING_UTF8, ChannelSpec::getName,
    				ChannelSpec::new);

    public ChannelType type;
    public ChannelOwner owner;
    public String name;
}
