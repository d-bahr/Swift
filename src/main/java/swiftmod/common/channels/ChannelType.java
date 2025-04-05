package swiftmod.common.channels;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import swiftmod.common.SwiftUtils;
import swiftmod.pipes.PipeType;

public enum ChannelType
{
	Items(0),
	Fluids(1),
	Energy(2);

    private int index;

    private ChannelType(int i)
    {
        index = i;
    }

    public int getIndex()
    {
        return index;
    }
    
    public PipeType toPipeType()
    {
    	// Note: all channel types must have a corresponding pipe type.
    	switch (index)
    	{
    	case 0:
    		return PipeType.Item;
    	case 1:
    		return PipeType.Fluid;
    	case 2:
    		return PipeType.Energy;
    	default:
    		throw new RuntimeException("Unexpected channel type.");
    	}
    }
    
    private static final java.util.function.IntFunction<ChannelType> BY_ID = ByIdMap.continuous(ChannelType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    
    public static final StreamCodec<ByteBuf, ChannelType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ChannelType::ordinal);

    public static final ChannelType[] TYPES = { Items, Fluids, Energy };
    
    public static final int NUM_TYPES = TYPES.length;

    public static ChannelType fromIndex(int index)
    {
        return TYPES[index];
    }

    public static byte[] toByteArray(ChannelType[] c)
    {
    	byte[] a = new byte[c.length];
        for (int i = 0; i < c.length; ++i)
            a[i] = (byte)c[i].getIndex();
        return a;
    }

    public static ChannelType[] fromByteArray(byte[] a)
    {
    	ChannelType[] rc = new ChannelType[a.length];
        for (int i = 0; i < a.length; ++i)
            rc[i] = fromIndex(a[i]);
        return rc;
    }

    public static CompoundTag write(CompoundTag nbt, ChannelType c)
    {
        nbt.putByte(SwiftUtils.tagName("channelType"), (byte)c.getIndex());
        return nbt;
    }

    public static CompoundTag writeArray(CompoundTag nbt, ChannelType[] c)
    {
        byte[] i = ChannelType.toByteArray(c);
        nbt.putByteArray(SwiftUtils.tagName("channelTypes"), i);
        return nbt;
    }

    public static ChannelType read(CompoundTag nbt)
    {
        byte b = nbt.getByte(SwiftUtils.tagName("channelType"));
        return ChannelType.fromIndex(b);
    }

    public static ChannelType[] readArray(CompoundTag nbt)
    {
        byte[] b = nbt.getByteArray(SwiftUtils.tagName("channelTypes"));
        return ChannelType.fromByteArray(b);
    }

    public static FriendlyByteBuf write(FriendlyByteBuf FriendlyByteBuf, ChannelType c)
    {
        FriendlyByteBuf.writeByte(c.getIndex());
        return FriendlyByteBuf;
    }

    public static FriendlyByteBuf writeArray(FriendlyByteBuf FriendlyByteBuf, ChannelType[] c)
    {
        FriendlyByteBuf.writeByteArray(ChannelType.toByteArray(c));
        return FriendlyByteBuf;
    }

    public static ChannelType read(FriendlyByteBuf FriendlyByteBuf)
    {
        return ChannelType.fromIndex(FriendlyByteBuf.readByte());
    }

    public static ChannelType[] readArray(FriendlyByteBuf FriendlyByteBuf)
    {
        return ChannelType.fromByteArray(FriendlyByteBuf.readByteArray());
    }
}
