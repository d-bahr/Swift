package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;

public class RedstoneControlConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, RedstoneControlConfigurationPacket packet);
    }

    public RedstoneControlConfigurationPacket()
    {
        super(TYPE);
        redstoneControl = RedstoneControl.Disabled;
    }

    public RedstoneControlConfigurationPacket(int index, RedstoneControl rc)
    {
        super(TYPE, index);
        redstoneControl = rc;
    }
    
    public RedstoneControl getRedstoneControl()
    {
    	return redstoneControl;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }

    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, RedstoneControlConfigurationPacket::handle);
    }

    public RedstoneControl redstoneControl;
    
    public static final CustomPacketPayload.Type<RedstoneControlConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "redstone_cfg"));
    public static final StreamCodec<ByteBuf, RedstoneControlConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, RedstoneControlConfigurationPacket::getIndex,
    				RedstoneControl.STREAM_CODEC, RedstoneControlConfigurationPacket::getRedstoneControl,
    				RedstoneControlConfigurationPacket::new);
}
