package swiftmod.common.channels;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.SwiftUtils;

public class ChannelSpec
{
    public static final int TAG_ITEMS = 0;
    public static final int TAG_FLUIDS = 1;

    public ChannelSpec()
    {
        owner = new ChannelOwner();
        name = "";
        tag = 0;
    }

    public ChannelSpec(ChannelOwner o, String n)
    {
        owner = o;
        name = n;
        tag = 0;
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
        nbt.putInt(SwiftUtils.tagName("channel_tag"), tag);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        owner.read(nbt);
        name = nbt.getString(SwiftUtils.tagName("channel_name"));
        tag = nbt.getInt(SwiftUtils.tagName("channel_tag"));
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        owner.write(buffer);
        buffer.writeUtf(name);
        buffer.writeInt(tag);
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        owner.read(buffer);
        name = buffer.readUtf();
        tag = buffer.readInt();
    }

    public ChannelKey getKey()
    {
        return new ChannelKey(name, tag);
    }

    public ChannelOwner owner;
    public String name;
    public int tag;
}
