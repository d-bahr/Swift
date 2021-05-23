package swiftmod.common.channels;

import java.util.function.Supplier;

import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public abstract class ChannelManager<T extends ChannelData> extends WorldSavedData
{
    @FunctionalInterface
    public interface Decoder<T extends ChannelData>
    {
        T decode(CompoundNBT nbt);
    };

    public ChannelManager(String name, Supplier<T> supplier, Decoder<T> decoder)
    {
        super(name);
        m_channelSupplier = supplier;
        m_channelDecoder = decoder;
    }

    public void save()
    {
        setDirty();
    }

    protected T create()
    {
        return m_channelSupplier.get();
    }

    protected T decode(CompoundNBT nbt)
    {
        return m_channelDecoder.decode(nbt);
    }

    public static <T extends ChannelManager<?>> T getManager(Supplier<T> supplier, String name)
    {
        return getManager(World.OVERWORLD, supplier, name);
    }

    public static <T extends ChannelManager<?>> T getManager(World world, Supplier<T> supplier, String name)
    {
        if (world.isClientSide)
            throw new RuntimeException("Server-side operation called from client");

        return getManager(world.dimension(), supplier, name);
    }

    public static <T extends ChannelManager<?>> T getManager(RegistryKey<World> world, Supplier<T> supplier, String name)
    {
        DimensionSavedDataManager storage = ServerLifecycleHooks.getCurrentServer().getLevel(world).getDataStorage();
        return storage.computeIfAbsent(supplier, name);
    }

    private Supplier<T> m_channelSupplier;
    private Decoder<T> m_channelDecoder;
}
