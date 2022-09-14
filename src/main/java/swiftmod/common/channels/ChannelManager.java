package swiftmod.common.channels;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class ChannelManager<T extends ChannelData> extends SavedData
{
    @FunctionalInterface
    public interface Decoder<T extends ChannelData>
    {
        T decode(CompoundTag nbt);
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

    protected abstract void load(CompoundTag nbt);

    protected String getId()
    {
    	return m_id;
    }

    protected T create()
    {
        return m_channelSupplier.get();
    }

    protected T decode(CompoundTag nbt)
    {
        return m_channelDecoder.decode(nbt);
    }

    public static <T extends ChannelManager<?>> T getManager(Function<CompoundTag, T> loader, Supplier<T> supplier, String name)
    {
        return getManager(Level.OVERWORLD, loader, supplier, name);
    }

    public static <T extends ChannelManager<?>> T getManager(Level world, Function<CompoundTag, T> loader, Supplier<T> supplier, String name)
    {
        if (world.isClientSide)
            throw new RuntimeException("Server-side operation called from client");

        return getManager(world.dimension(), loader, supplier, name);
    }

    public static <T extends ChannelManager<?>> T getManager(ResourceKey<Level> world, Function<CompoundTag, T> loader, Supplier<T> supplier, String name)
    {
        DimensionDataStorage storage = ServerLifecycleHooks.getCurrentServer().getLevel(world).getDataStorage();
        return storage.computeIfAbsent(loader, supplier, name);
    }

    private String m_id;
    private Supplier<T> m_channelSupplier;
    private Decoder<T> m_channelDecoder;
}
