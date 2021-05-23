package swiftmod.common.channels;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.SwiftUtils;

public class ChannelSpec
{
    public ChannelSpec()
    {
        owner = new ChannelOwner();
        name = "";
    }

    public ChannelSpec(ChannelOwner o, String n)
    {
        owner = o;
        name = n;
    }

    public ChannelSpec(CompoundNBT nbt)
    {
        owner = new ChannelOwner();
        read(nbt);
    }

    public ChannelSpec(PacketBuffer buffer)
    {
        owner = new ChannelOwner();
        read(buffer);
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        owner.write(nbt);
        nbt.putString(SwiftUtils.tagName("channel_name"), name);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        owner.read(nbt);
        name = nbt.getString(SwiftUtils.tagName("channel_name"));
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        owner.write(buffer);
        buffer.writeUtf(name);
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        owner.read(buffer);
        name = buffer.readUtf();
    }

    public ChannelOwner owner;
    public String name;
}
