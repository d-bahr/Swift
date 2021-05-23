package swiftmod.common.channels;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class ChannelData
{
    public ChannelData()
    {
    }

    public ChannelData(CompoundNBT nbt)
    {
        read(nbt);
    }

    public ChannelData(PacketBuffer buffer)
    {
        read(buffer);
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
    }
}
