package swiftmod.common.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;

public class ItemClearFilterPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ItemClearFilterPacket packet);
    }

    public ItemClearFilterPacket()
    {
        super(TYPE);
    }

    public ItemClearFilterPacket(int index)
    {
        super(TYPE, index);
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, ItemClearFilterPacket::handle);
    }
    
    public static final CustomPacketPayload.Type<ItemClearFilterPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "item_clear"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemClearFilterPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, ItemClearFilterPacket::getIndex,
    				ItemClearFilterPacket::new);
}
