package swiftmod.common.client;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class DirectionalPacket extends Packet
{
    public DirectionalPacket(CustomPacketPayload.Type<? extends CustomPacketPayload> t)
    {
    	super(t);
        direction = Direction.NORTH;
    }

    public DirectionalPacket(CustomPacketPayload.Type<? extends CustomPacketPayload> t, Direction dir)
    {
    	super(t);
        direction = dir;
    }
    
    public Direction getDirection()
    {
    	return direction;
    }

    public Direction direction;
}
