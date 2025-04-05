package swiftmod.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TileEntityBase extends BlockEntity
{
    protected TileEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider)
    {
    	loadAdditional(pkt.getTag(), provider); // read from the nbt in the packet
    }

    /*
     * Creates a tag containing all of the BlockEntity information, used by vanilla to transmit from
     * server to client
     */
    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
    	CompoundTag tag = super.getUpdateTag(provider);
        write(provider, tag);
        return tag;
    }

    /*
     * Populates this BlockEntity with information from the tag, used by vanilla to transmit from server
     * to client
     */
    @Override
    public final void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
    	loadAdditional(nbt, provider);
    }

    @Override
    public final void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        read(provider, nbt);
    }

    @Override
    public final void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
    	super.saveAdditional(nbt, provider);
    	write(provider, nbt);
    }

    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
    }

    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
    }
    
    public BlockPos getNeighborPos(Direction dir)
    {
    	return worldPosition.relative(dir);
    }
    
    public BlockEntity getNeighborEntity(Direction dir)
    {
    	return level.getBlockEntity(getNeighborPos(dir));
    }
    
    public boolean hasRedstoneSignal()
    {
    	return level.hasNeighborSignal(worldPosition);
    }
    
    @Override
    public int hashCode()
    {
    	return worldPosition.hashCode();
    }
}
