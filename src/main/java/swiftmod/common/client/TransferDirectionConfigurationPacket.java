package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.TransferDirection;

public class TransferDirectionConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, TransferDirectionConfigurationPacket packet);
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

    public TransferDirectionConfigurationPacket(PacketBuffer buffer)
    {
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        transferDirection = TransferDirection.fromIndex(buffer.readInt());
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeInt(transferDirection.getIndex());
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
        channel.registerMessage(PacketIDs.TransferDirectionConfiguration.value(),
                TransferDirectionConfigurationPacket.class, TransferDirectionConfigurationPacket::encode,
                TransferDirectionConfigurationPacket::new, TransferDirectionConfigurationPacket::handle);
    }

    public TransferDirection transferDirection;
}
