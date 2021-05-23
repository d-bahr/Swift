package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.IDataCacheContainer;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.WildcardFilterPacket;

public class WildcardFilterUpgradeContainer extends Container
        implements WildcardFilterPacket.Handler, IDataCacheContainer<WildcardFilterUpgradeDataCache>
{
    protected WildcardFilterUpgradeContainer(int windowID, PlayerInventory playerInventory)
    {
        super(SwiftContainers.s_wildcardFilterContainertype, windowID);
        m_cache = new WildcardFilterUpgradeDataCache();
    }

    protected WildcardFilterUpgradeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_wildcardFilterContainertype, windowID);
        m_cache = new WildcardFilterUpgradeDataCache();
        decode(extraData);
    }
    
    public WildcardFilterUpgradeDataCache getCache()
    {
        return m_cache;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    public ItemStack getHeldItem()
    {
        return m_cache.itemStack;
    }

    public void addFilter(String filter)
    {
        getCache().addFilter(filter);

        WildcardFilterPacket updatePacket = new WildcardFilterPacket();
        updatePacket.filter = filter;
        updatePacket.add = true;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void removeFilter(String filter)
    {
        getCache().removeFilter(filter);

        WildcardFilterPacket updatePacket = new WildcardFilterPacket();
        updatePacket.filter = filter;
        updatePacket.add = false;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public static void encode(PlayerEntity player, ItemStack heldItem, PacketBuffer buffer)
    {
        buffer.writeItemStack(heldItem, false);
    }

    public void decode(PacketBuffer buffer)
    {
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayerEntity player, WildcardFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem)
        {
            if (packet.add)
                WildcardFilterUpgradeDataCache.addFilter(packet.filter, itemStack);
            else
                WildcardFilterUpgradeDataCache.removeFilter(packet.filter, itemStack);
        }
    }

    public static WildcardFilterUpgradeContainer createContainerServerSide(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory);
    }

    public static WildcardFilterUpgradeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
    
    private WildcardFilterUpgradeDataCache m_cache;
}
