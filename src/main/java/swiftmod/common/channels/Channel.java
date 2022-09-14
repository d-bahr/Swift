package swiftmod.common.channels;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class Channel<T extends ChannelData>
{
    public Channel(Supplier<T> supplier)
    {
        spec = new ChannelSpec();
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

    public ChannelSpec spec;
    public T data;
}
