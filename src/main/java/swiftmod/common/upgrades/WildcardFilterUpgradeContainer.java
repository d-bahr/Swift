package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.IDataCacheContainer;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.client.ItemWildcardFilterPacket;

public class WildcardFilterUpgradeContainer extends AbstractContainerMenu
	// In this case using ItemWildcard vs FluidWildcard doesn't matter; we just pick one.
	implements ItemWildcardFilterPacket.Handler, IDataCacheContainer<WildcardFilterUpgradeDataCache>
{
    protected WildcardFilterUpgradeContainer(int windowID, Inventory playerInventory)
    {
        super(SwiftContainers.s_wildcardFilterContainerType.get(), windowID);
        m_cache = new WildcardFilterUpgradeDataCache();
    }

    protected WildcardFilterUpgradeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_wildcardFilterContainerType.get(), windowID);
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

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.filter = filter;
        updatePacket.add = true;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void removeFilter(String filter)
    {
        getCache().removeFilter(filter);

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.filter = filter;
        updatePacket.add = false;
        PacketDistributor.sendToServer(updatePacket);
    }

    public static void encode(Player player, ItemStack heldItem, RegistryFriendlyByteBuf buffer)
    {
    	ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, heldItem);
    }

    public void decode(RegistryFriendlyByteBuf buffer)
    {
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayer player, ItemWildcardFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem.get())
        {
            if (packet.add)
                WildcardFilterUpgradeDataCache.addFilter(packet.filter, itemStack);
            else
                WildcardFilterUpgradeDataCache.removeFilter(packet.filter, itemStack);
        }
    }

	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return null;
	}

    public static WildcardFilterUpgradeContainer createContainerServerSide(int windowID, Inventory playerInventory, Player playerEntity)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory);
    }

    public static WildcardFilterUpgradeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new WildcardFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
    
    private WildcardFilterUpgradeDataCache m_cache;
}
