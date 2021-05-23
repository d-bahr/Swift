package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.ClearFilterPacket;
import swiftmod.common.client.FluidFilterSlotPacket;
import swiftmod.common.client.FluidFilterConfigurationPacket;

public class BasicFluidFilterUpgradeContainer extends ContainerBase<BasicFluidFilterUpgradeDataCache>
        implements FluidFilterConfigurationPacket.Handler, FluidFilterSlotPacket.Handler, ClearFilterPacket.Handler
{

    protected BasicFluidFilterUpgradeContainer(int windowID, PlayerInventory playerInventory)
    {
        super(SwiftContainers.s_basicFluidFilterContainerType, windowID, playerInventory, 8, 107);
    }

    protected BasicFluidFilterUpgradeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_basicFluidFilterContainerType, windowID, new BasicFluidFilterUpgradeDataCache(), playerInventory, 8, 107);
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
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void clearAllFilters()
    {
        m_cache.clearAllFilters();

        ClearFilterPacket updatePacket = new ClearFilterPacket();
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(FluidFilterConfigurationPacket updatePacket)
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
    public void handle(ServerPlayerEntity player, FluidFilterConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
        {
            BasicFluidFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
            BasicFluidFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, FluidFilterSlotPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
        {
            BasicFluidFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.fluidStack, itemStack);
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, ClearFilterPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
        {
            BasicFluidFilterUpgradeDataCache.clearAllFilters(itemStack);
        }
    }

    public static BasicFluidFilterUpgradeContainer createContainerServerSide(int windowID,
            PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new BasicFluidFilterUpgradeContainer(windowID, playerInventory);
    }

    public static BasicFluidFilterUpgradeContainer createContainerClientSide(int windowID,
            PlayerInventory playerInventory, PacketBuffer extraData)
    {
        return new BasicFluidFilterUpgradeContainer(windowID, playerInventory, extraData);
    }
}
