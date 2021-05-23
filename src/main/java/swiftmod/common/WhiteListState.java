package swiftmod.common;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

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

    public static CompoundNBT write(CompoundNBT nbt, WhiteListState state)
    {
        nbt.putInt(SwiftUtils.tagName("whiteListState"), state.getIndex());
        return nbt;
    }

    public static CompoundNBT writeArray(CompoundNBT nbt, WhiteListState[] states)
    {
        int[] i = WhiteListState.toIntArray(states);
        nbt.putIntArray(SwiftUtils.tagName("whiteListStates"), i);
        return nbt;
    }

    public static WhiteListState read(CompoundNBT nbt)
    {
        int i = nbt.getInt(SwiftUtils.tagName("whiteListState"));
        return WhiteListState.fromIndex(i);
    }

    public static WhiteListState[] readArray(CompoundNBT nbt)
    {
        int[] i = nbt.getIntArray(SwiftUtils.tagName("whiteListStates"));
        return WhiteListState.fromIntArray(i);
    }

    public static PacketBuffer write(PacketBuffer packetBuffer, WhiteListState state)
    {
        packetBuffer.writeVarInt(state.getIndex());
        return packetBuffer;
    }

    public static PacketBuffer writeArray(PacketBuffer packetBuffer, WhiteListState[] states)
    {
        packetBuffer.writeVarIntArray(WhiteListState.toIntArray(states));
        return packetBuffer;
    }

    public static WhiteListState read(PacketBuffer packetBuffer)
    {
        return WhiteListState.fromIndex(packetBuffer.readVarInt());
    }

    public static WhiteListState[] readArray(PacketBuffer packetBuffer)
    {
        return WhiteListState.fromIntArray(packetBuffer.readVarIntArray());
    }
}
