package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.IDataCacheContainer;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.client.ChannelConfigurationPacket;

public class TeleporterUpgradeContainer extends Container
        implements ChannelConfigurationPacket.Handler, IDataCacheContainer<ChannelConfigurationDataCache>
{
    protected TeleporterUpgradeContainer(int windowID)
    {
        super(SwiftContainers.s_teleporterUpgradeContainerType, windowID);
        m_cache = new ChannelConfigurationDataCache();
    }

    protected TeleporterUpgradeContainer(int windowID, PacketBuffer extraData)
    {
        super(SwiftContainers.s_teleporterUpgradeContainerType, windowID);
        m_cache = new ChannelConfigurationDataCache();
        decode(extraData);
    }

    @Override
    public ChannelConfigurationDataCache getCache()
    {
        return m_cache;
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    public void addChannel(ChannelSpec spec)
    {
        m_cache.addChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Add);
    }

    public void deleteChannel(ChannelSpec spec)
    {
        m_cache.deleteChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Delete);
    }

    public void setChannel(ChannelSpec spec)
    {
        m_cache.setChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Set);
    }

    public void unsetChannel()
    {
        m_cache.clearChannel();
        sendUpdatePacket(new ChannelSpec(), ChannelConfigurationPacket.Type.Unset);
    }

    private void sendUpdatePacket(ChannelSpec spec, ChannelConfigurationPacket.Type type)
    {
        ChannelConfigurationPacket updatePacket = new ChannelConfigurationPacket(spec, type);
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public static void encode(PlayerEntity player, ItemStack heldItem, PacketBuffer buffer)
    {
        ChannelConfigurationDataCache cache = ChannelConfigurationDataCache.create(BaseChannelManager.getManager(),
                player, heldItem);
        cache.write(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayerEntity player, ChannelConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof TeleporterUpgradeItem)
        {
            switch (packet.type)
            {
            case Add:
                {
                    BaseChannelManager manager = BaseChannelManager.getManager();
                    manager.put(packet.channel);
                    manager.save();
                }
                break;
            case Delete:
                {
                    BaseChannelManager manager = BaseChannelManager.getManager();
                    manager.delete(packet.channel.spec);
                    manager.save();
                }
                break;
            case Set:
                ChannelConfigurationDataCache.setChannel(itemStack, packet.channel.spec);
                break;
            case Unset:
                ChannelConfigurationDataCache.clearChannel(itemStack);
                break;
            }
        }
    }

    public static TeleporterUpgradeContainer createContainerServerSide(int windowID)
    {
        return new TeleporterUpgradeContainer(windowID);
    }

    public static TeleporterUpgradeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new TeleporterUpgradeContainer(windowID, extraData);
    }

    ChannelConfigurationDataCache m_cache;
}
