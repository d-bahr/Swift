package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.BigItemStack;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.client.ItemClearFilterPacket;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.client.ItemFilterSlotPacket;

public class BasicItemFilterUpgradeContainer extends ContainerBase<BasicItemFilterUpgradeDataCache>
        implements ItemFilterConfigurationPacket.Handler, ItemFilterSlotPacket.Handler, ItemClearFilterPacket.Handler
{
    protected BasicItemFilterUpgradeContainer(int windowID, Inventory playerInventory)
    {
        super(SwiftContainers.s_basicItemFilterContainerType.get(), windowID, playerInventory, 8, 107);
    }

    protected BasicItemFilterUpgradeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicItemFilterContainerType.get(), windowID, new BasicItemFilterUpgradeDataCache(), playerInventory, 8, 107);
        decode(extraData);
    }

    public ItemStack getHeldItem()
    {
        return m_cache.itemStack;
    }

    public void updateFilter(int slot, ItemStack itemStack, int quantity)
    {
        getCache().setFilterSlot(slot, itemStack, quantity);

        ItemFilterSlotPacket updatePacket = new ItemFilterSlotPacket();
        updatePacket.slot = slot;
        updatePacket.itemStack = new BigItemStack(itemStack, quantity);
        PacketDistributor.sendToServer(updatePacket);
    }

    public void clearAllFilters()
    {
        m_cache.clearAllFilters();

        ItemClearFilterPacket updatePacket = new ItemClearFilterPacket();
        PacketDistributor.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(ItemFilterConfigurationPacket updatePacket)
    {
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
    public void handle(ServerPlayer player, ItemFilterConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
        {
            BasicItemFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
            BasicItemFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
            BasicItemFilterUpgradeDataCache.setMatchDamage(packet.matchDamage, itemStack);
            BasicItemFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
            BasicItemFilterUpgradeDataCache.setMatchNBT(packet.matchNBT, itemStack);
            BasicItemFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemFilterSlotPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
        {
            BasicItemFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.itemStack, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemClearFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
        {
            BasicItemFilterUpgradeDataCache.clearAllFilters(itemStack);
        }
    }

    public static BasicItemFilterUpgradeContainer createContainerServerSide(int windowID, Inventory playerInventory, Player playerEntity)
    {
        return new BasicItemFilterUpgradeContainer(windowID, playerInventory);
    }

    public static BasicItemFilterUpgradeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new BasicItemFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
}
