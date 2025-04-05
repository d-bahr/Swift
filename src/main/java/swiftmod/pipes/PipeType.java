package swiftmod.pipes;

import java.util.EnumSet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import swiftmod.common.channels.ChannelType;

public enum PipeType
{
	Item(0),
	Fluid(1),
	Energy(2);
	
	PipeType(int index)
	{
		m_index = index;
	}
    
    public class ChannelTypeConversion
    {
    	public ChannelTypeConversion()
    	{
    		hasConversion = false;
    		type = ChannelType.Items;
    	}
    	
    	public ChannelTypeConversion(boolean h, ChannelType t)
    	{
    		hasConversion = h;
    		type = t;
    	}
    	
    	public ChannelTypeConversion(ChannelType t)
    	{
    		hasConversion = true;
    		type = t;
    	}
    	
    	public boolean hasConversion;
    	public ChannelType type;
    }
    
    public ChannelTypeConversion tryGetChannelType()
    {
    	// Currently, all pipe types correspond to channels, but that may change
    	// in the future (e.g. with AE2 cables).
    	switch (m_index)
    	{
    	case 0:
    		return new ChannelTypeConversion(ChannelType.Items);
    	case 1:
    		return new ChannelTypeConversion(ChannelType.Fluids);
    	case 2:
    		return new ChannelTypeConversion(ChannelType.Energy);
    	default:
    		return new ChannelTypeConversion();
    	}
    }
    
    public boolean canConvertToChannel()
    {
    	// Currently, all pipe types correspond to channels, but that may change
    	// in the future (e.g. with AE2 cables).
    	switch (m_index)
    	{
    	case 0:
    	case 1:
    	case 2:
    		return true;
    	default:
    		return false;
    	}
    }
    
    public ChannelType toChannelType()
    {
    	// Note: all channel types must have a corresponding pipe type.
    	ChannelTypeConversion conversion = tryGetChannelType();
    	if (conversion.hasConversion)
    		return conversion.type;
    	else
    		throw new RuntimeException("Unexpected pipe type.");
    }
    
    public int getIndex()
    {
    	return m_index;
    }
    
    public static PipeType fromIndex(int index)
    {
    	return BY_INDEX[index];
    }
    
    private static final java.util.function.IntFunction<PipeType> BY_ID = ByIdMap.continuous(PipeType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    
    public static final StreamCodec<ByteBuf, PipeType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PipeType::ordinal);
	
	public static final EnumSet<PipeType> AllTypes = EnumSet.allOf(PipeType.class);

    private static final PipeType[] BY_INDEX = { Item, Fluid, Energy };
	
	public static int numTypes()
	{
		return AllTypes.size();
	}
	
	private int m_index;
}
