package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.simple.SimpleChannel;
import swiftmod.common.WhiteListState;

public class FluidFilterConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, FluidFilterConfigurationPacket packet);
    }

    public FluidFilterConfigurationPacket()
    {
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchMod = false;
        matchOreDictionary = false;
    }

    public FluidFilterConfigurationPacket(WhiteListState state, boolean count, boolean mod, boolean oreDict)
    {
        whiteListState = state;
        matchCount = count;
        matchMod = mod;
        matchOreDictionary = oreDict;
    }

    public FluidFilterConfigurationPacket(FriendlyByteBuf buffer)
    {
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        whiteListState = WhiteListState.fromIndex(buffer.readInt());
        matchCount = buffer.readBoolean();
        matchMod = buffer.readBoolean();
        matchOreDictionary = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeInt(whiteListState.getIndex());
        buffer.writeBoolean(matchCount);
        buffer.writeBoolean(matchMod);
        buffer.writeBoolean(matchOreDictionary);
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        {
            Handler handler = (Handler) player.containerMenu;
            handler.handle(player, this);
        }
    }

    public static void register(SimpleChannel channel)
    {
        channel.registerMessage(PacketIDs.FluidFilterConfiguration.value(), FluidFilterConfigurationPacket.class,
                FluidFilterConfigurationPacket::encode, FluidFilterConfigurationPacket::new, FluidFilterConfigurationPacket::handle);
    }

    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchMod;
    public boolean matchOreDictionary;
}
