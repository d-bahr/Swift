package swiftmod.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record ImmutableFluidStack(FluidStack fluidStack)
{
    public static final Codec<ImmutableFluidStack> CODEC = RecordCodecBuilder.create(instance ->
    		instance.group(FluidStack.OPTIONAL_CODEC.fieldOf("f").forGetter(ImmutableFluidStack::fluidStack)).apply(instance, ImmutableFluidStack::new));
    
	public static StreamCodec<RegistryFriendlyByteBuf, ImmutableFluidStack> STREAM_CODEC = StreamCodec.composite(FluidStack.OPTIONAL_STREAM_CODEC, ImmutableFluidStack::fluidStack, ImmutableFluidStack::new);
}
