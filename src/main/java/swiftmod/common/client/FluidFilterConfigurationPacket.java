package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;

public class FluidFilterConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, FluidFilterConfigurationPacket packet);
    }

    public FluidFilterConfigurationPacket()
    {
    	super(TYPE);
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchMod = false;
        matchOreDictionary = false;
    }

    public FluidFilterConfigurationPacket(int index, WhiteListState state, boolean count, boolean mod, boolean oreDict)
    {
    	super(TYPE, index);
        whiteListState = state;
        matchCount = count;
        matchMod = mod;
        matchOreDictionary = oreDict;
    }
    
    public WhiteListState getWhiteListState()
    {
    	return whiteListState;
    }
    
    public boolean getMatchCount()
    {
    	return matchCount;
    }
    
    public boolean getMatchMod()
    {
    	return matchMod;
    }
    
    public boolean getMatchOreDictionary()
    {
    	return matchOreDictionary;
    }

    public void process(ServerPlayer player)
    {
        if (player.containerMenu instanceof Handler)
        	((Handler) player.containerMenu).handle(player, this);
    }
    
    public static void register(PayloadRegistrar registrar)
    {
    	registrar.playToServer(TYPE, STREAM_CODEC, FluidFilterConfigurationPacket::handle);
    }

    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchMod;
    public boolean matchOreDictionary;
    
    public static final CustomPacketPayload.Type<FluidFilterConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "fluid_cfg"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidFilterConfigurationPacket> STREAM_CODEC =
    		StreamCodec.composite(IndexingPacket.STREAM_CODEC, FluidFilterConfigurationPacket::getIndex,
    				WhiteListState.STREAM_CODEC, FluidFilterConfigurationPacket::getWhiteListState,
    				ByteBufCodecs.BOOL, FluidFilterConfigurationPacket::getMatchCount,
    				ByteBufCodecs.BOOL, FluidFilterConfigurationPacket::getMatchMod,
    				ByteBufCodecs.BOOL, FluidFilterConfigurationPacket::getMatchOreDictionary,
    				FluidFilterConfigurationPacket::new);
}
