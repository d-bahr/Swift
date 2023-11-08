package swiftmod.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeItem;
import swiftmod.common.upgrades.BasicItemFilterUpgradeItem;
import swiftmod.common.upgrades.ChunkLoaderUpgradeItem;
import swiftmod.common.upgrades.InterdimensionalUpgradeItem;
import swiftmod.common.upgrades.SideUpgradeItem;
import swiftmod.common.upgrades.SpeedDowngradeItem;
import swiftmod.common.upgrades.SpeedUpgradeItem;
import swiftmod.common.upgrades.StackUpgradeItem;
import swiftmod.common.upgrades.TeleporterUpgradeItem;
import swiftmod.common.upgrades.UltimateStackUpgradeItem;
import swiftmod.common.upgrades.WildcardFilterUpgradeItem;

public class SwiftItems
{
    public static void registerItems()
    {
        s_basicItemPipeBlockItem     = s_items.register("basic_item_pipe",     () -> createBlockItem(64, SwiftBlocks.s_basicItemPipeBlock));
        s_advancedItemPipeBlockItem  = s_items.register("advanced_item_pipe",  () -> createBlockItem(64, SwiftBlocks.s_advancedItemPipeBlock));
        s_ultimateItemPipeBlockItem  = s_items.register("ultimate_item_pipe",  () -> createBlockItem(64, SwiftBlocks.s_ultimateItemPipeBlock));
        s_basicFluidPipeBlockItem    = s_items.register("basic_fluid_pipe",    () -> createBlockItem(64, SwiftBlocks.s_basicFluidPipeBlock));
        s_advancedFluidPipeBlockItem = s_items.register("advanced_fluid_pipe", () -> createBlockItem(64, SwiftBlocks.s_advancedFluidPipeBlock));
        s_ultimateFluidPipeBlockItem = s_items.register("ultimate_fluid_pipe", () -> createBlockItem(64, SwiftBlocks.s_ultimateFluidPipeBlock));

        s_tankBlockItem               = s_items.register(TankItem.REGISTRY_NAME, () -> new TankItem());
        s_speedUpgradeItem            = s_items.register(SpeedUpgradeItem.REGISTRY_NAME, () -> new SpeedUpgradeItem());
        s_stackUpgradeItem            = s_items.register(StackUpgradeItem.REGISTRY_NAME, () -> new StackUpgradeItem());
        s_speedDowngradeItem          = s_items.register(SpeedDowngradeItem.REGISTRY_NAME, () -> new SpeedDowngradeItem());
        s_ultimateStackUpgradeItem    = s_items.register(UltimateStackUpgradeItem.REGISTRY_NAME, () -> new UltimateStackUpgradeItem());
        s_teleporterUpgradeItem       = s_items.register(TeleporterUpgradeItem.REGISTRY_NAME, () -> new TeleporterUpgradeItem());
        s_interdimensionalUpgradeItem = s_items.register(InterdimensionalUpgradeItem.REGISTRY_NAME, () -> new InterdimensionalUpgradeItem());
        s_basicItemFilterUpgradeItem  = s_items.register(BasicItemFilterUpgradeItem.REGISTRY_NAME, () -> new BasicItemFilterUpgradeItem());
        s_basicFluidFilterUpgradeItem = s_items.register(BasicFluidFilterUpgradeItem.REGISTRY_NAME, () -> new BasicFluidFilterUpgradeItem());
        s_sideUpgradeItem             = s_items.register(SideUpgradeItem.REGISTRY_NAME, () -> new SideUpgradeItem());
        s_chunkLoaderUpgradeItem      = s_items.register(ChunkLoaderUpgradeItem.REGISTRY_NAME, () -> new ChunkLoaderUpgradeItem());
        s_wildcardFilterUpgradeItem   = s_items.register(WildcardFilterUpgradeItem.REGISTRY_NAME, () -> new WildcardFilterUpgradeItem());
        s_copyPastaItem               = s_items.register(CopyPastaItem.REGISTRY_NAME, () -> new CopyPastaItem());

    	s_items.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static BlockItem createBlockItem(int maxStackSize, RegistryObject<? extends Block> block)
    {
        Item.Properties properties = new Item.Properties().stacksTo(maxStackSize);
        BlockItem item = new BlockItem(block.get(), properties);
        return item;
    }
    
    private static DeferredRegister<Item> s_items = DeferredRegister.create(ForgeRegistries.ITEMS, Swift.MOD_NAME);

    public static RegistryObject<BlockItem> s_basicItemPipeBlockItem;
    public static RegistryObject<BlockItem> s_advancedItemPipeBlockItem;
    public static RegistryObject<BlockItem> s_ultimateItemPipeBlockItem;
    public static RegistryObject<BlockItem> s_basicFluidPipeBlockItem;
    public static RegistryObject<BlockItem> s_advancedFluidPipeBlockItem;
    public static RegistryObject<BlockItem> s_ultimateFluidPipeBlockItem;
    public static RegistryObject<TankItem> s_tankBlockItem;
    public static RegistryObject<SpeedUpgradeItem> s_speedUpgradeItem;
    public static RegistryObject<StackUpgradeItem> s_stackUpgradeItem;
    public static RegistryObject<SpeedDowngradeItem> s_speedDowngradeItem;
    public static RegistryObject<UltimateStackUpgradeItem> s_ultimateStackUpgradeItem;
    public static RegistryObject<TeleporterUpgradeItem> s_teleporterUpgradeItem;
    public static RegistryObject<InterdimensionalUpgradeItem> s_interdimensionalUpgradeItem;
    public static RegistryObject<BasicItemFilterUpgradeItem> s_basicItemFilterUpgradeItem;
    public static RegistryObject<BasicFluidFilterUpgradeItem> s_basicFluidFilterUpgradeItem;
    public static RegistryObject<SideUpgradeItem> s_sideUpgradeItem;
    public static RegistryObject<ChunkLoaderUpgradeItem> s_chunkLoaderUpgradeItem;
    public static RegistryObject<WildcardFilterUpgradeItem> s_wildcardFilterUpgradeItem;
    public static RegistryObject<CopyPastaItem> s_copyPastaItem;
}
