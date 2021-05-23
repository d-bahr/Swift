package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class WildcardFilterPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, WildcardFilterPacket packet);
    }

    public WildcardFilterPacket()
    {
        super();
        filter = new String();
        add = false;
    }

    public WildcardFilterPacket(PacketBuffer buffer)
    {
        super();
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        add = buffer.readBoolean();
        filter = buffer.readUtf();
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeBoolean(add);
        buffer.writeUtf(filter);
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
        channel.registerMessage(PacketIDs.WildcardFilter.value(),
                WildcardFilterPacket.class, WildcardFilterPacket::encode,
                WildcardFilterPacket::new, WildcardFilterPacket::handle);
    }
    
    public String filter;
    public boolean add;
}
