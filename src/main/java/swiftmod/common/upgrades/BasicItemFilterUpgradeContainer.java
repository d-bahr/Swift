package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.BigItemStack;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.ClearFilterPacket;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.client.ItemFilterSlotPacket;

public class BasicItemFilterUpgradeContainer extends ContainerBase<BasicItemFilterUpgradeDataCache>
        implements ItemFilterConfigurationPacket.Handler, ItemFilterSlotPacket.Handler, ClearFilterPacket.Handler
{
    protected BasicItemFilterUpgradeContainer(int windowID, PlayerInventory playerInventory)
    {
        super(SwiftContainers.s_basicItemFilterContainerType, windowID, playerInventory, 8, 107);
    }

    protected BasicItemFilterUpgradeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_basicItemFilterContainerType, windowID, new BasicItemFilterUpgradeDataCache(), playerInventory, 8, 107);
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
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void clearAllFilters()
    {
        m_cache.clearAllFilters();

        ClearFilterPacket updatePacket = new ClearFilterPacket();
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(ItemFilterConfigurationPacket updatePacket)
    {
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
    public void handle(ServerPlayerEntity player, ItemFilterConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem)
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
    public void handle(ServerPlayerEntity player, ItemFilterSlotPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem)
        {
            BasicItemFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.itemStack, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, ClearFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem)
        {
            BasicItemFilterUpgradeDataCache.clearAllFilters(itemStack);
        }
    }

    public static BasicItemFilterUpgradeContainer createContainerServerSide(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new BasicItemFilterUpgradeContainer(windowID, playerInventory);
    }

    public static BasicItemFilterUpgradeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new BasicItemFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
}
