package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import swiftmod.common.IDataCacheContainer;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.WildcardFilterPacket;

public class WildcardFilterUpgradeContainer extends AbstractContainerMenu implements WildcardFilterPacket.Handler, IDataCacheContainer<WildcardFilterUpgradeDataCache>
{
    protected WildcardFilterUpgradeContainer(int windowID, Inventory playerInventory)
    {
        super(SwiftContainers.s_wildcardFilterContainertype, windowID);
        m_cache = new WildcardFilterUpgradeDataCache();
    }

    protected WildcardFilterUpgradeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
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
    public boolean stillValid(Player playerIn)
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

    public static void encode(Player player, ItemStack heldItem, FriendlyByteBuf buffer)
    {
        buffer.writeItemStack(heldItem, false);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayer player, WildcardFilterPacket packet)
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

    public static WildcardFilterUpgradeContainer createContainerServerSide(int windowID, Inventory playerInventory, Player playerEntity)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory);
    }

    public static WildcardFilterUpgradeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
    
    private WildcardFilterUpgradeDataCache m_cache;
}
