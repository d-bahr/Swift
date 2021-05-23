package swiftmod.common;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityBase<T extends DataCache> extends TileEntity
{
    protected TileEntityBase(TileEntityType<?> type, T cache)
    {
        super(type);
        m_cache = cache;
    }
    
    public T getCache()
    {
        return m_cache;
    }

    // When the world loads from disk, the server needs to send the TileEntity information to the client
    // it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
    // getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
    // getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk
    // update packet
    // Not really required for this example since we only use the timer on the client, but included
    // anyway for illustration
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbt = new CompoundNBT();
        save(nbt);
        return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        BlockState blockState = level.getBlockState(worldPosition);
        load(blockState, pkt.getTag()); // read from the nbt in the packet
    }

    /*
     * Creates a tag containing all of the TileEntity information, used by vanilla to transmit from
     * server to client
     */
    @Override
    public CompoundNBT getUpdateTag()
    {
        return save(new CompoundNBT());
    }

    /*
     * Populates this TileEntity with information from the tag, used by vanilla to transmit from server
     * to client
     */
    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT nbt)
    {
        load(blockState, nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        read(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        return write(super.save(nbt));
    }

    public void read(BlockState state, CompoundNBT nbt)
    {
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        return nbt;
    }

    protected final T m_cache;
}
