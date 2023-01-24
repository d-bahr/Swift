package swiftmod.pipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
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

    public void serialize(FriendlyByteBuf buffer, NeighboringItems items)
    {
        write(buffer);
        items.serialize(buffer);
    }

    public NeighboringItems deserialize(FriendlyByteBuf buffer)
    {
        read(buffer);
        return NeighboringItems.deserialize(buffer);
    }

    public void write(CompoundTag nbt, boolean writeChannelData)
    {
        RedstoneControl.writeArray(nbt, redstoneControls);
        TransferDirection.writeArray(nbt, transferDirections);
        channelConfiguration.write(nbt, writeChannelData);
    }

    public void write(CompoundTag nbt)
    {
        write(nbt, true);
    }

    public void read(CompoundTag nbt)
    {
        redstoneControls = RedstoneControl.readArray(nbt);
        transferDirections = TransferDirection.readArray(nbt);
        channelConfiguration.read(nbt);
    }

    public void write(FriendlyByteBuf buffer)
    {
        RedstoneControl.writeArray(buffer, redstoneControls);
        TransferDirection.writeArray(buffer, transferDirections);
        channelConfiguration.write(buffer);
    }

    public void read(FriendlyByteBuf buffer)
    {
        redstoneControls = RedstoneControl.readArray(buffer);
        transferDirections = TransferDirection.readArray(buffer);
        channelConfiguration.read(buffer);
    }

    public void writeTransferDirection(FriendlyByteBuf buffer, Direction direction)
    {
        TransferDirection.write(buffer, transferDirections[SwiftUtils.dirToIndex(direction)]);
    }

    public void readTransferDirection(FriendlyByteBuf buffer, Direction direction)
    {
        transferDirections[SwiftUtils.dirToIndex(direction)] = TransferDirection.read(buffer);
    }

    public void writeRedstoneControl(FriendlyByteBuf buffer, Direction direction)
    {
        RedstoneControl.write(buffer, redstoneControls[SwiftUtils.dirToIndex(direction)]);
    }

    public void readRedstoneControl(FriendlyByteBuf buffer, Direction direction)
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
