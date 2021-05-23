package swiftmod.pipes;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import swiftmod.common.DataCache;
import swiftmod.common.NeighboringItems;
import swiftmod.common.RedstoneControl;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TransferDirection;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;

public class PipeDataCache implements DataCache
{
    public PipeDataCache()
    {
        int len = Direction.values().length;

        redstoneControls = new RedstoneControl[len];
        for (int i = 0; i < redstoneControls.length; ++i)
            redstoneControls[i] = RedstoneControl.Disabled;

        transferDirections = new TransferDirection[len];
        for (int i = 0; i < transferDirections.length; ++i)
            transferDirections[i] = TransferDirection.Extract;

        channelConfiguration = new ChannelConfigurationDataCache();
    }

    public void serialize(PacketBuffer buffer, NeighboringItems items)
    {
        write(buffer);
        items.serialize(buffer);
    }

    public NeighboringItems deserialize(PacketBuffer buffer)
    {
        read(buffer);
        return NeighboringItems.deserialize(buffer);
    }

    public CompoundNBT write(CompoundNBT nbt, boolean writeChannelData)
    {
        RedstoneControl.writeArray(nbt, redstoneControls);
        TransferDirection.writeArray(nbt, transferDirections);
        channelConfiguration.write(nbt, writeChannelData);
        return nbt;
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        return write(nbt, true);
    }

    public void read(CompoundNBT nbt)
    {
        redstoneControls = RedstoneControl.readArray(nbt);
        transferDirections = TransferDirection.readArray(nbt);
        channelConfiguration.read(nbt);
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        RedstoneControl.writeArray(buffer, redstoneControls);
        TransferDirection.writeArray(buffer, transferDirections);
        channelConfiguration.write(buffer);
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        redstoneControls = RedstoneControl.readArray(buffer);
        transferDirections = TransferDirection.readArray(buffer);
        channelConfiguration.read(buffer);
    }

    public PacketBuffer writeTransferDirection(PacketBuffer buffer, Direction direction)
    {
        TransferDirection.write(buffer, transferDirections[SwiftUtils.dirToIndex(direction)]);
        return buffer;
    }

    public void readTransferDirection(PacketBuffer buffer, Direction direction)
    {
        transferDirections[SwiftUtils.dirToIndex(direction)] = TransferDirection.read(buffer);
    }

    public PacketBuffer writeRedstoneControl(PacketBuffer buffer, Direction direction)
    {
        RedstoneControl.write(buffer, redstoneControls[SwiftUtils.dirToIndex(direction)]);
        return buffer;
    }

    public void readRedstoneControl(PacketBuffer buffer, Direction direction)
    {
        redstoneControls[SwiftUtils.dirToIndex(direction)] = RedstoneControl.read(buffer);
    }

    public RedstoneControl getRedstoneControl(Direction direction)
    {
        return redstoneControls[SwiftUtils.dirToIndex(direction)];
    }

    public void setRedstoneControl(Direction direction, RedstoneControl rc)
    {
        redstoneControls[SwiftUtils.dirToIndex(direction)] = rc;
    }

    public TransferDirection getTransferDirection(Direction direction)
    {
        return transferDirections[SwiftUtils.dirToIndex(direction)];
    }

    public void setTransferDirection(Direction direction, TransferDirection td)
    {
        transferDirections[SwiftUtils.dirToIndex(direction)] = td;
    }

    public RedstoneControl[] redstoneControls;
    public TransferDirection[] transferDirections;
    public ChannelConfigurationDataCache channelConfiguration;
}
