package swiftmod.common.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.core.HolderLookup;
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

        m_channels = new HashMap<ChannelOwnership, OwnedChannels<T>>();
        m_attachments = new HashMap<ChannelOwnership, OwnedChannelAttachments>();
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
            OwnedChannelAttachments attachments = m_attachments.get(new ChannelOwnership(spec));
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
        OwnedChannelAttachments attachments = m_attachments.get(new ChannelOwnership(spec));
        if (attachments != null)
            attachments.remove(spec.getKey(), attachment);
    }

    public Set<ChannelAttachment> getAttached(ChannelSpec spec)
    {
        OwnedChannelAttachments attachments = m_attachments.get(new ChannelOwnership(spec));
        if (attachments == null)
            return new HashSet<ChannelAttachment>();
        else
            return attachments.getOrEmpty(spec.getKey());
    }

    @Override
    public void load(HolderLookup.Provider provider, CompoundTag nbt)
    {
        clear();
        CompoundTag parent = nbt.getCompound(getId());
        ListTag list = parent.getList(SwiftUtils.tagName("channels"), Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            CompoundTag tag = list.getCompound(i);
            ChannelSpec spec = new ChannelSpec(tag);
            T data = decode(tag, spec);
            put(spec, data);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider)
    {
        CompoundTag parent = new CompoundTag();
        ListTag list = new ListTag();
        for (HashMap.Entry<ChannelOwnership, OwnedChannels<T>> entry1 : m_channels.entrySet())
        {
            OwnedChannels<T> x = entry1.getValue();
            for (HashMap.Entry<String, T> entry2 : x.entrySet())
            {
                CompoundTag tag = new CompoundTag();
                ChannelOwnership key = entry1.getKey();
                ChannelSpec spec = new ChannelSpec(key.type, key.owner, entry2.getKey());
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
        OwnedChannels<T> ownedChannels = get(spec.type, spec.owner);
        if (ownedChannels == null)
        {
            ownedChannels = new OwnedChannels<T>();
            m_channels.put(new ChannelOwnership(spec), ownedChannels);
            m_attachments.put(new ChannelOwnership(spec), new OwnedChannelAttachments());
        }
        ownedChannels.put(spec.name, data);
    }

    public boolean putIfAbsent(Channel<T> channel)
    {
    	return putIfAbsent(channel.spec, channel.data);
    }

    public boolean putIfAbsent(ChannelSpec spec, T data)
    {
        OwnedChannels<T> ownedChannels = get(spec.type, spec.owner);
        if (ownedChannels == null)
        {
            ownedChannels = new OwnedChannels<T>();
            m_channels.put(new ChannelOwnership(spec), ownedChannels);
            m_attachments.put(new ChannelOwnership(spec), new OwnedChannelAttachments());
        }
        return ownedChannels.putIfAbsent(spec.name, data) == null;
    }

    public T add(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.type, spec.owner);
        if (ownedChannels == null)
        {
            ownedChannels = new OwnedChannels<T>();
            m_channels.put(new ChannelOwnership(spec), ownedChannels);
            m_attachments.put(new ChannelOwnership(spec), new OwnedChannelAttachments());
            T t = create(spec);
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
                T t = create(spec);
                ownedChannels.put(spec.name, t);
                return t;
            }
        }
    }

    public OwnedChannels<T> get(ChannelType type, ChannelOwner owner)
    {
        return m_channels.get(new ChannelOwnership(type, owner));
    }

    public T get(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.type, spec.owner);
        if (ownedChannels == null)
            return null;
        else
            return ownedChannels.get(spec.name);
    }

    public boolean exists(ChannelSpec spec)
    {
        OwnedChannels<T> ownedChannels = get(spec.type, spec.owner);
        if (ownedChannels == null)
            return false;
        else
            return ownedChannels.containsKey(spec.name);
    }

    public OwnedChannels<T> delete(ChannelType type, ChannelOwner owner)
    {
    	ChannelOwnership key = new ChannelOwnership(type, owner);
        OwnedChannelAttachments attachments = m_attachments.remove(key);
        for (HashSet<ChannelAttachment> a : attachments.values())
        {
            for (ChannelAttachment attachment : a)
                m_blocks.remove(attachment);
        }
        return m_channels.remove(key);
    }

    public T delete(ChannelSpec spec)
    {
        OwnedChannelAttachments attachments = m_attachments.get(new ChannelOwnership(spec));
        if (attachments != null)
        {
            HashSet<ChannelAttachment> a = attachments.remove(spec.getKey());
            if (a != null)
            {
                for (ChannelAttachment attachment : a)
                    m_blocks.remove(attachment);
            }
        }
        OwnedChannels<T> ownedChannels = m_channels.get(new ChannelOwnership(spec));
        if (ownedChannels == null)
            return null;
        else
            return ownedChannels.remove(spec.name);
    }
    
    private class ChannelOwnership
    {
    	public ChannelOwnership(ChannelType t, ChannelOwner o)
    	{
    		type = t;
    		owner = o;
    	}

    	public ChannelOwnership(ChannelSpec spec)
    	{
    		type = spec.type;
    		owner = spec.owner;
    	}
    	
    	@Override
    	public int hashCode()
    	{
    		return Objects.hash(type, owner);
    	}

    	@Override
    	public boolean equals(Object o)
    	{
    		if (this == o)
    			return true;
    		else if (o == null || getClass() != o.getClass())
    			return false;
    		else
    		{
    			@SuppressWarnings("unchecked")
				ChannelOwnership other = (ChannelOwnership)o;
    			return type == other.type && owner.equals(other.owner);
    		}
    	}
    	
    	public ChannelType type;
    	public ChannelOwner owner;
    }

    private HashMap<ChannelOwnership, OwnedChannels<T>> m_channels;
    private HashMap<ChannelOwnership, OwnedChannelAttachments> m_attachments;
    private HashMap<ChannelAttachment, ChannelSpec> m_blocks;
}
