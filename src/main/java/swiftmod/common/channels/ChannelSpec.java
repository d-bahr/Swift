package swiftmod.common.channels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
        owner.write(nbt);
        nbt.putString(SwiftUtils.tagName("channel_name"), name);
        nbt.putInt(SwiftUtils.tagName("channel_tag"), tag);
    }

    public void read(CompoundTag nbt)
    {
        owner.read(nbt);
        name = nbt.getString(SwiftUtils.tagName("channel_name"));
        tag = nbt.getInt(SwiftUtils.tagName("channel_tag"));
    }

    public void write(FriendlyByteBuf buffer)
    {
        owner.write(buffer);
        buffer.writeUtf(name);
        buffer.writeInt(tag);
    }

    public void read(FriendlyByteBuf buffer)
    {
        owner.read(buffer);
        name = buffer.readUtf(32767);
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
