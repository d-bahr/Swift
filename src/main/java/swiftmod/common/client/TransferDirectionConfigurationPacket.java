package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;

public class TransferDirectionConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, TransferDirectionConfigurationPacket packet);
    }

    public TransferDirectionConfigurationPacket()
    {
        super(TYPE);
        transferDirection = TransferDirection.Extract;
    }

    public TransferDirectionConfigurationPacket(int index, TransferDirection td)
    {
        super(TYPE, index);
        transferDirection = td;
    }
    
    public TransferDirection getTransferDirection()
    {
    	return transferDirection;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }

    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, TransferDirectionConfigurationPacket::handle);
    }

    public TransferDirection transferDirection;
    
    public static final CustomPacketPayload.Type<TransferDirectionConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "tx_dir_cfg"));
    public static final StreamCodec<ByteBuf, TransferDirectionConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, TransferDirectionConfigurationPacket::getIndex,
    				TransferDirection.STREAM_CODEC, TransferDirectionConfigurationPacket::getTransferDirection,
    				TransferDirectionConfigurationPacket::new);
}
