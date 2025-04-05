package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ChannelData
{
    public ChannelData()
    {
    }

    public ChannelData(CompoundTag nbt)
    {
        read(nbt);
    }

    public ChannelData(FriendlyByteBuf buffer)
    {
        read(buffer);
    }

    public void write(CompoundTag nbt)
    {
    }

    public void read(CompoundTag nbt)
    {
    }

    public void write(FriendlyByteBuf buffer)
    {
    }

    public void read(FriendlyByteBuf buffer)
    {
    }
    
    public static ChannelData create(ChannelSpec spec)
    {
    	return new ChannelData();
    }
    
    public static ChannelData decode(CompoundTag nbt, ChannelSpec spec)
    {
    	return new ChannelData(nbt);
    }
}
