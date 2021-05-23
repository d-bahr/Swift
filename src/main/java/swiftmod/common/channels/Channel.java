package swiftmod.common.channels;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

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

    public CompoundNBT write(CompoundNBT nbt)
    {
        spec.write(nbt);
        data.write(nbt);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        spec.read(nbt);
        data.read(nbt);
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        spec.write(buffer);
        data.write(buffer);
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        spec.read(buffer);
        data.read(buffer);
        
    }

    public ChannelSpec spec;
    public T data;
}
