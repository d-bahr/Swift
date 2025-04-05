package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;

public class PriorityConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, PriorityConfigurationPacket packet);
    }

    public PriorityConfigurationPacket()
    {
        super(TYPE);
        priority = 0;
    }

    public PriorityConfigurationPacket(int index, int p)
    {
        super(TYPE, index);
        priority = p;
    }
    
    public int getPriority()
    {
    	return priority;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, PriorityConfigurationPacket::handle);
    }

    public int priority;
    
    public static final CustomPacketPayload.Type<PriorityConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "priority"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PriorityConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, PriorityConfigurationPacket::getIndex,
    				ByteBufCodecs.INT, PriorityConfigurationPacket::getPriority,
    				PriorityConfigurationPacket::new);
}
