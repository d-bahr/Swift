package swiftmod.common.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import swiftmod.common.ContainerBase;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.client.SideConfigurationPacket;

public class SideUpgradeContainer extends ContainerBase<SideUpgradeDataCache> implements SideConfigurationPacket.Handler
{
    protected SideUpgradeContainer(int windowID, Inventory playerInventory)
    {
        super(SwiftContainers.s_sideUpgradeContainerType, windowID, playerInventory, 8, 107);
    }

    protected SideUpgradeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_sideUpgradeContainerType, windowID, new SideUpgradeDataCache(), playerInventory, 8,
                107);
        decode(extraData);
    }

    @Override
    public boolean stillValid(Player player)
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

    public static void encode(Player player, ItemStack heldItem, FriendlyByteBuf buffer)
    {
        buffer.writeItemStack(heldItem, false);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        m_cache.read(buffer);
    }

    @Override
    public void handle(ServerPlayer player, SideConfigurationPacket packet)
    {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() == SwiftItems.s_sideUpgradeItem)
        {
            SideUpgradeDataCache.setStates(packet.directionStates, itemStack);
        }
    }

    public static SideUpgradeContainer createContainerServerSide(int windowID, Inventory playerInventory,
            Player playerEntity)
    {
        return new SideUpgradeContainer(windowID, playerInventory);
    }

    public static SideUpgradeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new SideUpgradeContainer(windowID, playerInventory, extraData);
    }
}
