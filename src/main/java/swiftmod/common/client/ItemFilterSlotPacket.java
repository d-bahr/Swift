package swiftmod.common.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.simple.SimpleChannel;
import swiftmod.common.BigItemStack;

public class ItemFilterSlotPacket extends DirectionalPacket
{
    public interface Handler
    {
        public void handle(ServerPlayer player, ItemFilterSlotPacket packet);
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

    public ItemFilterSlotPacket(FriendlyByteBuf buffer)
    {
        itemStack = new BigItemStack();
        decode(buffer);
    }

    public void decode(FriendlyByteBuf buffer)
    {
        super.decode(buffer);
        slot = buffer.readInt();
        itemStack.read(buffer);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeInt(slot);
        itemStack.write(buffer);
    }

    public void process(ServerPlayer player)
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
