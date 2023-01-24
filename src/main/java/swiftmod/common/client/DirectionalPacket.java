package swiftmod.common.client;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
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

    public void decode(FriendlyByteBuf buffer)
    {
        direction = SwiftUtils.indexToDir(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(SwiftUtils.dirToIndex(direction));
    }

    public Direction direction;
}
