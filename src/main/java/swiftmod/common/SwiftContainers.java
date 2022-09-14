package swiftmod.common;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.network.IContainerFactory;
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
    public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event)
    {
        IForgeRegistry<MenuType<?>> registry = event.getRegistry();

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
    	MenuScreens.register(s_basicItemPipeContainerType, BasicItemPipeContainerScreen::new);
    	MenuScreens.register(s_advancedItemPipeContainerType, AdvancedItemPipeContainerScreen::new);
        MenuScreens.register(s_ultimateItemPipeContainerType, UltimateItemPipeContainerScreen::new);
        MenuScreens.register(s_basicFluidPipeContainerType, BasicFluidPipeContainerScreen::new);
        MenuScreens.register(s_advancedFluidPipeContainerType, AdvancedFluidPipeContainerScreen::new);
        MenuScreens.register(s_ultimateFluidPipeContainerType, UltimateFluidPipeContainerScreen::new);
        MenuScreens.register(s_basicItemFilterContainerType, BasicItemFilterUpgradeContainerScreen::new);
        MenuScreens.register(s_basicFluidFilterContainerType, BasicFluidFilterUpgradeContainerScreen::new);
        MenuScreens.register(s_teleporterUpgradeContainerType, TeleporterUpgradeContainerScreen::new);
        MenuScreens.register(s_sideUpgradeContainerType, SideUpgradeContainerScreen::new);
        MenuScreens.register(s_wildcardFilterContainertype, WildcardFilterUpgradeContainerScreen::new);
    }

    private static <T extends AbstractContainerMenu> MenuType<T> createContainer(String registryName, IContainerFactory<T> factory)
    {
        MenuType<T> t = IForgeMenuType.create(factory);
        t.setRegistryName(Swift.MOD_NAME, registryName);
        return t;
    }

    public static MenuType<BasicItemPipeContainer> s_basicItemPipeContainerType;
    public static MenuType<AdvancedItemPipeContainer> s_advancedItemPipeContainerType;
    public static MenuType<UltimateItemPipeContainer> s_ultimateItemPipeContainerType;
    public static MenuType<BasicFluidPipeContainer> s_basicFluidPipeContainerType;
    public static MenuType<AdvancedFluidPipeContainer> s_advancedFluidPipeContainerType;
    public static MenuType<UltimateFluidPipeContainer> s_ultimateFluidPipeContainerType;
    public static MenuType<BasicItemFilterUpgradeContainer> s_basicItemFilterContainerType;
    public static MenuType<BasicFluidFilterUpgradeContainer> s_basicFluidFilterContainerType;
    public static MenuType<TeleporterUpgradeContainer> s_teleporterUpgradeContainerType;
    public static MenuType<SideUpgradeContainer> s_sideUpgradeContainerType;
    public static MenuType<WildcardFilterUpgradeContainer> s_wildcardFilterContainertype;
}
