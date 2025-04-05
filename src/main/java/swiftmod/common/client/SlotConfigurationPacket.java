package swiftmod.common.client;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;

public class SlotConfigurationPacket extends Packet
{
    public interface Handler
    {
        public void handle(ServerPlayer player, SlotConfigurationPacket packet);
    }

    public SlotConfigurationPacket()
    {
    	super(TYPE);
        slots = new ArrayList<Integer>();
        enable = false;
    }

    public SlotConfigurationPacket(int slot, boolean enable)
    {
    	super(TYPE);
        slots = new ArrayList<Integer>();
        slots.add(slot);
        this.enable = enable;
    }

    public SlotConfigurationPacket(List<Integer> slots, boolean enable)
    {
    	super(TYPE);
        this.slots = slots;
        this.enable = enable;
    }
    
    public List<Integer> getSlots()
    {
    	return slots;
    }
    
    public boolean getEnable()
    {
    	return enable;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }

    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, SlotConfigurationPacket::handle);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
    	return TYPE;
    }

    public List<Integer> slots;
    public boolean enable;
    
    public static final CustomPacketPayload.Type<SlotConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "slot_cfg"));
    public static final StreamCodec<ByteBuf, SlotConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(ByteBufCodecs.INT.apply(ByteBufCodecs.list()), SlotConfigurationPacket::getSlots,
    				ByteBufCodecs.BOOL, SlotConfigurationPacket::getEnable,
    				SlotConfigurationPacket::new);
}
