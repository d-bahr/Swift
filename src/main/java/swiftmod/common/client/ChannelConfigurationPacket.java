package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.channels.Channel;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelSpec;

public class ChannelConfigurationPacket extends Packet
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, ChannelConfigurationPacket packet);
    }

    public enum Type
    {
        Add(0),
        Delete(1),
        Set(2),
        Unset(3);

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

    public ChannelConfigurationPacket()
    {
        this(new Channel<ChannelData>(ChannelData::new), Type.Unset);
    }

    public ChannelConfigurationPacket(Channel<ChannelData> ch)
    {
        this(ch, Type.Add);
    }

    public ChannelConfigurationPacket(Channel<ChannelData> ch, Type t)
    {
        type = t;
        channel = ch;
    }

    public ChannelConfigurationPacket(ChannelSpec spec, Type t)
    {
        this(new Channel<ChannelData>(spec, ChannelData::new), t);
    }

    public ChannelConfigurationPacket(PacketBuffer buffer)
    {
        this();
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        type = Type.fromInt(buffer.readByte());
        channel.read(buffer);
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeByte((byte)type.toInt());
        channel.write(buffer);
    }

    public void process(ServerPlayerEntity player)
    {
        if (player.containerMenu instanceof Handler)
        {
            Handler handler = (Handler) player.containerMenu;
            handler.handle(player, this);
        }
    }

    public static void register(SimpleChannel channel)
    {
        channel.registerMessage(PacketIDs.ChannelConfiguration.value(),
                ChannelConfigurationPacket.class, ChannelConfigurationPacket::encode,
                ChannelConfigurationPacket::new, ChannelConfigurationPacket::handle);
    }

    public Type type;
    public Channel<ChannelData> channel;
}
