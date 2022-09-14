package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraftforge.network.simple.SimpleChannel;
import swiftmod.common.TransferDirection;

public class TransferDirectionConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, TransferDirectionConfigurationPacket packet);
    }

    public TransferDirectionConfigurationPacket()
    {
        super();
        transferDirection = TransferDirection.Extract;
    }

    public TransferDirectionConfigurationPacket(Direction dir, TransferDirection td)
    {
        super(dir);
        transferDirection = td;
    }

    public TransferDirectionConfigurationPacket(FriendlyByteBuf buffer)
    {
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        transferDirection = TransferDirection.fromIndex(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeInt(transferDirection.getIndex());
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
        channel.registerMessage(PacketIDs.TransferDirectionConfiguration.value(),
                TransferDirectionConfigurationPacket.class, TransferDirectionConfigurationPacket::encode,
                TransferDirectionConfigurationPacket::new, TransferDirectionConfigurationPacket::handle);
    }

    public TransferDirection transferDirection;
}
