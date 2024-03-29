package swiftmod.common.upgrades;

import java.util.HashMap;
import java.util.TreeSet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelOwner;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.OwnerBasedChannelManager;

public class ChannelConfigurationDataCache extends ItemStackDataCache
{
    public ChannelConfigurationDataCache()
    {
        super();
        privateChannels = new TreeSet<String>();
        publicChannels = new TreeSet<String>();
    }

    public ChannelConfigurationDataCache(ItemStack itemStack)
    {
        super(itemStack);
        privateChannels = new TreeSet<String>();
        publicChannels = new TreeSet<String>();
    }

    public void write(CompoundTag nbt, boolean serializeChannels)
    {
        super.write(nbt);

        if (serializeChannels && privateChannels != null)
        {
            ListTag list = new ListTag();
            for (String name : privateChannels)
                list.add(StringTag.valueOf(name));
            nbt.put(TAG_PRIVATE_CHANNELS, list);
        }
        else
        {
            nbt.put(TAG_PRIVATE_CHANNELS, new ListTag());
        }

        if (serializeChannels && publicChannels != null)
        {
            ListTag list = new ListTag();
            for (String name : publicChannels)
                list.add(StringTag.valueOf(name));
            nbt.put(TAG_PUBLIC_CHANNELS, list);
        }
        else
        {
            nbt.put(TAG_PUBLIC_CHANNELS, new ListTag());
        }
    }

    @Override
    public void write(CompoundTag nbt)
    {
        write(nbt, true);
    }

    public void read(CompoundTag nbt)
    {
        super.read(nbt);

        privateChannels.clear();
        publicChannels.clear();

        ListTag privList = nbt.getList(TAG_PRIVATE_CHANNELS, Tag.TAG_STRING);
        for (int i = 0; i < privList.size(); ++i)
            privateChannels.add(privList.getString(i));

        ListTag pubList = nbt.getList(TAG_PUBLIC_CHANNELS, Tag.TAG_STRING);
        for (int i = 0; i < pubList.size(); ++i)
            publicChannels.add(pubList.getString(i));
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
        super.write(buffer);

        if (privateChannels != null)
        {
            buffer.writeInt(privateChannels.size());
            for (String name : privateChannels)
                buffer.writeUtf(name);
        }
        else
        {
            buffer.writeInt(0);
        }

        if (publicChannels != null)
        {
            buffer.writeInt(publicChannels.size());
            for (String name : publicChannels)
                buffer.writeUtf(name);
        }
        else
        {
            buffer.writeInt(0);
        }
    }

    public void read(FriendlyByteBuf buffer)
    {
        super.read(buffer);

        privateChannels.clear();
        publicChannels.clear();

        int numPrivChannels = buffer.readInt();
        for (int i = 0; i < numPrivChannels; ++i)
            privateChannels.add(buffer.readUtf(32767));

        int numPubChannels = buffer.readInt();
        for (int i = 0; i < numPubChannels; ++i)
            publicChannels.add(buffer.readUtf(32767));
    }

    public void setChannel(ChannelSpec spec)
    {
        setChannel(itemStack, spec);
    }

    public static void setChannel(ItemStack itemStack, ChannelSpec spec)
    {
        CompoundTag nbt = itemStack.getOrCreateTagElement(TeleporterUpgradeItem.NBT_TAG);
        spec.write(nbt);
    }

    public void clearChannel()
    {
        clearChannel(itemStack);
    }

    public static void clearChannel(ItemStack itemStack)
    {
        itemStack.removeTagKey(TeleporterUpgradeItem.NBT_TAG);
    }

    public ChannelSpec getChannel()
    {
        return getChannel(itemStack);
    }

    public static ChannelSpec getChannel(ItemStack itemStack)
    {
        if (itemStack == null)
            return null;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return null;
        CompoundTag nbt = itemStack.getTagElement(TeleporterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return null;
        else
            return new ChannelSpec(nbt);
    }

    public void addChannel(ChannelSpec spec)
    {
        if (spec.owner.isPrivate())
            privateChannels.add(spec.name);
        else
            publicChannels.add(spec.name);
    }

    public void deleteChannel(ChannelSpec spec)
    {
        if (spec.owner.isPrivate())
            privateChannels.remove(spec.name);
        else
            publicChannels.remove(spec.name);
    }

    public void assignCurrentChannels(OwnerBasedChannelManager<ChannelData> manager, Player player)
    {
        HashMap<String, ChannelData> privateChannels = manager.get(new ChannelOwner(player.getUUID()));
        HashMap<String, ChannelData> publicChannels = manager.get(ChannelOwner.Public);

        this.privateChannels.clear();
        this.publicChannels.clear();

        if (privateChannels != null)
        {
            for (String name : privateChannels.keySet())
                this.privateChannels.add(name);
        }

        if (publicChannels != null)
        {
            for (String name : publicChannels.keySet())
                this.publicChannels.add(name);
        }
    }

    public static ChannelConfigurationDataCache create(OwnerBasedChannelManager<ChannelData> manager, Player player)
    {
        return create(manager, player, ItemStack.EMPTY);
    }

    public static ChannelConfigurationDataCache create(OwnerBasedChannelManager<ChannelData> manager, Player player, ItemStack stack)
    {
        ChannelConfigurationDataCache cache = new ChannelConfigurationDataCache();
        cache.itemStack = stack;
        cache.assignCurrentChannels(manager, player);
        return cache;
    }

    public TreeSet<String> privateChannels;
    public TreeSet<String> publicChannels;

    public static final String TAG_PRIVATE_CHANNELS = SwiftUtils.tagName("privateChannels");
    public static final String TAG_PUBLIC_CHANNELS = SwiftUtils.tagName("publicChannels");
}
