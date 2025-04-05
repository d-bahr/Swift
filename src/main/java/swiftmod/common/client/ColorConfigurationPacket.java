package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Color;
import swiftmod.common.Swift;

public class ColorConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ColorConfigurationPacket packet);
    }

    public ColorConfigurationPacket()
    {
        super(TYPE);
        color = Color.Transparent;
    }

    public ColorConfigurationPacket(int index, Color c)
    {
        super(TYPE, index);
        color = c;
    }
    
    public Color getColor()
    {
    	return color;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }

    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, ColorConfigurationPacket::handle);
    }

    public Color color;
    
    public static final CustomPacketPayload.Type<ColorConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "color_cfg"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColorConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, ColorConfigurationPacket::getIndex,
    				Color.STREAM_CODEC, ColorConfigurationPacket::getColor,
    				ColorConfigurationPacket::new);
}
