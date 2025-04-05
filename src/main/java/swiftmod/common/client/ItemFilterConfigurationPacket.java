package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

import com.mojang.datafixers.util.Function7;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.Swift;
import swiftmod.common.WhiteListState;

public class ItemFilterConfigurationPacket extends IndexingPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ItemFilterConfigurationPacket packet);
    }

    public ItemFilterConfigurationPacket()
    {
    	super(TYPE);
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchDamage = false;
        matchMod = false;
        matchNBT = false;
        matchOreDictionary = false;
    }

    public ItemFilterConfigurationPacket(int index, WhiteListState state, boolean count, boolean damage, boolean mod,
            boolean nbt, boolean oreDict)
    {
    	super(TYPE, index);
        whiteListState = state;
        matchCount = count;
        matchDamage = damage;
        matchMod = mod;
        matchNBT = nbt;
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
    
    public boolean getMatchDamage()
    {
    	return matchDamage;
    }
    
    public boolean getMatchMod()
    {
    	return matchMod;
    }
    
    public boolean getMatchNBT()
    {
    	return matchNBT;
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
    	registrar.playToServer(TYPE, STREAM_CODEC, ItemFilterConfigurationPacket::handle);
    }

    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchDamage;
    public boolean matchMod;
    public boolean matchNBT;
    public boolean matchOreDictionary;
    
    // StreamCodec.composite doesn't support enough parameters so we have to do this garbage...
    // This is just a copy-paste from StreamCodec.
    private static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
        final StreamCodec<? super B, T1> codec1,
        final Function<C, T1> getter1,
        final StreamCodec<? super B, T2> codec2,
        final Function<C, T2> getter2,
        final StreamCodec<? super B, T3> codec3,
        final Function<C, T3> getter3,
        final StreamCodec<? super B, T4> codec4,
        final Function<C, T4> getter4,
        final StreamCodec<? super B, T5> codec5,
        final Function<C, T5> getter5,
        final StreamCodec<? super B, T6> codec6,
        final Function<C, T6> getter6,
        final StreamCodec<? super B, T7> codec7,
        final Function<C, T7> getter7,
        final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B p_330310_) {
                T1 t1 = codec1.decode(p_330310_);
                T2 t2 = codec2.decode(p_330310_);
                T3 t3 = codec3.decode(p_330310_);
                T4 t4 = codec4.decode(p_330310_);
                T5 t5 = codec5.decode(p_330310_);
                T6 t6 = codec6.decode(p_330310_);
                T7 t7 = codec7.decode(p_330310_);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B p_332052_, C p_331912_) {
                codec1.encode(p_332052_, getter1.apply(p_331912_));
                codec2.encode(p_332052_, getter2.apply(p_331912_));
                codec3.encode(p_332052_, getter3.apply(p_331912_));
                codec4.encode(p_332052_, getter4.apply(p_331912_));
                codec5.encode(p_332052_, getter5.apply(p_331912_));
                codec6.encode(p_332052_, getter6.apply(p_331912_));
                codec7.encode(p_332052_, getter7.apply(p_331912_));
            }
        };
    }
    
    public static final CustomPacketPayload.Type<ItemFilterConfigurationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Swift.MOD_NAME, "item_cfg"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemFilterConfigurationPacket> STREAM_CODEC =
    		composite(IndexingPacket.STREAM_CODEC, ItemFilterConfigurationPacket::getIndex,
    				WhiteListState.STREAM_CODEC, ItemFilterConfigurationPacket::getWhiteListState,
    				ByteBufCodecs.BOOL, ItemFilterConfigurationPacket::getMatchCount,
    				ByteBufCodecs.BOOL, ItemFilterConfigurationPacket::getMatchDamage,
    				ByteBufCodecs.BOOL, ItemFilterConfigurationPacket::getMatchMod,
    				ByteBufCodecs.BOOL, ItemFilterConfigurationPacket::getMatchNBT,
    				ByteBufCodecs.BOOL, ItemFilterConfigurationPacket::getMatchOreDictionary,
    				ItemFilterConfigurationPacket::new);
}
