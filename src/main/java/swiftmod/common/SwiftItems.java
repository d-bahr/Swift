package swiftmod.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeItem;
import swiftmod.common.upgrades.BasicItemFilterUpgradeItem;
import swiftmod.common.upgrades.ChunkLoaderUpgradeItem;
import swiftmod.common.upgrades.SpeedDowngradeItem;
import swiftmod.common.upgrades.SpeedUpgradeItem;
import swiftmod.common.upgrades.StackUpgradeItem;
import swiftmod.common.upgrades.UltimateStackUpgradeItem;
import swiftmod.common.upgrades.WildcardFilterUpgradeItem;

public class SwiftItems
{
    public static void registerItems(IEventBus bus)
    {
        s_basicItemPipeBlockItem      = s_items.registerSimpleBlockItem(SwiftBlocks.s_basicItemPipeBlock, new Item.Properties().stacksTo(64));
        s_advancedItemPipeBlockItem   = s_items.registerSimpleBlockItem(SwiftBlocks.s_advancedItemPipeBlock, new Item.Properties().stacksTo(64));
        s_basicFluidPipeBlockItem     = s_items.registerSimpleBlockItem(SwiftBlocks.s_basicFluidPipeBlock, new Item.Properties().stacksTo(64));
        s_advancedFluidPipeBlockItem  = s_items.registerSimpleBlockItem(SwiftBlocks.s_advancedFluidPipeBlock, new Item.Properties().stacksTo(64));
        s_basicEnergyPipeBlockItem    = s_items.registerSimpleBlockItem(SwiftBlocks.s_basicEnergyPipeBlock, new Item.Properties().stacksTo(64));
        s_advancedEnergyPipeBlockItem = s_items.registerSimpleBlockItem(SwiftBlocks.s_advancedEnergyPipeBlock, new Item.Properties().stacksTo(64));
        s_basicOmniPipeBlockItem      = s_items.registerSimpleBlockItem(SwiftBlocks.s_basicOmniPipeBlock, new Item.Properties().stacksTo(64));
        s_advancedOmniPipeBlockItem   = s_items.registerSimpleBlockItem(SwiftBlocks.s_advancedOmniPipeBlock, new Item.Properties().stacksTo(64));
        s_wormholeItem                = s_items.registerSimpleBlockItem(SwiftBlocks.s_wormholeBlock, new Item.Properties().stacksTo(64));

        s_tankBlockItem               = s_items.register(TankItem.REGISTRY_NAME, () -> new TankItem());
        s_speedUpgradeItem            = s_items.register(SpeedUpgradeItem.REGISTRY_NAME, () -> new SpeedUpgradeItem());
        s_stackUpgradeItem            = s_items.register(StackUpgradeItem.REGISTRY_NAME, () -> new StackUpgradeItem());
        s_speedDowngradeItem          = s_items.register(SpeedDowngradeItem.REGISTRY_NAME, () -> new SpeedDowngradeItem());
        s_ultimateStackUpgradeItem    = s_items.register(UltimateStackUpgradeItem.REGISTRY_NAME, () -> new UltimateStackUpgradeItem());
        s_basicItemFilterUpgradeItem  = s_items.register(BasicItemFilterUpgradeItem.REGISTRY_NAME, () -> new BasicItemFilterUpgradeItem());
        s_basicFluidFilterUpgradeItem = s_items.register(BasicFluidFilterUpgradeItem.REGISTRY_NAME, () -> new BasicFluidFilterUpgradeItem());
        s_chunkLoaderUpgradeItem      = s_items.register(ChunkLoaderUpgradeItem.REGISTRY_NAME, () -> new ChunkLoaderUpgradeItem());
        s_wildcardFilterUpgradeItem   = s_items.register(WildcardFilterUpgradeItem.REGISTRY_NAME, () -> new WildcardFilterUpgradeItem());
        s_wrenchItem                  = s_items.register(WrenchItem.REGISTRY_NAME, () -> new WrenchItem());
        s_copyPastaItem               = s_items.register(CopyPastaItem.REGISTRY_NAME, () -> new CopyPastaItem());

    	s_items.register(bus);
    }
    
    private static DeferredRegister.Items s_items = DeferredRegister.createItems(Swift.MOD_NAME);

    public static DeferredItem<BlockItem> s_basicItemPipeBlockItem;
    public static DeferredItem<BlockItem> s_advancedItemPipeBlockItem;
    public static DeferredItem<BlockItem> s_basicFluidPipeBlockItem;
    public static DeferredItem<BlockItem> s_advancedFluidPipeBlockItem;
    public static DeferredItem<BlockItem> s_basicEnergyPipeBlockItem;
    public static DeferredItem<BlockItem> s_advancedEnergyPipeBlockItem;
    public static DeferredItem<BlockItem> s_basicOmniPipeBlockItem;
    public static DeferredItem<BlockItem> s_advancedOmniPipeBlockItem;
    public static DeferredItem<BlockItem> s_wormholeItem;
    public static DeferredItem<TankItem> s_tankBlockItem;
    public static DeferredItem<SpeedUpgradeItem> s_speedUpgradeItem;
    public static DeferredItem<StackUpgradeItem> s_stackUpgradeItem;
    public static DeferredItem<SpeedDowngradeItem> s_speedDowngradeItem;
    public static DeferredItem<UltimateStackUpgradeItem> s_ultimateStackUpgradeItem;
    public static DeferredItem<BasicItemFilterUpgradeItem> s_basicItemFilterUpgradeItem;
    public static DeferredItem<BasicFluidFilterUpgradeItem> s_basicFluidFilterUpgradeItem;
    public static DeferredItem<ChunkLoaderUpgradeItem> s_chunkLoaderUpgradeItem;
    public static DeferredItem<WildcardFilterUpgradeItem> s_wildcardFilterUpgradeItem;
    public static DeferredItem<WrenchItem> s_wrenchItem;
    public static DeferredItem<CopyPastaItem> s_copyPastaItem;
}
