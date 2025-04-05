package swiftmod.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import swiftmod.pipes.*;

public class SwiftBlocks
{
    public static void registerBlocks(IEventBus bus)
    {
    	s_basicItemPipeBlock      = s_blocks.register("basic_item_pipe", BasicItemPipeBlock::new);
    	s_advancedItemPipeBlock   = s_blocks.register("advanced_item_pipe", AdvancedItemPipeBlock::new);
    	s_basicFluidPipeBlock     = s_blocks.register("basic_fluid_pipe", BasicFluidPipeBlock::new);
    	s_advancedFluidPipeBlock  = s_blocks.register("advanced_fluid_pipe", AdvancedFluidPipeBlock::new);
    	s_basicEnergyPipeBlock    = s_blocks.register("basic_energy_pipe", BasicEnergyPipeBlock::new);
    	s_advancedEnergyPipeBlock = s_blocks.register("advanced_energy_pipe", AdvancedEnergyPipeBlock::new);
    	s_basicOmniPipeBlock      = s_blocks.register("basic_omni_pipe", BasicOmniPipeBlock::new);
    	s_advancedOmniPipeBlock   = s_blocks.register("advanced_omni_pipe", AdvancedOmniPipeBlock::new);
    	s_tankBlock               = s_blocks.register("tank", TankBlock::new);
    	s_wormholeBlock           = s_blocks.register("wormhole", WormholeBlock::new);
    	
    	s_blocks.register(bus);
    }
    
    private static DeferredRegister.Blocks s_blocks = DeferredRegister.createBlocks(Swift.MOD_NAME);

    public static DeferredBlock<BasicItemPipeBlock> s_basicItemPipeBlock;
    public static DeferredBlock<AdvancedItemPipeBlock> s_advancedItemPipeBlock;
    public static DeferredBlock<BasicFluidPipeBlock> s_basicFluidPipeBlock;
    public static DeferredBlock<AdvancedFluidPipeBlock> s_advancedFluidPipeBlock;
    public static DeferredBlock<BasicEnergyPipeBlock> s_basicEnergyPipeBlock;
    public static DeferredBlock<AdvancedEnergyPipeBlock> s_advancedEnergyPipeBlock;
    public static DeferredBlock<BasicOmniPipeBlock> s_basicOmniPipeBlock;
    public static DeferredBlock<AdvancedOmniPipeBlock> s_advancedOmniPipeBlock;
    public static DeferredBlock<TankBlock> s_tankBlock;
    public static DeferredBlock<WormholeBlock> s_wormholeBlock;
}
