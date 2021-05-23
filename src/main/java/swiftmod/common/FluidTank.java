package swiftmod.common;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class FluidTank extends net.minecraftforge.fluids.capability.templates.FluidTank implements IFluidTank, DataCache
{
    public FluidTank(int capacity)
    {
        super(capacity);
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        writeToNBT(nbt);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        readFromNBT(nbt);
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        fluid.writeToPacket(buffer);
        return buffer;
    }

    public void read(PacketBuffer buffer)
    {
        setFluid(FluidStack.readFromPacket(buffer));
    }
}
