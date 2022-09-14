package swiftmod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public enum TransferDirection
{
    Extract(0),
    Insert(1);

    private int index;

    private TransferDirection(int i)
    {
        index = i;
    }

    public int getIndex()
    {
        return index;
    }

    private static final TransferDirection[] BY_INDEX = { Extract, Insert };

    public static TransferDirection fromIndex(int index)
    {
        return BY_INDEX[index];
    }

    public static int[] toIntArray(TransferDirection[] directions)
    {
        int[] a = new int[directions.length];
        for (int i = 0; i < directions.length; ++i)
            a[i] = directions[i].getIndex();
        return a;
    }

    public static TransferDirection[] fromIntArray(int[] a)
    {
        TransferDirection[] directions = new TransferDirection[a.length];
        for (int i = 0; i < a.length; ++i)
            directions[i] = fromIndex(a[i]);
        return directions;
    }

    public static CompoundTag write(CompoundTag nbt, TransferDirection direction)
    {
        nbt.putInt(SwiftUtils.tagName("transferDirection"), direction.getIndex());
        return nbt;
    }

    public static CompoundTag writeArray(CompoundTag nbt, TransferDirection[] directions)
    {
        int[] i = TransferDirection.toIntArray(directions);
        nbt.putIntArray(SwiftUtils.tagName("transferDirections"), i);
        return nbt;
    }

    public static TransferDirection read(CompoundTag nbt)
    {
        int i = nbt.getInt(SwiftUtils.tagName("transferDirection"));
        return TransferDirection.fromIndex(i);
    }

    public static TransferDirection[] readArray(CompoundTag nbt)
    {
        int[] i = nbt.getIntArray(SwiftUtils.tagName("transferDirections"));
        return TransferDirection.fromIntArray(i);
    }

    public static FriendlyByteBuf write(FriendlyByteBuf FriendlyByteBuf, TransferDirection direction)
    {
        FriendlyByteBuf.writeVarInt(direction.getIndex());
        return FriendlyByteBuf;
    }

    public static FriendlyByteBuf writeArray(FriendlyByteBuf FriendlyByteBuf, TransferDirection[] directions)
    {
        FriendlyByteBuf.writeVarIntArray(TransferDirection.toIntArray(directions));
        return FriendlyByteBuf;
    }

    public static TransferDirection read(FriendlyByteBuf FriendlyByteBuf)
    {
        return TransferDirection.fromIndex(FriendlyByteBuf.readVarInt());
    }

    public static TransferDirection[] readArray(FriendlyByteBuf FriendlyByteBuf)
    {
        return TransferDirection.fromIntArray(FriendlyByteBuf.readVarIntArray());
    }
}
