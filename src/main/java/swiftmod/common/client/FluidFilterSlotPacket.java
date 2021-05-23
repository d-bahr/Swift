package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class FluidFilterSlotPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, FluidFilterSlotPacket packet);
    }

    public FluidFilterSlotPacket()
    {
        slot = 0;
        fluidStack = FluidStack.EMPTY;
    }

    public FluidFilterSlotPacket(int s, FluidStack stack)
    {
        slot = s;
        fluidStack = stack;
    }

    public FluidFilterSlotPacket(PacketBuffer buffer)
    {
        fluidStack = FluidStack.EMPTY;
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        slot = buffer.readInt();
        fluidStack = FluidStack.readFromPacket(buffer);
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeInt(slot);
        fluidStack.writeToPacket(buffer);
    }

    public void process(ServerPlayerEntity player)
    {
        if (player.containerMenu instanceof Handler)
        {
            Handler handler = (Handler) player.containerMenu;
            handler.handle(player, this);
        }
    }

    public static void register(SimpleChannel channel)
    {
        channel.registerMessage(PacketIDs.FluidFilterSlot.value(),
                FluidFilterSlotPacket.class, FluidFilterSlotPacket::encode,
                FluidFilterSlotPacket::new, FluidFilterSlotPacket::handle);
    }

    public int slot;
    public FluidStack fluidStack;
}
