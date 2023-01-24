package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraftforge.network.simple.SimpleChannel;

public class SideConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, SideConfigurationPacket packet);
    }

    public SideConfigurationPacket()
    {
        super();
        directionStates = new byte[Direction.values().length];
        for (int i = 0; i < directionStates.length; ++i)
            directionStates[i] = 0;
    }

    public SideConfigurationPacket(byte[] b)
    {
        super();
        directionStates = b;
    }

    public SideConfigurationPacket(FriendlyByteBuf buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        directionStates = buffer.readByteArray();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeByteArray(directionStates);
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        {
            Handler handler = (Handler) player.containerMenu;
            handler.handle(player, this);
        }
    }

    public static void register(SimpleChannel channel)
    {
        channel.registerMessage(PacketIDs.SideConfiguration.value(),
                SideConfigurationPacket.class, SideConfigurationPacket::encode,
                SideConfigurationPacket::new, SideConfigurationPacket::handle);
    }

    public byte[] directionStates;
}
