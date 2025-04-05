package swiftmod.common.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class Packet implements CustomPacketPayload
{
    public Packet(CustomPacketPayload.Type<? extends CustomPacketPayload> t)
    {
    	m_type = t;
    }
    
    public static void handle(final Packet data, final IPayloadContext context)
    {
        Player p = context.player();

        if (!(p instanceof ServerPlayer))
            return;

        ServerPlayer player = (ServerPlayer)context.player();

        // Dispatch from the network thread to the main server processing thread.
        context.enqueueWork(() -> data.process(player));
    }

    public abstract void process(ServerPlayer player);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
    	return m_type;
    }
    
    private CustomPacketPayload.Type<? extends CustomPacketPayload> m_type;
}
