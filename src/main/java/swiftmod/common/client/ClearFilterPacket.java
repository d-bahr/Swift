package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ClearFilterPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, ClearFilterPacket packet);
    }

    public ClearFilterPacket()
    {
        super();
    }

    public ClearFilterPacket(PacketBuffer buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
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
        channel.registerMessage(PacketIDs.ClearFilter.value(),
                ClearFilterPacket.class, ClearFilterPacket::encode,
                ClearFilterPacket::new, ClearFilterPacket::handle);
    }
}
