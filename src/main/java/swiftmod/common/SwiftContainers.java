package swiftmod.common;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
    public static void registerContainers()
    {
    	s_basicItemPipeContainerType = s_containers.register("basic_item_pipe", () -> createContainer(BasicItemPipeContainer::createContainerClientSide));
    	s_advancedItemPipeContainerType = s_containers.register("advanced_item_pipe", () -> createContainer(AdvancedItemPipeContainer::createContainerClientSide));
    	s_ultimateItemPipeContainerType = s_containers.register("ultimate_item_pipe", () -> createContainer(UltimateItemPipeContainer::createContainerClientSide));
    	s_basicFluidPipeContainerType = s_containers.register("basic_fluid_pipe", () -> createContainer(BasicFluidPipeContainer::createContainerClientSide));
    	s_advancedFluidPipeContainerType = s_containers.register("advanced_fluid_pipe", () -> createContainer(AdvancedFluidPipeContainer::createContainerClientSide));
    	s_ultimateFluidPipeContainerType = s_containers.register("ultimate_fluid_pipe", () -> createContainer(UltimateFluidPipeContainer::createContainerClientSide));
    	s_basicItemFilterContainerType = s_containers.register("basic_item_filter_upgrade", () -> createContainer(BasicItemFilterUpgradeContainer::createContainerClientSide));
    	s_basicFluidFilterContainerType = s_containers.register("basic_fluid_filter_upgrade", () -> createContainer(BasicFluidFilterUpgradeContainer::createContainerClientSide));
    	s_teleporterUpgradeContainerType = s_containers.register("teleporter_upgrade", () -> createContainer(TeleporterUpgradeContainer::createContainerClientSide));
    	s_sideUpgradeContainerType = s_containers.register("side_upgrade", () -> createContainer(SideUpgradeContainer::createContainerClientSide));
    	s_wildcardFilterContainertype = s_containers.register("wildcard_filter_upgrade", () -> createContainer(WildcardFilterUpgradeContainer::createContainerClientSide));

        s_containers.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void registerScreenTypes()
    {
    	MenuScreens.register(s_basicItemPipeContainerType.get(), BasicItemPipeContainerScreen::new);
    	MenuScreens.register(s_advancedItemPipeContainerType.get(), AdvancedItemPipeContainerScreen::new);
        MenuScreens.register(s_ultimateItemPipeContainerType.get(), UltimateItemPipeContainerScreen::new);
        MenuScreens.register(s_basicFluidPipeContainerType.get(), BasicFluidPipeContainerScreen::new);
        MenuScreens.register(s_advancedFluidPipeContainerType.get(), AdvancedFluidPipeContainerScreen::new);
        MenuScreens.register(s_ultimateFluidPipeContainerType.get(), UltimateFluidPipeContainerScreen::new);
        MenuScreens.register(s_basicItemFilterContainerType.get(), BasicItemFilterUpgradeContainerScreen::new);
        MenuScreens.register(s_basicFluidFilterContainerType.get(), BasicFluidFilterUpgradeContainerScreen::new);
        MenuScreens.register(s_teleporterUpgradeContainerType.get(), TeleporterUpgradeContainerScreen::new);
        MenuScreens.register(s_sideUpgradeContainerType.get(), SideUpgradeContainerScreen::new);
        MenuScreens.register(s_wildcardFilterContainertype.get(), WildcardFilterUpgradeContainerScreen::new);
    }

    private static <T extends AbstractContainerMenu> MenuType<T> createContainer(IContainerFactory<T> factory)
    {
        return IForgeMenuType.create(factory);
    }
    
    private static DeferredRegister<MenuType<?>> s_containers = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Swift.MOD_NAME);

    public static RegistryObject<MenuType<BasicItemPipeContainer>> s_basicItemPipeContainerType;
    public static RegistryObject<MenuType<AdvancedItemPipeContainer>> s_advancedItemPipeContainerType;
    public static RegistryObject<MenuType<UltimateItemPipeContainer>> s_ultimateItemPipeContainerType;
    public static RegistryObject<MenuType<BasicFluidPipeContainer>> s_basicFluidPipeContainerType;
    public static RegistryObject<MenuType<AdvancedFluidPipeContainer>> s_advancedFluidPipeContainerType;
    public static RegistryObject<MenuType<UltimateFluidPipeContainer>> s_ultimateFluidPipeContainerType;
    public static RegistryObject<MenuType<BasicItemFilterUpgradeContainer>> s_basicItemFilterContainerType;
    public static RegistryObject<MenuType<BasicFluidFilterUpgradeContainer>> s_basicFluidFilterContainerType;
    public static RegistryObject<MenuType<TeleporterUpgradeContainer>> s_teleporterUpgradeContainerType;
    public static RegistryObject<MenuType<SideUpgradeContainer>> s_sideUpgradeContainerType;
    public static RegistryObject<MenuType<WildcardFilterUpgradeContainer>> s_wildcardFilterContainertype;
}
