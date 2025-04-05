package swiftmod.pipes;

import swiftmod.common.Color;
import swiftmod.common.Filter;
import swiftmod.common.RedstoneControl;

public class PipeTransferData<V>
{
	public PipeTransferData()
	{
		redstoneControl = RedstoneControl.Disabled;
		color = Color.Transparent;
		filter = null;
		maxTransferQuantity = null;
		tickRate = 20;
	}
	
	public RedstoneControl redstoneControl;
	public Color color;
	public Filter<V> filter;
	public PipeTransferQuantity maxTransferQuantity;
	public int tickRate;
}
