package swiftmod.common.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftUtils;

public class OwnerBasedChannelManager<T extends ChannelData> extends ChannelManager<T>
{
    public OwnerBasedChannelManager(String id, Supplier<T> supplier, Decoder<T> decoder)
    {
    	super(id, supplier, decoder);

        m_channels = new HashMap<ChannelOwner, OwnedChannels<T>>();
        m_attachments = new HashMap<ChannelOwner, OwnedChannelAttachments>();
        m_blocks = new HashMap<ChannelAttachment, ChannelSpec>();
    }

    public void clear()
    {
        m_channels.clear();
        m_attachments.clear();
        m_blocks.clear();
    }

    public void attach(ChannelSpec spec, ChannelAttachment attachment)
    {
        if (exists(spec))
        {
            OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
            if (attachments != null)
                attachments.add(spec.getKey(), attachment);
            m_blocks.put(attachment, spec);
        }
    }

    public void attach(ChannelSpec spec, BlockEntity te)
    {
        attach(spec, new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    public void reattach(ChannelSpec spec, ChannelAttachment attachment)
    {
        detach(attachment);
        attach(spec, attachment);
    }

    public void reattach(ChannelSpec spec, BlockEntity te)
    {
        ChannelAttachment attachment = new ChannelAttachment(te.getLevel(), te.getBlockPos());
        detach(attachment);
        attach(spec, attachment);
    }

    public void detach(ChannelAttachment attachment)
    {
        ChannelSpec spec = m_blocks.remove(attachment);
        if (spec != null)
            detachWorker(spec, attachment);
    }

    public void detach(BlockEntity te)
    {
        detach(new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    public void detach(ChannelSpec spec, ChannelAttachment attachment)
    {
        m_blocks.remove(attachment);
        detachWorker(spec, attachment);
    }

    public void detach(ChannelSpec spec, BlockEntity te)
    {
        detach(spec, new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    private void detachWorker(ChannelSpec spec, ChannelAttachment attachment)
    {
        OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
        if (attachments != null)
            attachments.remove(spec.getKey(), attachment);
    }

    public Set<ChannelAttachment> getAttached(ChannelSpec spec)
    {
        OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
        if (attachments == null)
            return new HashSet<ChannelAttachment>();
        else
            return attachments.getOrEmpty(spec.getKey());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        clear();
        CompoundTag parent = nbt.getCompound(getId());
        ListTag list = parent.getList(SwiftUtils.tagName("channels"), Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            CompoundTag tag = list.getCompound(i);
            ChannelSpec spec = new ChannelSpec(tag);
            T data = decode(tag);
            put(spec, data);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        CompoundTag parent = new CompoundTag();
        ListTag list = new ListTag();
        for (HashMap.Entry<ChannelOwner, OwnedChannels<T>> entry1 : m_channels.entrySet())
        {
            OwnedChannels<T> x = entry1.getValue();
            for (HashMap.Entry<String, T> entry2 : x.entrySet())
            {
                CompoundTag tag = new CompoundTag();
                ChannelSpec spec = new ChannelSpec(entry1.getKey(), entry2.getKey());
                spec.write(tag);
                entry2.getValue().write(tag);
                list.add(tag);
            }
        }
        parent.put(SwiftUtils.tagName("channels"), list);
        nbt.put(getId(), parent);
        return nbt;
    }

    public void put(Channel<T> channel)
    {
        put(channel.spec, channel.data);
    }

    public void put(ChannelSpec spec, T data)
    {
        OwnedChannels<T> ownedChannels = get(spec.owner);
        if (ownedChannels == null)
        {
            ownedChannels = new OwnedChannels<T>();
            m_channels.put(spec.owner, ownedChannels);
            m_attachments.put(spec.owner, new OwnedChannelAttachments());
        }
        ownedChannels.put(spec.name, data);
    }

    public T add(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.owner);
        if (ownedChannels == null)
        {
            ownedChannels = new OwnedChannels<T>();
            m_channels.put(spec.owner, ownedChannels);
            m_attachments.put(spec.owner, new OwnedChannelAttachments());
            T t = create();
            ownedChannels.put(spec.name, t);
            return t;
        }
        else
        {
            T existing = ownedChannels.get(spec.name);
            if (existing != null)
            {
                return existing;
            }
            else
            {
                T t = create();
                ownedChannels.put(spec.name, t);
                return t;
            }
        }
    }

    public OwnedChannels<T> get(ChannelOwner owner)
    {
        return m_channels.get(owner);
    }

    public T get(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.owner);
        if (ownedChannels == null)
            return null;
        else
            return ownedChannels.get(spec.name);
    }

    public boolean exists(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.owner);
        if (ownedChannels == null)
            return false;
        else
            return ownedChannels.containsKey(spec.name);
    }

    public OwnedChannels<T> delete(ChannelOwner owner)
    {
        OwnedChannelAttachments attachments = m_attachments.remove(owner);
        for (HashSet<ChannelAttachment> a : attachments.values())
        {
            for (ChannelAttachment attachment : a)
                m_blocks.remove(attachment);
        }
        return m_channels.remove(owner);
    }

    public T delete(ChannelSpec spec)
    {
        OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
        if (attachments != null)
        {
            HashSet<ChannelAttachment> a = attachments.remove(spec.getKey());
            if (a != null)
            {
                for (ChannelAttachment attachment : a)
                    m_blocks.remove(attachment);
            }
        }
        OwnedChannels<T> ownedChannels = m_channels.get(spec.owner);
        if (ownedChannels == null)
            return null;
        else
            return ownedChannels.remove(spec.name);
    }

    private HashMap<ChannelOwner, OwnedChannels<T>> m_channels;
    private HashMap<ChannelOwner, OwnedChannelAttachments> m_attachments;
    private HashMap<ChannelAttachment, ChannelSpec> m_blocks;
}
