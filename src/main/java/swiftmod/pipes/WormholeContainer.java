package swiftmod.pipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import swiftmod.common.IDataCacheContainer;
import swiftmod.common.SwiftContainers;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.Channel;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelOwner;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.ChannelType;
import swiftmod.common.client.ChannelConfigurationPacket;
import swiftmod.common.client.ChannelConfigurationPacket.Type;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.pipes.networks.PipeChannelNetwork;

public class WormholeContainer extends AbstractContainerMenu
	implements ChannelConfigurationPacket.Handler, IDataCacheContainer<ChannelConfigurationDataCache>
{
    @FunctionalInterface
    public interface ChannelChangedCallback
    {
        void onChannelChanged(Type type, Channel<ChannelData> channel);
    }
    
    protected WormholeContainer(int windowID)
    {
        super(SwiftContainers.s_wormholeContainerType.get(), windowID);
        m_privateChannels = null;
        m_publicChannels = null;
        m_cache = new ChannelConfigurationDataCache();
        m_channelChangedCallback = null;
    }

    protected WormholeContainer(int windowID, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_wormholeContainerType.get(), windowID);
        m_privateChannels = new ArrayList<TreeSet<String>>(ChannelType.NUM_TYPES);
        m_publicChannels = new ArrayList<TreeSet<String>>(ChannelType.NUM_TYPES);
    	for (int i = 0; i < ChannelType.NUM_TYPES; ++i)
    	{
    		m_privateChannels.add(new TreeSet<String>());
    		m_publicChannels.add(new TreeSet<String>());
    	}
        
        m_cache = new ChannelConfigurationDataCache();
        decode(extraData);
        m_channelChangedCallback = null;
    }

    protected WormholeContainer(int windowID,
    		ChannelConfigurationDataCache cache,
    		ChannelChangedCallback callback)
    {
        super(SwiftContainers.s_wormholeContainerType.get(), windowID);
        m_privateChannels = null;
        m_publicChannels = null;
        m_cache = cache;
        m_channelChangedCallback = callback;
    }

    public static WormholeContainer createContainerServerSide(int windowID, ChannelConfigurationDataCache cache,
    		ChannelChangedCallback callback)
    {
        return new WormholeContainer(windowID, cache, callback);
    }

    public static WormholeContainer createContainerClientSide(int windowID, RegistryFriendlyByteBuf extraData)
    {
        return new WormholeContainer(windowID, extraData);
    }
    
    public void setChannelChangedCallback(ChannelChangedCallback callback)
    {
    	m_channelChangedCallback = callback;
    }

    @Override
    public ChannelConfigurationDataCache getCache()
    {
    	return m_cache;
    }

    public ArrayList<TreeSet<String>> getPrivateChannels()
    {
    	return m_privateChannels;
    }

    public TreeSet<String> getPrivateChannels(int index)
    {
    	return m_privateChannels.get(index);
    }

    public ArrayList<TreeSet<String>> getPublicChannels()
    {
    	return m_publicChannels;
    }

    public TreeSet<String> getPublicChannels(int index)
    {
    	return m_publicChannels.get(index);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    public void addChannel(ChannelSpec spec)
    {
    	int index = spec.type.getIndex();
    	if (spec.owner.isPrivate())
    		m_privateChannels.get(index).add(spec.name);
    	else
    		m_publicChannels.get(index).add(spec.name);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Add);
    }

    public void deleteChannel(ChannelSpec spec)
    {
    	int index = spec.type.getIndex();
    	if (spec.owner.isPrivate())
    		m_privateChannels.get(index).remove(spec.name);
    	else
    		m_publicChannels.get(index).remove(spec.name);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Delete);
    }

    public void setChannel(ChannelSpec spec)
    {
        m_cache.setChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Set);
    }

    public void unsetChannel(ChannelType type)
    {
        m_cache.clearChannel(type);
        sendUpdatePacket(new ChannelSpec(type), ChannelConfigurationPacket.Type.Unset);
    }

    private void sendUpdatePacket(ChannelSpec spec, ChannelConfigurationPacket.Type type)
    {
        ChannelConfigurationPacket updatePacket = new ChannelConfigurationPacket(spec, type);
        PacketDistributor.sendToServer(updatePacket);
    }

    public static void encode(ChannelConfigurationDataCache cache, Player player, FriendlyByteBuf buffer)
    {
    	for (int i = 0; i < ChannelType.NUM_TYPES; ++i)
    	{
    		ChannelType t = ChannelType.fromIndex(i);
	        HashMap<String, PipeChannelNetwork> privateChannels = BaseChannelManager.getManager().get(t, new ChannelOwner(player.getUUID()));
	        HashMap<String, PipeChannelNetwork> publicChannels = BaseChannelManager.getManager().get(t, ChannelOwner.Public);
	
	        if (privateChannels != null)
	        {
	            buffer.writeInt(privateChannels.size());
	        	privateChannels.forEach((key, value) -> buffer.writeUtf(key));
	        }
	        else
	        {
	            buffer.writeInt(0);
	        }

	        if (publicChannels != null)
	        {
	            buffer.writeInt(publicChannels.size());
	            publicChannels.forEach((key, value) -> buffer.writeUtf(key));
	        }
	        else
	        {
	            buffer.writeInt(0);
	        }
    	}
        
        cache.write(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
    	for (int i = 0; i < ChannelType.NUM_TYPES; ++i)
    	{
    		int len = buffer.readInt();
    		TreeSet<String> list = m_privateChannels.get(i);
    		list.clear();
    		for (int j = 0; j < len; ++j)
    			list.add(buffer.readUtf());
    		
    		len = buffer.readInt();
    		list = m_publicChannels.get(i);
    		list.clear();
    		for (int j = 0; j < len; ++j)
    			list.add(buffer.readUtf());
    	}
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayer player, ChannelConfigurationPacket packet)
    {
    	if (m_channelChangedCallback != null)
    		m_channelChangedCallback.onChannelChanged(packet.type, packet.channel);
    }

	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return null;
	}

    public static WormholeContainer createContainerServerSide(int windowID)
    {
        return new WormholeContainer(windowID);
    }

    public static WormholeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new WormholeContainer(windowID, extraData);
    }

    protected ArrayList<TreeSet<String>> m_privateChannels; // Only used on client side.
    protected ArrayList<TreeSet<String>> m_publicChannels; // Only used on client side.
    protected ChannelConfigurationDataCache m_cache;
    protected ChannelChangedCallback m_channelChangedCallback;
}
