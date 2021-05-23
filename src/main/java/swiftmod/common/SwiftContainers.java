package swiftmod.common;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeContainer;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeContainerScreen;
import swiftmod.common.upgrades.BasicItemFilterUpgradeContainer;
import swiftmod.common.upgrades.BasicItemFilterUpgradeContainerScreen;
import swiftmod.common.upgrades.SideUpgradeContainer;
import swiftmod.common.upgrades.SideUpgradeContainerScreen;
import swiftmod.common.upgrades.TeleporterUpgradeContainer;
import swiftmod.common.upgrades.TeleporterUpgradeContainerScreen;
import swiftmod.common.upgrades.WildcardFilterUpgradeContainer;
import swiftmod.common.upgrades.WildcardFilterUpgradeContainerScreen;
import swiftmod.pipes.*;

public class SwiftContainers
{
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();

        s_basicItemPipeContainerType = createContainer("basic_item_pipe", BasicItemPipeContainer::createContainerClientSide);
        registry.register(s_basicItemPipeContainerType);

        s_advancedItemPipeContainerType = createContainer("advanced_item_pipe", AdvancedItemPipeContainer::createContainerClientSide);
        registry.register(s_advancedItemPipeContainerType);

        s_ultimateItemPipeContainerType = createContainer("ultimate_item_pipe", UltimateItemPipeContainer::createContainerClientSide);
        registry.register(s_ultimateItemPipeContainerType);

        s_basicFluidPipeContainerType = createContainer("basic_fluid_pipe", BasicFluidPipeContainer::createContainerClientSide);
        registry.register(s_basicFluidPipeContainerType);

        s_advancedFluidPipeContainerType = createContainer("advanced_fluid_pipe", AdvancedFluidPipeContainer::createContainerClientSide);
        registry.register(s_advancedFluidPipeContainerType);

        s_ultimateFluidPipeContainerType = createContainer("ultimate_fluid_pipe", UltimateFluidPipeContainer::createContainerClientSide);
        registry.register(s_ultimateFluidPipeContainerType);

        s_basicItemFilterContainerType = createContainer("basic_item_filter_upgrade", BasicItemFilterUpgradeContainer::createContainerClientSide);
        registry.register(s_basicItemFilterContainerType);

        s_basicFluidFilterContainerType = createContainer("basic_fluid_filter_upgrade", BasicFluidFilterUpgradeContainer::createContainerClientSide);
        registry.register(s_basicFluidFilterContainerType);

        s_teleporterUpgradeContainerType = createContainer("teleporter_upgrade", TeleporterUpgradeContainer::createContainerClientSide);
        registry.register(s_teleporterUpgradeContainerType);

        s_sideUpgradeContainerType = createContainer("side_upgrade", SideUpgradeContainer::createContainerClientSide);
        registry.register(s_sideUpgradeContainerType);
        
        s_wildcardFilterContainertype = createContainer("wildcard_filter_upgrade", WildcardFilterUpgradeContainer::createContainerClientSide);
        registry.register(s_wildcardFilterContainertype);
    }

    public static void registerScreenTypes()
    {
        ScreenManager.register(s_basicItemPipeContainerType, BasicItemPipeContainerScreen::new);
        ScreenManager.register(s_advancedItemPipeContainerType, AdvancedItemPipeContainerScreen::new);
        ScreenManager.register(s_ultimateItemPipeContainerType, UltimateItemPipeContainerScreen::new);
        ScreenManager.register(s_basicFluidPipeContainerType, BasicFluidPipeContainerScreen::new);
        ScreenManager.register(s_advancedFluidPipeContainerType, AdvancedFluidPipeContainerScreen::new);
        ScreenManager.register(s_ultimateFluidPipeContainerType, UltimateFluidPipeContainerScreen::new);
        ScreenManager.register(s_basicItemFilterContainerType, BasicItemFilterUpgradeContainerScreen::new);
        ScreenManager.register(s_basicFluidFilterContainerType, BasicFluidFilterUpgradeContainerScreen::new);
        ScreenManager.register(s_teleporterUpgradeContainerType, TeleporterUpgradeContainerScreen::new);
        ScreenManager.register(s_sideUpgradeContainerType, SideUpgradeContainerScreen::new);
        ScreenManager.register(s_wildcardFilterContainertype, WildcardFilterUpgradeContainerScreen::new);
    }

    private static <T extends Container> ContainerType<T> createContainer(String registryName, IContainerFactory<T> factory)
    {
        ContainerType<T> t = IForgeContainerType.create(factory);
        t.setRegistryName(Swift.MOD_NAME, registryName);
        return t;
    }

    public static ContainerType<BasicItemPipeContainer> s_basicItemPipeContainerType;
    public static ContainerType<AdvancedItemPipeContainer> s_advancedItemPipeContainerType;
    public static ContainerType<UltimateItemPipeContainer> s_ultimateItemPipeContainerType;
    public static ContainerType<BasicFluidPipeContainer> s_basicFluidPipeContainerType;
    public static ContainerType<AdvancedFluidPipeContainer> s_advancedFluidPipeContainerType;
    public static ContainerType<UltimateFluidPipeContainer> s_ultimateFluidPipeContainerType;
    public static ContainerType<BasicItemFilterUpgradeContainer> s_basicItemFilterContainerType;
    public static ContainerType<BasicFluidFilterUpgradeContainer> s_basicFluidFilterContainerType;
    public static ContainerType<TeleporterUpgradeContainer> s_teleporterUpgradeContainerType;
    public static ContainerType<SideUpgradeContainer> s_sideUpgradeContainerType;
    public static ContainerType<WildcardFilterUpgradeContainer> s_wildcardFilterContainertype;
}
