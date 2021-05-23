package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.RedstoneControl;

public class RedstoneControlConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, RedstoneControlConfigurationPacket packet);
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

    public RedstoneControlConfigurationPacket(PacketBuffer buffer)
    {
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        redstoneControl = RedstoneControl.fromIndex(buffer.readInt());
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeInt(redstoneControl.getIndex());
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
        channel.registerMessage(PacketIDs.RedstoneControlConfiguration.value(),
                RedstoneControlConfigurationPacket.class, RedstoneControlConfigurationPacket::encode,
                RedstoneControlConfigurationPacket::new, RedstoneControlConfigurationPacket::handle);
    }

    public RedstoneControl redstoneControl;
}
