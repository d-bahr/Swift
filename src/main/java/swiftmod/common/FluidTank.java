package swiftmod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class FluidTank extends net.minecraftforge.fluids.capability.templates.FluidTank implements IFluidTank, DataCache
{
    public FluidTank(int capacity)
    {
        super(capacity);
    }

    public void write(CompoundTag nbt)
    {
        writeToNBT(nbt);
    }

    public void read(CompoundTag nbt)
    {
        readFromNBT(nbt);
    }

    public void write(FriendlyByteBuf buffer)
    {
        fluid.writeToPacket(buffer);
    }

    public void read(FriendlyByteBuf buffer)
    {
        setFluid(FluidStack.readFromPacket(buffer));
    }
    
    public static FluidStack readFluidStack(CompoundTag nbt)
    {
        return FluidStack.loadFluidStackFromNBT(nbt);
    }
}
