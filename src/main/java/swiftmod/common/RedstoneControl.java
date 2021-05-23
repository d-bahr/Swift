package swiftmod.common;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public enum RedstoneControl
{
    Disabled(0),
    Ignore(1),
    Normal(2),
    Inverted(3);

    private int index;

    private RedstoneControl(int i)
    {
        index = i;
    }

    public int getIndex()
    {
        return index;
    }

    private static final RedstoneControl[] BY_INDEX = { Disabled, Ignore, Normal, Inverted };

    public static RedstoneControl fromIndex(int index)
    {
        return BY_INDEX[index];
    }

    public static int[] toIntArray(RedstoneControl[] rc)
    {
        int[] a = new int[rc.length];
        for (int i = 0; i < rc.length; ++i)
            a[i] = rc[i].getIndex();
        return a;
    }

    public static RedstoneControl[] fromIntArray(int[] a)
    {
        RedstoneControl[] rc = new RedstoneControl[a.length];
        for (int i = 0; i < a.length; ++i)
            rc[i] = fromIndex(a[i]);
        return rc;
    }

    public static CompoundNBT write(CompoundNBT nbt, RedstoneControl redstoneControl)
    {
        nbt.putInt(SwiftUtils.tagName("redstoneControl"), redstoneControl.getIndex());
        return nbt;
    }

    public static CompoundNBT writeArray(CompoundNBT nbt, RedstoneControl[] redstoneControls)
    {
        int[] i = RedstoneControl.toIntArray(redstoneControls);
        nbt.putIntArray(SwiftUtils.tagName("redstoneControls"), i);
        return nbt;
    }

    public static RedstoneControl read(CompoundNBT nbt)
    {
        int i = nbt.getInt(SwiftUtils.tagName("redstoneControl"));
        return RedstoneControl.fromIndex(i);
    }

    public static RedstoneControl[] readArray(CompoundNBT nbt)
    {
        int[] i = nbt.getIntArray(SwiftUtils.tagName("redstoneControls"));
        return RedstoneControl.fromIntArray(i);
    }

    public static PacketBuffer write(PacketBuffer packetBuffer, RedstoneControl redstoneControl)
    {
        packetBuffer.writeVarInt(redstoneControl.getIndex());
        return packetBuffer;
    }

    public static PacketBuffer writeArray(PacketBuffer packetBuffer, RedstoneControl[] redstoneControls)
    {
        packetBuffer.writeVarIntArray(RedstoneControl.toIntArray(redstoneControls));
        return packetBuffer;
    }

    public static RedstoneControl read(PacketBuffer packetBuffer)
    {
        return RedstoneControl.fromIndex(packetBuffer.readVarInt());
    }

    public static RedstoneControl[] readArray(PacketBuffer packetBuffer)
    {
        return RedstoneControl.fromIntArray(packetBuffer.readVarIntArray());
    }
}
