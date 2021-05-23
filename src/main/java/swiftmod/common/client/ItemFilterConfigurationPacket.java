package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.WhiteListState;

public class ItemFilterConfigurationPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, ItemFilterConfigurationPacket packet);
    }

    public ItemFilterConfigurationPacket()
    {
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchDamage = false;
        matchMod = false;
        matchNBT = false;
        matchOreDictionary = false;
    }

    public ItemFilterConfigurationPacket(WhiteListState state, boolean count, boolean damage, boolean mod,
            boolean nbt, boolean oreDict)
    {
        whiteListState = state;
        matchCount = count;
        matchDamage = damage;
        matchMod = mod;
        matchNBT = nbt;
        matchOreDictionary = oreDict;
    }

    public ItemFilterConfigurationPacket(PacketBuffer buffer)
    {
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        whiteListState = WhiteListState.fromIndex(buffer.readInt());
        matchCount = buffer.readBoolean();
        matchDamage = buffer.readBoolean();
        matchMod = buffer.readBoolean();
        matchNBT = buffer.readBoolean();
        matchOreDictionary = buffer.readBoolean();
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeInt(whiteListState.getIndex());
        buffer.writeBoolean(matchCount);
        buffer.writeBoolean(matchDamage);
        buffer.writeBoolean(matchMod);
        buffer.writeBoolean(matchNBT);
        buffer.writeBoolean(matchOreDictionary);
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
        channel.registerMessage(PacketIDs.ItemFilterConfiguration.value(), ItemFilterConfigurationPacket.class,
                ItemFilterConfigurationPacket::encode, ItemFilterConfigurationPacket::new, ItemFilterConfigurationPacket::handle);
    }

    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchNBT;
    public boolean matchDamage;
    public boolean matchMod;
    public boolean matchOreDictionary;
}
