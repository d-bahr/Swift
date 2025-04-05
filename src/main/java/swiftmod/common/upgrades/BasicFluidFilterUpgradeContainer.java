package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.client.FluidClearFilterPacket;
import swiftmod.common.client.FluidFilterSlotPacket;
import swiftmod.common.client.FluidFilterConfigurationPacket;

public class BasicFluidFilterUpgradeContainer extends ContainerBase<BasicFluidFilterUpgradeDataCache>
        implements FluidFilterConfigurationPacket.Handler, FluidFilterSlotPacket.Handler, FluidClearFilterPacket.Handler
{

    protected BasicFluidFilterUpgradeContainer(int windowID, Inventory playerInventory)
    {
        super(SwiftContainers.s_basicFluidFilterContainerType.get(), windowID, playerInventory, 8, 107);
    }

    protected BasicFluidFilterUpgradeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicFluidFilterContainerType.get(), windowID, new BasicFluidFilterUpgradeDataCache(), playerInventory, 8, 107);
        decode(extraData);
    }

    public ItemStack getHeldItem()
    {
        return m_cache.itemStack;
    }

    public void updateFilter(int slot, FluidStack fluidStack)
    {
        getCache().setFilterSlot(slot, fluidStack);

        FluidFilterSlotPacket updatePacket = new FluidFilterSlotPacket();
        updatePacket.slot = slot;
        updatePacket.fluidStack = fluidStack;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void clearAllFilters()
    {
        m_cache.clearAllFilters();

        FluidClearFilterPacket updatePacket = new FluidClearFilterPacket();
        PacketDistributor.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(FluidFilterConfigurationPacket updatePacket)
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
    public void handle(ServerPlayer player, FluidFilterConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
        {
            BasicFluidFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayer player, FluidFilterSlotPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
        {
            BasicFluidFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.fluidStack, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayer player, FluidClearFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
        {
            BasicFluidFilterUpgradeDataCache.clearAllFilters(itemStack);
        }
    }

    public static BasicFluidFilterUpgradeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, Player playerEntity)
    {
        return new BasicFluidFilterUpgradeContainer(windowID, playerInventory);
    }

    public static BasicFluidFilterUpgradeContainer createContainerClientSide(int windowID,
            Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        return new BasicFluidFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
}
