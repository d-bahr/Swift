package swiftmod.common.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.simple.SimpleChannel;

public class ClearFilterPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ClearFilterPacket packet);
    }

    public ClearFilterPacket()
    {
        super();
    }

    public ClearFilterPacket(FriendlyByteBuf buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
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
        channel.registerMessage(PacketIDs.ClearFilter.value(),
                ClearFilterPacket.class, ClearFilterPacket::encode,
                ClearFilterPacket::new, ClearFilterPacket::handle);
    }
}
