package swiftmod.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public class FluidTank extends net.neoforged.neoforge.fluids.capability.templates.FluidTank implements IFluidTank, DataCache
{
    public FluidTank(int capacity)
    {
        super(capacity);
    }

    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
        writeToNBT(provider, nbt);
    }

    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        readFromNBT(provider, nbt);
    }

    public void write(RegistryFriendlyByteBuf buffer)
    {
    	FluidStack.STREAM_CODEC.encode(buffer, fluid);
    }

    public void read(RegistryFriendlyByteBuf buffer)
    {
    	setFluid(FluidStack.STREAM_CODEC.decode(buffer));
    }
    
    public static FluidStack readFluidStack(HolderLookup.Provider provider, CompoundTag nbt)
    {
    	return FluidStack.parseOptional(provider, nbt);
    }
}
