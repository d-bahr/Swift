package swiftmod.common.channels;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.SwiftUtils;

public class ChannelOwner
{
    public ChannelOwner()
    {
        uuid = null;
    }

    public ChannelOwner(UUID u)
    {
        uuid = u;
    }

    public ChannelOwner(CompoundNBT nbt)
    {
        read(nbt);
    }

    public ChannelOwner(PacketBuffer buffer)
    {
        read(buffer);
    }

    public void setPublic()
    {
        uuid = null;
    }

    public void setPrivate(UUID u)
    {
        uuid = u;
    }

    public boolean isPrivate()
    {
        return uuid != null;
    }

    public UUID get()
    {
        return uuid;
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putBoolean(SwiftUtils.tagName("channel_private"), isPrivate());
        if (isPrivate())
            nbt.putUUID(SwiftUtils.tagName("channel_owner"), get());
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        boolean isPrivate = nbt.getBoolean(SwiftUtils.tagName("channel_private"));
        if (isPrivate)
            setPrivate(nbt.getUUID(SwiftUtils.tagName("channel_owner")));
        else
            setPublic();
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        buffer.writeBoolean(isPrivate());
        if (isPrivate())
            buffer.writeUUID(get());
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        boolean isPrivate = buffer.readBoolean();
        if (isPrivate)
            setPrivate(buffer.readUUID());
        else
            setPublic();
    }

    @Override
    public int hashCode()
    {
        return uuid == null ? 0 : uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != ChannelOwner.class))
            return false;
        ChannelOwner other = (ChannelOwner) obj;
        if (uuid == null)
            return other.uuid == null;
        else if (other.uuid == null)
            return false;
        else
            return uuid.equals(other.uuid);
    }

    public String toString()
    {
        return uuid == null ? "{null}" : uuid.toString();
    }

    public int compareTo(ChannelOwner other)
    {
        if (uuid == null)
            return other.uuid == null ? 0 : -1;
        else if (other.uuid == null)
            return 1;
        else
            return uuid.compareTo(other.uuid);
    }

    private UUID uuid;

    public static final ChannelOwner Public = new ChannelOwner();
}
