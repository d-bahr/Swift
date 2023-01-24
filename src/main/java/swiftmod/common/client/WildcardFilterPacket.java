package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.simple.SimpleChannel;

public class WildcardFilterPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, WildcardFilterPacket packet);
    }

    public WildcardFilterPacket()
    {
        super();
        filter = new String();
        add = false;
    }

    public WildcardFilterPacket(FriendlyByteBuf buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        add = buffer.readBoolean();
        filter = buffer.readUtf(32767);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeBoolean(add);
        buffer.writeUtf(filter);
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
        channel.registerMessage(PacketIDs.WildcardFilter.value(),
                WildcardFilterPacket.class, WildcardFilterPacket::encode,
                WildcardFilterPacket::new, WildcardFilterPacket::handle);
    }
    
    public String filter;
    public boolean add;
}
