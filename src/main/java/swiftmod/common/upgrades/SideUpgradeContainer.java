package swiftmod.common.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.SideConfigurationPacket;

public class SideUpgradeContainer extends ContainerBase<SideUpgradeDataCache> implements SideConfigurationPacket.Handler
{
    protected SideUpgradeContainer(int windowID, PlayerInventory playerInventory)
    {
        super(SwiftContainers.s_sideUpgradeContainerType, windowID, playerInventory, 8, 107);
    }

    protected SideUpgradeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_sideUpgradeContainerType, windowID, new SideUpgradeDataCache(), playerInventory, 8,
                107);
        decode(extraData);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    public void sendUpdatePacketToServer(byte[] sides)
    {
        sendUpdatePacketToServer(new SideConfigurationPacket(sides));
    }

    public void sendUpdatePacketToServer(SideConfigurationPacket updatePacket)
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
    public void handle(ServerPlayerEntity player, SideConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_sideUpgradeItem)
        {
            SideUpgradeDataCache.setStates(packet.directionStates, itemStack);
        }
    }

    public static SideUpgradeContainer createContainerServerSide(int windowID, PlayerInventory playerInventory,
            PlayerEntity playerEntity)
    {
        return new SideUpgradeContainer(windowID, playerInventory);
    }

    public static SideUpgradeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new SideUpgradeContainer(windowID, playerInventory, extraData);
    }
}
