package swiftmod.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TileEntityBase<T extends DataCache> extends BlockEntity
{
    protected TileEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state, T cache)
    {
        super(type, pos, state);
        m_cache = cache;
    }
    
    public T getCache()
    {
        return m_cache;
    }

    // When the world loads from disk, the server needs to send the BlockEntity information to the client
    // it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
    // getUpdatePacket() and onDataPacket() are used for one-at-a-time BlockEntity updates
    // getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk
    // update packet
    // Not really required for this example since we only use the timer on the client, but included
    // anyway for illustration
    @Override
    public final Packet<ClientGamePacketListener> getUpdatePacket()
    {
    	// This will end up calling getUpdateTag(), which in turn calls save().
    	// TODO: Might need to call this instead:
    	// new ClientboundBlockEntityDataPacket(worldPosition, -1, getUpdateTag());
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        load(pkt.getTag()); // read from the nbt in the packet
    }

    /*
     * Creates a tag containing all of the BlockEntity information, used by vanilla to transmit from
     * server to client
     */
    @Override
    public final CompoundTag getUpdateTag()
    {
    	CompoundTag tag = super.getUpdateTag();
        write(tag);
        return tag;
    }

    /*
     * Populates this BlockEntity with information from the tag, used by vanilla to transmit from server
     * to client
     */
    @Override
    public final void handleUpdateTag(CompoundTag nbt)
    {
        load(nbt);
    }

    @Override
    public final void load(CompoundTag nbt)
    {
        super.load(nbt);
        read(nbt);
    }

    @Override
    public final void saveAdditional(CompoundTag nbt)
    {
    	super.saveAdditional(nbt);
    	write(nbt);
    }

    public void read(CompoundTag nbt)
    {
    }

    public void write(CompoundTag nbt)
    {
    }

    protected final T m_cache;
}
