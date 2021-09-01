package swiftmod.common;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
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
    public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        s_basicItemPipeBlockItem = createBlockItem("basic_item_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_basicItemPipeBlock);
        registry.register(s_basicItemPipeBlockItem);

        s_advancedItemPipeBlockItem = createBlockItem("advanced_item_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_advancedItemPipeBlock);
        registry.register(s_advancedItemPipeBlockItem);

        s_ultimateItemPipeBlockItem = createBlockItem("ultimate_item_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_ultimateItemPipeBlock);
        registry.register(s_ultimateItemPipeBlockItem);

        s_basicFluidPipeBlockItem = createBlockItem("basic_fluid_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_basicFluidPipeBlock);
        registry.register(s_basicFluidPipeBlockItem);

        s_advancedFluidPipeBlockItem = createBlockItem("advanced_fluid_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_advancedFluidPipeBlock);
        registry.register(s_advancedFluidPipeBlockItem);

        s_ultimateFluidPipeBlockItem = createBlockItem("ultimate_fluid_pipe", 64, Swift.ITEM_GROUP, SwiftBlocks.s_ultimateFluidPipeBlock);
        registry.register(s_ultimateFluidPipeBlockItem);

        s_tankBlockItem = new TankItem();
        registry.register(s_tankBlockItem);

        s_speedUpgradeItem = new SpeedUpgradeItem();
        registry.register(s_speedUpgradeItem);

        s_stackUpgradeItem = new StackUpgradeItem();
        registry.register(s_stackUpgradeItem);

        s_speedDowngradeItem = new SpeedDowngradeItem();
        registry.register(s_speedDowngradeItem);

        s_ultimateStackUpgradeItem = new UltimateStackUpgradeItem();
        registry.register(s_ultimateStackUpgradeItem);

        s_teleporterUpgradeItem = new TeleporterUpgradeItem();
        registry.register(s_teleporterUpgradeItem);

        s_interdimensionalUpgradeItem = new InterdimensionalUpgradeItem();
        registry.register(s_interdimensionalUpgradeItem);

        s_basicItemFilterUpgradeItem = new BasicItemFilterUpgradeItem();
        registry.register(s_basicItemFilterUpgradeItem);

        s_basicFluidFilterUpgradeItem = new BasicFluidFilterUpgradeItem();
        registry.register(s_basicFluidFilterUpgradeItem);

        s_sideUpgradeItem = new SideUpgradeItem();
        registry.register(s_sideUpgradeItem);

        s_chunkLoaderUpgradeItem = new ChunkLoaderUpgradeItem();
        registry.register(s_chunkLoaderUpgradeItem);

        s_wildcardFilterUpgradeItem = new WildcardFilterUpgradeItem();
        registry.register(s_wildcardFilterUpgradeItem);

        s_copyPastaItem = new CopyPastaItem();
        registry.register(s_copyPastaItem);
    }

    private static BlockItem createBlockItem(String registryName, int maxStackSize, ItemGroup itemGroup, Block block)
    {
        Item.Properties properties = new Item.Properties().stacksTo(maxStackSize).tab(itemGroup);
        BlockItem item = new BlockItem(block, properties);
        item.setRegistryName(Swift.MOD_NAME, registryName);
        return item;
    }

    public static BlockItem s_basicItemPipeBlockItem;
    public static BlockItem s_advancedItemPipeBlockItem;
    public static BlockItem s_ultimateItemPipeBlockItem;
    public static BlockItem s_basicFluidPipeBlockItem;
    public static BlockItem s_advancedFluidPipeBlockItem;
    public static BlockItem s_ultimateFluidPipeBlockItem;
    public static TankItem s_tankBlockItem;
    public static SpeedUpgradeItem s_speedUpgradeItem;
    public static StackUpgradeItem s_stackUpgradeItem;
    public static SpeedDowngradeItem s_speedDowngradeItem;
    public static UltimateStackUpgradeItem s_ultimateStackUpgradeItem;
    public static TeleporterUpgradeItem s_teleporterUpgradeItem;
    public static InterdimensionalUpgradeItem s_interdimensionalUpgradeItem;
    public static BasicItemFilterUpgradeItem s_basicItemFilterUpgradeItem;
    public static BasicFluidFilterUpgradeItem s_basicFluidFilterUpgradeItem;
    public static SideUpgradeItem s_sideUpgradeItem;
    public static ChunkLoaderUpgradeItem s_chunkLoaderUpgradeItem;
    public static WildcardFilterUpgradeItem s_wildcardFilterUpgradeItem;
    public static CopyPastaItem s_copyPastaItem;
}
