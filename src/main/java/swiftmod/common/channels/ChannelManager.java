package swiftmod.common.channels;

import java.util.function.BiFunction;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public abstract class ChannelManager<T extends ChannelData> extends SavedData
{
    @FunctionalInterface
    public interface Supplier<T extends ChannelData>
    {
        T create(ChannelSpec spec);
    };
    
    @FunctionalInterface
    public interface Decoder<T extends ChannelData>
    {
        T decode(CompoundTag nbt, ChannelSpec spec);
    };

    public ChannelManager(String id, Supplier<T> supplier, Decoder<T> decoder)
    {
    	m_id = id;
        m_channelSupplier = supplier;
        m_channelDecoder = decoder;
    }

    public void save()
    {
        setDirty();
    }

    protected abstract void load(HolderLookup.Provider provider, CompoundTag nbt);

    protected String getId()
    {
    	return m_id;
    }

    protected T create(ChannelSpec spec)
    {
        return m_channelSupplier.create(spec);
    }

    protected T decode(CompoundTag nbt, ChannelSpec spec)
    {
        return m_channelDecoder.decode(nbt, spec);
    }

    public static <U extends ChannelManager<?>> U getManager(BiFunction<CompoundTag, HolderLookup.Provider, U> loader,
    		java.util.function.Supplier<U> supplier, String name)
    {
        return getManager(Level.OVERWORLD, loader, supplier, name);
    }

    public static <U extends ChannelManager<?>> U getManager(Level world, BiFunction<CompoundTag, HolderLookup.Provider, U> loader,
    		java.util.function.Supplier<U> supplier, String name)
    {
        if (world.isClientSide)
            throw new RuntimeException("Server-side operation called from client");

        return getManager(world.dimension(), loader, supplier, name);
    }

    public static <U extends ChannelManager<?>> U getManager(ResourceKey<Level> world, BiFunction<CompoundTag, HolderLookup.Provider, U> loader,
    		java.util.function.Supplier<U> supplier, String name)
    {
        DimensionDataStorage storage = ServerLifecycleHooks.getCurrentServer().getLevel(world).getDataStorage();
        return storage.computeIfAbsent(new Factory<U>(supplier, loader), name);
    }

    private String m_id;
    private Supplier<T> m_channelSupplier;
    private Decoder<T> m_channelDecoder;
}
