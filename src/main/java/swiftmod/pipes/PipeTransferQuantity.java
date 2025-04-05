package swiftmod.pipes;

public class PipeTransferQuantity
{
	public PipeTransferQuantity()
    {
        moveStacks = false;
        quantity = 1;
    }

	public PipeTransferQuantity(boolean stacks, int amount)
    {
        moveStacks = stacks;
        quantity = amount;
    }

    public boolean moveStacks;
    public int quantity;
}
