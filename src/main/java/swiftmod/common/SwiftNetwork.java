package swiftmod.common;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import swiftmod.common.client.*;

public class SwiftNetwork
{
    public static void registerPackets(RegisterPayloadHandlersEvent event)
    {
    	if (registrar == null)
    		registrar = event.registrar(CHANNEL_PROTOCOL_VERSION);
    	
        RedstoneControlConfigurationPacket.register(registrar);
        TransferDirectionConfigurationPacket.register(registrar);
        ColorConfigurationPacket.register(registrar);
        PriorityConfigurationPacket.register(registrar);
        ItemFilterConfigurationPacket.register(registrar);
        ItemFilterSlotPacket.register(registrar);
        ItemClearFilterPacket.register(registrar);
        ItemWildcardFilterPacket.register(registrar);
        FluidFilterConfigurationPacket.register(registrar);
        FluidFilterSlotPacket.register(registrar);
        FluidClearFilterPacket.register(registrar);
        FluidWildcardFilterPacket.register(registrar);
        ChannelConfigurationPacket.register(registrar);
        SlotConfigurationPacket.register(registrar);
    }

    public static PayloadRegistrar registrar = null;
    private static String CHANNEL_PROTOCOL_VERSION = "4";
}
