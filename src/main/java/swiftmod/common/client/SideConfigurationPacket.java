package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SideConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, SideConfigurationPacket packet);
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

    public SideConfigurationPacket(PacketBuffer buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        directionStates = buffer.readByteArray();
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeByteArray(directionStates);
    }

    public void process(ServerPlayerEntity player)
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
