package swiftmod.common;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SwiftDataComponents
{
	public static void registerDataComponents(IEventBus bus)
	{
		REGISTRAR.register(bus);
	}
	
	public static <T> Codec<T> makeEnumCodec(String name, Function<T, Integer> getter, Function<Integer, T> supplier)
	{
	    return RecordCodecBuilder.create(instance ->
	    instance.group(Codec.INT.fieldOf(name).forGetter(getter)).apply(instance, supplier));
	}
	
	private static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> makeBooleanDataComponent(String name)
	{
		return REGISTRAR.registerComponentType(
			    name,
			    builder -> builder
			        .persistent(Codec.BOOL)
			        .networkSynchronized(ByteBufCodecs.BOOL)
			);
	}
	
	private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> makeIntegerDataComponent(String name)
	{
		return REGISTRAR.registerComponentType(
			    name,
			    builder -> builder
			        .persistent(Codec.INT)
			        .networkSynchronized(ByteBufCodecs.INT)
			);
	}
	
	public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Swift.MOD_NAME);

	// General
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ImmutableFluidStack>> FLUID_STACK_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "fluid",
	    builder -> builder
	        .persistent(ImmutableFluidStack.CODEC)
	        .networkSynchronized(ImmutableFluidStack.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ImmutableFluidStack>>> FLUID_STACK_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "fluids",
	    builder -> builder
	        .persistent(ImmutableFluidStack.CODEC.listOf())
	        .networkSynchronized(ImmutableFluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()))
	);
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> ITEM_STACK_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "items",
	    builder -> builder
	        .persistent(ItemStack.CODEC.listOf())
	        .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()))
	);
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<BigItemStack>>> BIG_ITEM_STACK_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "big_items",
	    builder -> builder
	        .persistent(BigItemStack.CODEC.listOf())
	        .networkSynchronized(BigItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()))
	);
	
	// For CopyPastaItem
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneControl>> REDSTONE_CONTROL_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "redstone_ctl",
	    builder -> builder
	        .persistent(RedstoneControl.CODEC)
	        .networkSynchronized(RedstoneControl.STREAM_CODEC)
	);
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<RedstoneControl>>> REDSTONE_CONTROL_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "redstone_ctls",
	    builder -> builder
	        .persistent(RedstoneControl.CODEC.listOf())
	        .networkSynchronized(RedstoneControl.STREAM_CODEC.apply(ByteBufCodecs.list()))
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<TransferDirection>> TRANSFER_DIRECTION_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "tx_dir",
	    builder -> builder
	        .persistent(TransferDirection.CODEC)
	        .networkSynchronized(TransferDirection.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TransferDirection>>> TRANSFER_DIRECTION_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "tx_dirs",
	    builder -> builder
	        .persistent(TransferDirection.CODEC.listOf())
	        .networkSynchronized(TransferDirection.STREAM_CODEC.apply(ByteBufCodecs.list()))
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PRIORITY_DATA_COMPONENT = makeIntegerDataComponent("priority");

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> PRIORITY_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "priorities",
	    builder -> builder
        .persistent(Codec.INT.listOf())
        .networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list()))
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Color>> COLOR_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "color",
	    builder -> builder
	        .persistent(Color.CODEC)
	        .networkSynchronized(Color.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Color>>> COLOR_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "colors",
	    builder -> builder
	        .persistent(Color.CODEC.listOf())
	        .networkSynchronized(Color.STREAM_CODEC.apply(ByteBufCodecs.list()))
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Direction>> DIRECTION_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "dir",
	    builder -> builder
	        .persistent(Direction.CODEC)
	        .networkSynchronized(Direction.STREAM_CODEC)
	);

	// For BasicItemFilterUpgradeDataCache and BasicFluidFilterUpgradeDataCache
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<WhiteListState>> WHITELIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "whitelist",
	    builder -> builder
	        .persistent(WhiteListState.CODEC)
	        .networkSynchronized(WhiteListState.STREAM_CODEC)
	);
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_COUNT_DATA_COMPONENT = makeBooleanDataComponent("m_count");
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_DAMAGE_DATA_COMPONENT = makeBooleanDataComponent("m_damage");
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_MOD_DATA_COMPONENT = makeBooleanDataComponent("m_mod");
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_NBT_DATA_COMPONENT = makeBooleanDataComponent("m_nbt");
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_ORE_DICT_DATA_COMPONENT = makeBooleanDataComponent("m_oredict");
	
	// For WildcardFilter
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> WILDCARD_LIST_DATA_COMPONENT = REGISTRAR.registerComponentType(
	    "wildcards",
	    builder -> builder
	        .persistent(Codec.STRING.listOf())
	        .networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()))
	);
}
