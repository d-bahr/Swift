package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import swiftmod.common.Swift;
import swiftmod.common.channels.Channel;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.ChannelType;

public class ChannelConfigurationPacket extends Packet
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ChannelConfigurationPacket packet);
    }

    public enum Type
    {
        Add(0),
        Delete(1),
        Set(2),
        Unset(3);
        
        private static final java.util.function.IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::ordinal);

        private static final Type[] BY_INDEX = { Add, Delete, Set, Unset};

        private int value;

        private Type(int v)
        {
            value = v;
        }
        
        public int toInt()
        {
            return value;
        }
        
        public static Type fromInt(int v)
        {
            return BY_INDEX[v];
        }
    };

    public ChannelConfigurationPacket(ChannelType t)
    {
        this(new Channel<ChannelData>(t, ChannelData::new), Type.Unset);
    }

    public ChannelConfigurationPacket(Channel<ChannelData> ch)
    {
        this(ch, Type.Add);
    }

    public ChannelConfigurationPacket(Channel<ChannelData> ch, Type t)
    {
    	super(TYPE);
        type = t;
        channel = ch;
    }

    public ChannelConfigurationPacket(ChannelSpec spec, Type t)
    {
        this(new Channel<ChannelData>(spec, ChannelData::new), t);
    }
    
    public Type getType()
    {
    	return type;
    }
    
    public Channel<ChannelData> getChannel()
    {
    	return channel;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }

    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, ChannelConfigurationPacket::handle);
    }

    public Type type;
    public Channel<ChannelData> channel;
    
    public static final CustomPacketPayload.Type<ChannelConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "channel_cfg"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChannelConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(Channel.makeStreamCodec(), ChannelConfigurationPacket::getChannel,
    				Type.STREAM_CODEC, ChannelConfigurationPacket::getType,
    				ChannelConfigurationPacket::new);
}
