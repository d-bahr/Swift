package swiftmod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public enum WhiteListState
{
    WhiteList(0),
    BlackList(1);

    private int index;

    private WhiteListState(int i)
    {
        index = i;
    }

    public int getIndex()
    {
        return index;
    }

    private static final WhiteListState[] BY_INDEX = { WhiteList, BlackList };

    public static WhiteListState fromIndex(int index)
    {
        return BY_INDEX[index];
    }

    public static int[] toIntArray(WhiteListState[] states)
    {
        int[] a = new int[states.length];
        for (int i = 0; i < states.length; ++i)
            a[i] = states[i].getIndex();
        return a;
    }

    public static WhiteListState[] fromIntArray(int[] a)
    {
        WhiteListState[] states = new WhiteListState[a.length];
        for (int i = 0; i < a.length; ++i)
            states[i] = fromIndex(a[i]);
        return states;
    }

    public static CompoundTag write(CompoundTag nbt, WhiteListState state)
    {
        nbt.putInt(SwiftUtils.tagName("whiteListState"), state.getIndex());
        return nbt;
    }

    public static CompoundTag writeArray(CompoundTag nbt, WhiteListState[] states)
    {
        int[] i = WhiteListState.toIntArray(states);
        nbt.putIntArray(SwiftUtils.tagName("whiteListStates"), i);
        return nbt;
    }

    public static WhiteListState read(CompoundTag nbt)
    {
        int i = nbt.getInt(SwiftUtils.tagName("whiteListState"));
        return WhiteListState.fromIndex(i);
    }

    public static WhiteListState[] readArray(CompoundTag nbt)
    {
        int[] i = nbt.getIntArray(SwiftUtils.tagName("whiteListStates"));
        return WhiteListState.fromIntArray(i);
    }

    public static FriendlyByteBuf write(FriendlyByteBuf FriendlyByteBuf, WhiteListState state)
    {
        FriendlyByteBuf.writeVarInt(state.getIndex());
        return FriendlyByteBuf;
    }

    public static FriendlyByteBuf writeArray(FriendlyByteBuf FriendlyByteBuf, WhiteListState[] states)
    {
        FriendlyByteBuf.writeVarIntArray(WhiteListState.toIntArray(states));
        return FriendlyByteBuf;
    }

    public static WhiteListState read(FriendlyByteBuf FriendlyByteBuf)
    {
        return WhiteListState.fromIndex(FriendlyByteBuf.readVarInt());
    }

    public static WhiteListState[] readArray(FriendlyByteBuf FriendlyByteBuf)
    {
        return WhiteListState.fromIntArray(FriendlyByteBuf.readVarIntArray());
    }
}
