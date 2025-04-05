package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;

public class FluidWildcardFilterPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, FluidWildcardFilterPacket packet);
    }

    public FluidWildcardFilterPacket()
    {
        super(TYPE);
        filter = new String();
        add = false;
    }

    public FluidWildcardFilterPacket(int index, String f, boolean a)
    {
        super(TYPE, index);
        filter = f;
        add = a;
    }
    
    public String getFilter()
    {
    	return filter;
    }
    
    public boolean getAdd()
    {
    	return add;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, FluidWildcardFilterPacket::handle);
    }
    
    public String filter;
    public boolean add;
    
    public static final CustomPacketPayload.Type<FluidWildcardFilterPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "fluid_wild"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidWildcardFilterPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, FluidWildcardFilterPacket::getIndex,
    				ByteBufCodecs.STRING_UTF8, FluidWildcardFilterPacket::getFilter,
    				ByteBufCodecs.BOOL, FluidWildcardFilterPacket::getAdd,
    				FluidWildcardFilterPacket::new);
}
