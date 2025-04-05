package swiftmod.pipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.Direction;
import swiftmod.common.Color;
import swiftmod.common.DataCache;
import swiftmod.common.NeighboringItems;
import swiftmod.common.RedstoneControl;
import swiftmod.common.TransferDirection;

public class PipeDataCache implements DataCache
{
    @FunctionalInterface
    public interface TransferDirectionChangedCallback
    {
        void onChanged(int index, TransferDirection transferDir);
    };

    @FunctionalInterface
    public interface RedstoneControlChangedCallback
    {
        void onChanged(int index, RedstoneControl rcOld, RedstoneControl rcNew);
    };

    @FunctionalInterface
    public interface ColorChangedCallback
    {
        void onChanged(int index, Color cOld, Color cNew);
    };

    @FunctionalInterface
    public interface PriorityChangedCallback
    {
        void onChanged(int index, int priorityNew, int priorityOld);
    };
    
    public PipeDataCache()
    {
    	this(Direction.values().length);
    }
    
    public PipeDataCache(int numIndices)
    {
        redstoneControls = new RedstoneControl[numIndices];
        transferDirections = new TransferDirection[numIndices];
        colors = new Color[numIndices];
        priorities = new int[numIndices];
        
        for (int i = 0; i < numIndices; ++i)
        {
            redstoneControls[i] = RedstoneControl.Disabled;
            transferDirections[i] = TransferDirection.Extract;
        	colors[i] = Color.Transparent;
        	priorities[i] = 0;
        }
        
        m_transferDirectionChangedCallback = null;
        m_redstoneControlChangedCallback = null;
        m_colorChangedCallback = null;
        m_priorityChangedCallback = null;
    }
    
    public int getDataSize()
    {
    	return redstoneControls.length;
    }

    public void setTransferDirectionChangedCallback(TransferDirectionChangedCallback callback)
    {
    	m_transferDirectionChangedCallback = callback;
    }

    public void setRedstoneControlChangedCallback(RedstoneControlChangedCallback callback)
    {
    	m_redstoneControlChangedCallback = callback;
    }

    public void setColorChangedCallback(ColorChangedCallback callback)
    {
    	m_colorChangedCallback = callback;
    }

    public void setPriorityChangedCallback(PriorityChangedCallback callback)
    {
    	m_priorityChangedCallback = callback;
    }

    public void serialize(RegistryFriendlyByteBuf buffer, NeighboringItems items)
    {
        write(buffer);
        items.serialize(buffer);
    }

    public NeighboringItems deserialize(RegistryFriendlyByteBuf buffer)
    {
        read(buffer);
        return NeighboringItems.deserialize(buffer);
    }

    public void write(CompoundTag nbt)
    {
        RedstoneControl.writeArray(nbt, redstoneControls);
        TransferDirection.writeArray(nbt, transferDirections);
        Color.writeArray(nbt, colors);
        nbt.putIntArray("priorities", priorities);
    }

    public void read(CompoundTag nbt)
    {
        redstoneControls = RedstoneControl.readArray(nbt);
        transferDirections = TransferDirection.readArray(nbt);
        colors = Color.readArray(nbt);
        priorities = nbt.getIntArray("priorities");
    }

    public void write(FriendlyByteBuf buffer)
    {
        RedstoneControl.writeArray(buffer, redstoneControls);
        TransferDirection.writeArray(buffer, transferDirections);
        Color.writeArray(buffer, colors);
        buffer.writeVarIntArray(priorities);
    }

    public void read(FriendlyByteBuf buffer)
    {
        redstoneControls = RedstoneControl.readArray(buffer);
        transferDirections = TransferDirection.readArray(buffer);
        colors = Color.readArray(buffer);
        priorities = buffer.readVarIntArray();
    }

    public void writeTransferDirection(FriendlyByteBuf buffer, int index)
    {
        TransferDirection.write(buffer, transferDirections[index]);
    }

    public void readTransferDirection(FriendlyByteBuf buffer, int index)
    {
        transferDirections[index] = TransferDirection.read(buffer);
    }

    public TransferDirection getTransferDirection(int index)
    {
        return transferDirections[index];
    }
    
    public TransferDirection[] getTransferDirections()
    {
    	return transferDirections;
    }

    public void setTransferDirection(int index, TransferDirection td)
    {
        transferDirections[index] = td;
        if (m_transferDirectionChangedCallback != null)
        	m_transferDirectionChangedCallback.onChanged(index, td);
    }

    public void writeRedstoneControl(FriendlyByteBuf buffer, int index)
    {
        RedstoneControl.write(buffer, redstoneControls[index]);
    }

    public void readRedstoneControl(FriendlyByteBuf buffer, int index)
    {
        redstoneControls[index] = RedstoneControl.read(buffer);
    }

    public RedstoneControl getRedstoneControl(int index)
    {
        return redstoneControls[index];
    }
    
    public RedstoneControl[] getRedstoneControls()
    {
    	return redstoneControls;
    }

    public void setRedstoneControl(int index, RedstoneControl rc)
    {
    	RedstoneControl rcOld = redstoneControls[index];
    	if (rcOld != rc)
    	{
	        redstoneControls[index] = rc;
	        if (m_redstoneControlChangedCallback != null)
	        	m_redstoneControlChangedCallback.onChanged(index, rcOld, rc);
    	}
    }

    public void writeColor(FriendlyByteBuf buffer, int index)
    {
    	Color.write(buffer, colors[index]);
    }

    public void readColor(FriendlyByteBuf buffer, int index)
    {
    	colors[index] = Color.read(buffer);
    }

    public Color getColor(int index)
    {
        return colors[index];
    }
    
    public Color[] getColors()
    {
    	return colors;
    }

    public void setColor(int index, Color color)
    {
    	Color cOld = colors[index];
    	if (cOld != color)
    	{
	    	colors[index] = color;
	    	if (m_colorChangedCallback != null)
	    		m_colorChangedCallback.onChanged(index, cOld, color);
    	}
    }

    public void writePriority(FriendlyByteBuf buffer, int index)
    {
    	buffer.writeInt(priorities[index]);
    }

    public void readPriority(FriendlyByteBuf buffer, int index)
    {
    	priorities[index] = buffer.readInt();
    }

    public int getPriority(int index)
    {
        return priorities[index];
    }
    
    public int[] getPriorities()
    {
    	return priorities;
    }

    public void setPriority(int index, int priority)
    {
    	int priorityOld = priorities[index];
    	if (priority != priorityOld)
    	{
	    	priorities[index] = priority;
	    	if (m_priorityChangedCallback != null)
	    		m_priorityChangedCallback.onChanged(index, priorityOld, priority);
    	}
    }

    protected RedstoneControl[] redstoneControls;
    protected TransferDirection[] transferDirections;
    protected Color[] colors;
    protected int[] priorities;
    
    protected TransferDirectionChangedCallback m_transferDirectionChangedCallback;
    protected RedstoneControlChangedCallback m_redstoneControlChangedCallback;
    protected ColorChangedCallback m_colorChangedCallback;
    protected PriorityChangedCallback m_priorityChangedCallback;
}
