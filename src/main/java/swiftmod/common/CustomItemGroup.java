package swiftmod.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomItemGroup
{
	public static void registerCreativeTabs(IEventBus bus)
	{
		s_tab = s_creativeModeTab.register(Swift.MOD_NAME, () ->
	    	CreativeModeTab.builder()
	    	.title(Component.literal("Swift"))
	    	.icon(() -> new ItemStack(SwiftItems.s_advancedItemPipeBlockItem.get()))
	    	.displayItems(CustomItemGroup::getDisplayItems)
	    	.build());

		s_creativeModeTab.register(bus);
	}
	
    private static void getDisplayItems(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output)
    {
        output.accept(SwiftItems.s_basicItemPipeBlockItem.get());
        output.accept(SwiftItems.s_advancedItemPipeBlockItem.get());
        output.accept(SwiftItems.s_basicFluidPipeBlockItem.get());
        output.accept(SwiftItems.s_advancedFluidPipeBlockItem.get());
        output.accept(SwiftItems.s_basicEnergyPipeBlockItem.get());
        output.accept(SwiftItems.s_advancedEnergyPipeBlockItem.get());
        output.accept(SwiftItems.s_basicOmniPipeBlockItem.get());
        output.accept(SwiftItems.s_advancedOmniPipeBlockItem.get());
        output.accept(SwiftItems.s_wormholeItem.get());
        output.accept(SwiftItems.s_tankBlockItem.get());
        output.accept(SwiftItems.s_speedUpgradeItem.get());
        output.accept(SwiftItems.s_stackUpgradeItem.get());
        output.accept(SwiftItems.s_speedDowngradeItem.get());
        output.accept(SwiftItems.s_ultimateStackUpgradeItem.get());
        output.accept(SwiftItems.s_basicItemFilterUpgradeItem.get());
        output.accept(SwiftItems.s_basicFluidFilterUpgradeItem.get());
        output.accept(SwiftItems.s_chunkLoaderUpgradeItem.get());
        output.accept(SwiftItems.s_wildcardFilterUpgradeItem.get());
        output.accept(SwiftItems.s_wrenchItem.get());
        output.accept(SwiftItems.s_copyPastaItem.get());
    }

    private static DeferredRegister<CreativeModeTab> s_creativeModeTab = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Swift.MOD_NAME);
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> s_tab;
}
