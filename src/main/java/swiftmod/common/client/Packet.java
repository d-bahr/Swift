package swiftmod.common.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class Packet
{
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        LogicalSide sideReceived = context.getDirection().getReceptionSide();
        context.setPacketHandled(true);

        if (sideReceived != LogicalSide.SERVER)
            return;

        ServerPlayerEntity player = context.getSender();

        // Dispatch from the network thread to the main server processing thread.
        context.enqueueWork(() -> process(player));
    }

    public abstract void decode(PacketBuffer buffer);

    public abstract void encode(PacketBuffer buffer);

    public abstract void process(ServerPlayerEntity player);
}
