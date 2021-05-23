package swiftmod.common.client;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import swiftmod.common.SwiftUtils;

public abstract class DirectionalPacket extends Packet
{
    public DirectionalPacket()
    {
        direction = Direction.NORTH;
    }

    public DirectionalPacket(Direction dir)
    {
        direction = dir;
    }

    public void decode(PacketBuffer buffer)
    {
        direction = SwiftUtils.indexToDir(buffer.readInt());
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(SwiftUtils.dirToIndex(direction));
    }

    public Direction direction;
}
