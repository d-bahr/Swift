package swiftmod.common.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SlotConfigurationPacket extends Packet
{
    public interface Handler
    {
        public void handle(ServerPlayerEntity player, SlotConfigurationPacket packet);
    }

    public SlotConfigurationPacket()
    {
        slots = new ArrayList<Integer>();
        enable = false;
    }

    public SlotConfigurationPacket(int slot, boolean enable)
    {
        slots = new ArrayList<Integer>();
        slots.add(slot);
        this.enable = enable;
    }

    public SlotConfigurationPacket(List<Integer> slots, boolean enable)
    {
        this.slots = slots;
        this.enable = enable;
    }

    public SlotConfigurationPacket(PacketBuffer buffer)
    {
        decode(buffer);
    }

    public void decode(PacketBuffer buffer)
    {
        int[] vals = buffer.readVarIntArray();
        slots = new ArrayList<Integer>(vals.length);
        for (int i = 0; i < vals.length; ++i)
            slots.add(vals[i]);
        enable = buffer.readBoolean();
    }

    public void encode(PacketBuffer buffer)
    {
        int[] vals = new int[slots.size()];
        for (int i = 0; i < slots.size(); ++i)
            vals[i] = slots.get(i);
        buffer.writeVarIntArray(vals);
        buffer.writeBoolean(enable);
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
        channel.registerMessage(PacketIDs.SlotConfiguration.value(),
                SlotConfigurationPacket.class, SlotConfigurationPacket::encode,
                SlotConfigurationPacket::new, SlotConfigurationPacket::handle);
    }

    public List<Integer> slots;
    public boolean enable;
}
