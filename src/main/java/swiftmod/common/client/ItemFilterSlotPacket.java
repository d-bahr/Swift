package swiftmod.common.client;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import swiftmod.common.BigItemStack;

public class ItemFilterSlotPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, ItemFilterSlotPacket packet);
    }

    public ItemFilterSlotPacket()
    {
        slot = 0;
        itemStack = new BigItemStack();
    }

    public ItemFilterSlotPacket(int s, BigItemStack stack)
    {
        slot = s;
        itemStack = stack;
    }

    public ItemFilterSlotPacket(PacketBuffer buffer)
    {
        itemStack = new BigItemStack();
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        super.decode(buffer);
        slot = buffer.readInt();
        itemStack.read(buffer);
    }

    public void encode(PacketBuffer buffer)
    {
        super.encode(buffer);
        buffer.writeInt(slot);
        itemStack.write(buffer);
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
        channel.registerMessage(PacketIDs.ItemFilterSlot.value(),
                ItemFilterSlotPacket.class, ItemFilterSlotPacket::encode,
                ItemFilterSlotPacket::new, ItemFilterSlotPacket::handle);
    }

    public int slot;
    public BigItemStack itemStack;
}
