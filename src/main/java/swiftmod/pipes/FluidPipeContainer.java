package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.client.ChannelConfigurationPacket;
import swiftmod.common.client.ClearFilterPacket;
import swiftmod.common.client.FluidFilterConfigurationPacket;
import swiftmod.common.client.FluidFilterSlotPacket;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.common.upgrades.TeleporterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;

public class FluidPipeContainer extends PipeContainer implements FluidFilterConfigurationPacket.Handler,
        FluidFilterSlotPacket.Handler, ClearFilterPacket.Handler, ChannelConfigurationPacket.Handler
{
    protected FluidPipeContainer(@Nullable ContainerType<?> type, int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier, sideUpgradeInventorySupplier, x, y);
    }

    protected FluidPipeContainer(@Nullable ContainerType<?> type, TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, tileEntity, windowID, playerInventory, cache, refreshFilterCallback, channelManagerCallback,
                upgradeInventory, sideUpgradeInventories, x, y);
    }

    public BasicFluidFilterUpgradeDataCache getBasicFilterCache(Direction direction)
    {
        BasicFluidFilterUpgradeDataCache cache = new BasicFluidFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }

    public void updateFilter(Direction direction, int slot, FluidStack fluidStack)
    {
        FluidFilterSlotPacket updatePacket = new FluidFilterSlotPacket();
        updatePacket.direction = direction;
        updatePacket.slot = slot;
        updatePacket.fluidStack = fluidStack;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void clearAllFilters(Direction direction)
    {
        ClearFilterPacket updatePacket = new ClearFilterPacket();
        updatePacket.direction = direction;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(FluidFilterConfigurationPacket updatePacket)
    {
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    @Override
    public void handle(ServerPlayerEntity player, FluidFilterConfigurationPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
            {
                BasicFluidFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, FluidFilterSlotPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
            {
                BasicFluidFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.fluidStack, itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, ClearFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem)
            {
                BasicFluidFilterUpgradeDataCache.clearAllFilters(itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, ChannelConfigurationPacket packet)
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack itemStack = m_baseUpgradeInventory.getItem(slot);
            if (itemStack.getItem() instanceof TeleporterUpgradeItem)
            {
                packet.channel.spec.tag = ChannelSpec.TAG_FLUIDS;
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
                    m_channelManagerCallback.manage(packet.channel.spec);
                    break;
                case Unset:
                    ChannelConfigurationDataCache.clearChannel(itemStack);
                    m_channelManagerCallback.manage(null);
                    break;
                }
            }
        }
    }
}
