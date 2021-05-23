package swiftmod.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.client.*;

public class SwiftNetwork
{
    public static void registerPackets()
    {
        if (mainChannel == null)
        {
            mainChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Swift.MOD_NAME, CHANNEL_NAME),
                    () -> CHANNEL_PROTOCOL_VERSION, CHANNEL_PROTOCOL_VERSION::equals, CHANNEL_PROTOCOL_VERSION::equals);
        }

        ChannelConfigurationPacket.register(mainChannel);
        ClearFilterPacket.register(mainChannel);
        ItemFilterConfigurationPacket.register(mainChannel);
        ItemFilterSlotPacket.register(mainChannel);
        RedstoneControlConfigurationPacket.register(mainChannel);
        TransferDirectionConfigurationPacket.register(mainChannel);
        SideConfigurationPacket.register(mainChannel);
        SlotConfigurationPacket.register(mainChannel);
        WildcardFilterPacket.register(mainChannel);
        FluidFilterConfigurationPacket.register(mainChannel);
        FluidFilterSlotPacket.register(mainChannel);
    }

    public static SimpleChannel mainChannel = null;
    private static String CHANNEL_NAME = "main";
    private static String CHANNEL_PROTOCOL_VERSION = "3";
}
