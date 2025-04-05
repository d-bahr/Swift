package swiftmod.common;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

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
    
    private static final java.util.function.IntFunction<RedstoneControl> BY_ID = ByIdMap.continuous(RedstoneControl::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    public static final Codec<RedstoneControl> CODEC = SwiftDataComponents.makeEnumCodec("r", RedstoneControl::getIndex, RedstoneControl::fromIndex);
    public static final StreamCodec<ByteBuf, RedstoneControl> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RedstoneControl::ordinal);

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

    public static CompoundTag write(CompoundTag nbt, RedstoneControl redstoneControl)
    {
        nbt.putInt(SwiftUtils.tagName("redstoneControl"), redstoneControl.getIndex());
        return nbt;
    }

    public static CompoundTag writeArray(CompoundTag nbt, RedstoneControl[] redstoneControls)
    {
        int[] i = RedstoneControl.toIntArray(redstoneControls);
        nbt.putIntArray(SwiftUtils.tagName("redstoneControls"), i);
        return nbt;
    }

    public static RedstoneControl read(CompoundTag nbt)
    {
        int i = nbt.getInt(SwiftUtils.tagName("redstoneControl"));
        return RedstoneControl.fromIndex(i);
    }

    public static RedstoneControl[] readArray(CompoundTag nbt)
    {
        int[] i = nbt.getIntArray(SwiftUtils.tagName("redstoneControls"));
        return RedstoneControl.fromIntArray(i);
    }

    public static FriendlyByteBuf write(FriendlyByteBuf FriendlyByteBuf, RedstoneControl redstoneControl)
    {
        FriendlyByteBuf.writeVarInt(redstoneControl.getIndex());
        return FriendlyByteBuf;
    }

    public static FriendlyByteBuf writeArray(FriendlyByteBuf FriendlyByteBuf, RedstoneControl[] redstoneControls)
    {
        FriendlyByteBuf.writeVarIntArray(RedstoneControl.toIntArray(redstoneControls));
        return FriendlyByteBuf;
    }

    public static RedstoneControl read(FriendlyByteBuf FriendlyByteBuf)
    {
        return RedstoneControl.fromIndex(FriendlyByteBuf.readVarInt());
    }

    public static RedstoneControl[] readArray(FriendlyByteBuf FriendlyByteBuf)
    {
        return RedstoneControl.fromIntArray(FriendlyByteBuf.readVarIntArray());
    }
}
