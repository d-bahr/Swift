package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraftforge.network.simple.SimpleChannel;
import swiftmod.common.RedstoneControl;

public class RedstoneControlConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, RedstoneControlConfigurationPacket packet);
    }

    public RedstoneControlConfigurationPacket()
    {
        super();
        redstoneControl = RedstoneControl.Disabled;
    }

    public RedstoneControlConfigurationPacket(Direction dir, RedstoneControl rc)
    {
        super(dir);
        redstoneControl = rc;
    }

    public RedstoneControlConfigurationPacket(FriendlyByteBuf buffer)
    {
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        redstoneControl = RedstoneControl.fromIndex(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeInt(redstoneControl.getIndex());
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
        channel.registerMessage(PacketIDs.RedstoneControlConfiguration.value(),
                RedstoneControlConfigurationPacket.class, RedstoneControlConfigurationPacket::encode,
                RedstoneControlConfigurationPacket::new, RedstoneControlConfigurationPacket::handle);
    }

    public RedstoneControl redstoneControl;
}
