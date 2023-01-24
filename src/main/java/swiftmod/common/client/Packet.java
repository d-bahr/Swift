package swiftmod.common.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public abstract class Packet
{
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        LogicalSide sideReceived = context.getDirection().getReceptionSide();
        context.setPacketHandled(true);

        if (sideReceived != LogicalSide.SERVER)
            return;

        ServerPlayer player = context.getSender();

        // Dispatch from the network thread to the main server processing thread.
        context.enqueueWork(() -> process(player));
    }

    public abstract void decode(FriendlyByteBuf buffer);

    public abstract void encode(FriendlyByteBuf buffer);

    public abstract void process(ServerPlayer player);
}
