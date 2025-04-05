package swiftmod.common.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class IndexingPacket extends Packet
{
    public IndexingPacket(CustomPacketPayload.Type<? extends CustomPacketPayload> t)
    {
    	super(t);
    	index = 0;
    }

    public IndexingPacket(CustomPacketPayload.Type<? extends CustomPacketPayload> t, int i)
    {
    	super(t);
    	index = i;
    }
    
    public int getIndex()
    {
    	return index;
    }

    public int index;
    
    public static final StreamCodec<ByteBuf, Integer> STREAM_CODEC = ByteBufCodecs.VAR_INT;
}
