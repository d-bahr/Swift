package swiftmod.common.channels;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Channel<T extends ChannelData>
{
    private Channel(Supplier<T> supplier)
    {
        spec = new ChannelSpec();
        data = supplier.get();
    }
    
    public Channel(ChannelType t, Supplier<T> supplier)
    {
        spec = new ChannelSpec(t);
        data = supplier.get();
    }

    public Channel(ChannelSpec s, Supplier<T> supplier)
    {
        spec = s;
        data = supplier.get();
    }

    public Channel(ChannelSpec s, T d)
    {
        spec = s;
        data = d;
    }

    public void write(CompoundTag nbt)
    {
        spec.write(nbt);
        data.write(nbt);
    }

    public void read(CompoundTag nbt)
    {
        spec.read(nbt);
        data.read(nbt);
    }

    public void write(FriendlyByteBuf buffer)
    {
        spec.write(buffer);
        data.write(buffer);
    }

    public void read(FriendlyByteBuf buffer)
    {
        spec.read(buffer);
        data.read(buffer);
    }
    
    public ChannelSpec getSpec()
    {
    	return spec;
    }
    
    public static <T extends ChannelData> void writeToStream(RegistryFriendlyByteBuf buffer, Channel<T> channel)
    {
    	channel.write(buffer);
    }
    
    public static Channel<ChannelData> readFromStream(RegistryFriendlyByteBuf buffer)
    {
    	Channel<ChannelData> channel = new Channel<ChannelData>(ChannelData::new);
    	channel.read(buffer);
    	return channel;
    }
    
    public static StreamCodec<RegistryFriendlyByteBuf, Channel<ChannelData>> makeStreamCodec()
    {
    	return StreamCodec.of(Channel::writeToStream, Channel::readFromStream);
    }

    public ChannelSpec spec;
    public T data;
}
