package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.BigItemStack;
import swiftmod.common.Swift;

public class ItemFilterSlotPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ItemFilterSlotPacket packet);
    }

    public ItemFilterSlotPacket()
    {
    	super(TYPE);
        slot = 0;
        itemStack = new BigItemStack();
    }

    public ItemFilterSlotPacket(int index, int s, BigItemStack stack)
    {
    	super(TYPE, index);
        slot = s;
        itemStack = stack;
    }
    
    public int getSlot()
    {
    	return slot;
    }
    
    public BigItemStack getItemStack()
    {
    	return itemStack;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, ItemFilterSlotPacket::handle);
    }

    public int slot;
    public BigItemStack itemStack;
    
    public static final CustomPacketPayload.Type<ItemFilterSlotPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "item_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemFilterSlotPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, ItemFilterSlotPacket::getIndex,
    				ByteBufCodecs.VAR_INT, ItemFilterSlotPacket::getSlot,
    				BigItemStack.STREAM_CODEC, ItemFilterSlotPacket::getItemStack,
    				ItemFilterSlotPacket::new);
}
