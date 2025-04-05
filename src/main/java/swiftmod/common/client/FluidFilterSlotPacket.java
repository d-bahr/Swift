package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;

public class FluidFilterSlotPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, FluidFilterSlotPacket packet);
    }

    public FluidFilterSlotPacket()
    {
    	super(TYPE);
        slot = 0;
        fluidStack = FluidStack.EMPTY;
    }

    public FluidFilterSlotPacket(int index, int s, FluidStack stack)
    {
    	super(TYPE, index);
        slot = s;
        fluidStack = stack;
    }
    
    public int getSlot()
    {
    	return slot;
    }
    
    public FluidStack getFluidStack()
    {
    	return fluidStack;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, FluidFilterSlotPacket::handle);
    }

    public int slot;
    public FluidStack fluidStack;
    
    public static final CustomPacketPayload.Type<FluidFilterSlotPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "fluid_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidFilterSlotPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, FluidFilterSlotPacket::getIndex,
    				ByteBufCodecs.VAR_INT, FluidFilterSlotPacket::getSlot,
    				FluidStack.OPTIONAL_STREAM_CODEC, FluidFilterSlotPacket::getFluidStack,
    				FluidFilterSlotPacket::new);
}
