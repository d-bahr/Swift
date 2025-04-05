package swiftmod.common;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;

public enum Color
{
	// Note: The "transparent color is a very light gray to be distinguishable
	// from other grayscale colors in the case that the alpha is ignored.
    Transparent(0, "None",       FastColor.ARGB32.color(0x00, 0xB0, 0xB0, 0xB0)),
    Black(1,       "Black",      FastColor.ARGB32.color(0xFF, 0x1D, 0x1D, 0x21)),
    Gray(2,        "Gray",       FastColor.ARGB32.color(0xFF, 0x47, 0x4F, 0x52)),
    LightGray(3,   "Light Gray", FastColor.ARGB32.color(0xFF, 0x9D, 0x9D, 0x97)),
    White(4,       "White",      FastColor.ARGB32.color(0xFF, 0xF9, 0xFF, 0xFE)),
    Pink(5,        "Pink",       FastColor.ARGB32.color(0xFF, 0xF3, 0x8B, 0xAA)),
    Red(6,         "Red",        FastColor.ARGB32.color(0xFF, 0xB0, 0x2E, 0x26)),
    Orange(7,      "Orange",     FastColor.ARGB32.color(0xFF, 0xF9, 0x80, 0x1D)),
    Yellow(8,      "Yellow",     FastColor.ARGB32.color(0xFF, 0xFE, 0xD8, 0x3D)),
    Lime(9,        "Lime",       FastColor.ARGB32.color(0xFF, 0x80, 0xC7, 0x1F)),
    Green(10,      "Green",      FastColor.ARGB32.color(0xFF, 0x5E, 0x7C, 0x16)),
    Cyan(11,       "Cyan",       FastColor.ARGB32.color(0xFF, 0x16, 0x9C, 0x9C)),
    LightBlue(12,  "Light Blue", FastColor.ARGB32.color(0xFF, 0x3A, 0xB3, 0xDA)),
    Blue(13,       "Blue",       FastColor.ARGB32.color(0xFF, 0x3C, 0x44, 0xAA)),
    Magenta(14,    "Magenta",    FastColor.ARGB32.color(0xFF, 0xC7, 0x4E, 0xBD)),
    Purple(15,     "Purple",     FastColor.ARGB32.color(0xFF, 0x89, 0x32, 0xB8)),
    Brown(16,      "Brown",      FastColor.ARGB32.color(0xFF, 0x83, 0x54, 0x32));

    private int index;
    private String name;
    private int argb32;

    private Color(int i, String n, int c)
    {
        index = i;
        name = n;
        argb32 = c;
    }

    public int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return name;
    }
    
    public int argb()
    {
    	return argb32;
    }
    
    public Color next()
    {
    	return (index + 1 < numColors()) ? fromIndex(index + 1) : fromIndex(0);
    }
    
    public Color previous()
    {
    	return (index > 0) ? fromIndex(index - 1) : fromIndex(numColors() - 1);
    }
    
    private static final java.util.function.IntFunction<Color> BY_ID = ByIdMap.continuous(Color::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    public static final Codec<Color> CODEC = SwiftDataComponents.makeEnumCodec("c", Color::getIndex, Color::fromIndex);
    public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Color::ordinal);

    private static final Color[] BY_INDEX = { Transparent, Black, Gray, LightGray, White, Pink, Red, Orange,
            Yellow, Lime, Green, Cyan, LightBlue, Blue, Magenta, Purple, Brown };

    public static Color fromIndex(int index)
    {
        return BY_INDEX[index];
    }
    
    public static int numColors()
    {
    	return BY_INDEX.length;
    }

    public static byte[] toByteArray(Color[] c)
    {
    	byte[] a = new byte[c.length];
        for (int i = 0; i < c.length; ++i)
            a[i] = (byte)c[i].getIndex();
        return a;
    }

    public static Color[] fromByteArray(byte[] a)
    {
    	Color[] rc = new Color[a.length];
        for (int i = 0; i < a.length; ++i)
            rc[i] = fromIndex(a[i]);
        return rc;
    }

    public static CompoundTag write(CompoundTag nbt, Color c)
    {
        nbt.putByte(SwiftUtils.tagName("color"), (byte)c.getIndex());
        return nbt;
    }

    public static CompoundTag writeArray(CompoundTag nbt, Color[] c)
    {
        byte[] i = Color.toByteArray(c);
        nbt.putByteArray(SwiftUtils.tagName("colors"), i);
        return nbt;
    }

    public static Color read(CompoundTag nbt)
    {
        byte b = nbt.getByte(SwiftUtils.tagName("color"));
        return Color.fromIndex(b);
    }

    public static Color[] readArray(CompoundTag nbt)
    {
        byte[] b = nbt.getByteArray(SwiftUtils.tagName("colors"));
        return Color.fromByteArray(b);
    }

    public static FriendlyByteBuf write(FriendlyByteBuf FriendlyByteBuf, Color c)
    {
        FriendlyByteBuf.writeByte(c.getIndex());
        return FriendlyByteBuf;
    }

    public static FriendlyByteBuf writeArray(FriendlyByteBuf FriendlyByteBuf, Color[] c)
    {
        FriendlyByteBuf.writeByteArray(Color.toByteArray(c));
        return FriendlyByteBuf;
    }

    public static Color read(FriendlyByteBuf FriendlyByteBuf)
    {
        return Color.fromIndex(FriendlyByteBuf.readByte());
    }

    public static Color[] readArray(FriendlyByteBuf FriendlyByteBuf)
    {
        return Color.fromByteArray(FriendlyByteBuf.readByteArray());
    }
}
