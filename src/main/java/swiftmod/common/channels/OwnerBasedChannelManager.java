package swiftmod.common.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import swiftmod.common.SwiftUtils;

public class OwnerBasedChannelManager<T extends ChannelData> extends ChannelManager<T>
{
    public OwnerBasedChannelManager(String name, Supplier<T> supplier, Decoder<T> decoder)
    {
        super(name, supplier, decoder);
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
                attachments.add(spec.name, attachment);
            m_blocks.put(attachment, spec);
        }
    }

    public void attach(ChannelSpec spec, TileEntity te)
    {
        attach(spec, new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    public void reattach(ChannelSpec spec, ChannelAttachment attachment)
    {
        detach(attachment);
        attach(spec, attachment);
    }

    public void reattach(ChannelSpec spec, TileEntity te)
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

    public void detach(TileEntity te)
    {
        detach(new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    public void detach(ChannelSpec spec, ChannelAttachment attachment)
    {
        m_blocks.remove(attachment);
        detachWorker(spec, attachment);
    }

    public void detach(ChannelSpec spec, TileEntity te)
    {
        detach(spec, new ChannelAttachment(te.getLevel(), te.getBlockPos()));
    }

    private void detachWorker(ChannelSpec spec, ChannelAttachment attachment)
    {
        OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
        if (attachments != null)
            attachments.remove(spec.name, attachment);
    }

    public Set<ChannelAttachment> getAttached(ChannelSpec spec)
    {
        OwnedChannelAttachments attachments = m_attachments.get(spec.owner);
        if (attachments == null)
            return new HashSet<ChannelAttachment>();
        else
            return attachments.getOrEmpty(spec.name);
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        clear();
        CompoundNBT parent = nbt.getCompound(getId());
        ListNBT list = parent.getList(SwiftUtils.tagName("channels"), Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            CompoundNBT tag = list.getCompound(i);
            ChannelSpec spec = new ChannelSpec(tag);
            T data = decode(tag);
            put(spec, data);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        CompoundNBT parent = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (HashMap.Entry<ChannelOwner, OwnedChannels<T>> entry1 : m_channels.entrySet())
        {
            OwnedChannels<T> x = entry1.getValue();
            for (HashMap.Entry<String, T> entry2 : x.entrySet())
            {
                CompoundNBT tag = new CompoundNBT();
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
            HashSet<ChannelAttachment> a = attachments.remove(spec.name);
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
