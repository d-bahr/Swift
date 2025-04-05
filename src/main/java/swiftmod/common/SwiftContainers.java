package swiftmod.common;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeContainer;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeContainerScreen;
import swiftmod.common.upgrades.BasicItemFilterUpgradeContainer;
import swiftmod.common.upgrades.BasicItemFilterUpgradeContainerScreen;
import swiftmod.common.upgrades.WildcardFilterUpgradeContainer;
import swiftmod.common.upgrades.WildcardFilterUpgradeContainerScreen;
import swiftmod.pipes.*;

public class SwiftContainers
{
    public static void registerContainers(IEventBus bus)
    {
    	s_basicItemPipeContainerType = s_containers.register("basic_item_pipe", () -> IMenuTypeExtension.create(BasicItemPipeContainer::createContainerClientSide));
    	s_advancedItemPipeContainerType = s_containers.register("advanced_item_pipe", () -> IMenuTypeExtension.create(AdvancedItemPipeContainer::createContainerClientSide));
    	s_basicFluidPipeContainerType = s_containers.register("basic_fluid_pipe", () -> IMenuTypeExtension.create(BasicFluidPipeContainer::createContainerClientSide));
    	s_advancedFluidPipeContainerType = s_containers.register("advanced_fluid_pipe", () -> IMenuTypeExtension.create(AdvancedFluidPipeContainer::createContainerClientSide));
    	s_basicEnergyPipeContainerType = s_containers.register("basic_energy_pipe", () -> IMenuTypeExtension.create(BasicEnergyPipeContainer::createContainerClientSide));
    	s_advancedEnergyPipeContainerType = s_containers.register("advanced_energy_pipe", () -> IMenuTypeExtension.create(AdvancedEnergyPipeContainer::createContainerClientSide));
    	s_basicOmniPipeContainerType = s_containers.register("basic_omni_pipe", () -> IMenuTypeExtension.create(BasicOmniPipeContainer::createContainerClientSide));
    	s_advancedOmniPipeContainerType = s_containers.register("advanced_omni_pipe", () -> IMenuTypeExtension.create(AdvancedOmniPipeContainer::createContainerClientSide));
    	s_basicItemFilterContainerType = s_containers.register("basic_item_filter_upgrade", () -> IMenuTypeExtension.create(BasicItemFilterUpgradeContainer::createContainerClientSide));
    	s_basicFluidFilterContainerType = s_containers.register("basic_fluid_filter_upgrade", () -> IMenuTypeExtension.create(BasicFluidFilterUpgradeContainer::createContainerClientSide));
    	s_wildcardFilterContainerType = s_containers.register("wildcard_filter_upgrade", () -> IMenuTypeExtension.create(WildcardFilterUpgradeContainer::createContainerClientSide));
    	s_wormholeContainerType = s_containers.register("wormhole", () -> IMenuTypeExtension.create(WormholeContainer::createContainerClientSide));

        s_containers.register(bus);
    }

    public static void registerScreenTypes(RegisterMenuScreensEvent event)
    {
    	event.register(s_basicItemPipeContainerType.get(), BasicItemPipeContainerScreen::new);
    	event.register(s_advancedItemPipeContainerType.get(), AdvancedItemPipeContainerScreen::new);
        event.register(s_basicFluidPipeContainerType.get(), BasicFluidPipeContainerScreen::new);
        event.register(s_advancedFluidPipeContainerType.get(), AdvancedFluidPipeContainerScreen::new);
        event.register(s_basicEnergyPipeContainerType.get(), BasicEnergyPipeContainerScreen::new);
        event.register(s_advancedEnergyPipeContainerType.get(), AdvancedEnergyPipeContainerScreen::new);
        event.register(s_basicOmniPipeContainerType.get(), BasicOmniPipeContainerScreen::new);
        event.register(s_advancedOmniPipeContainerType.get(), AdvancedOmniPipeContainerScreen::new);
        event.register(s_basicItemFilterContainerType.get(), BasicItemFilterUpgradeContainerScreen::new);
        event.register(s_basicFluidFilterContainerType.get(), BasicFluidFilterUpgradeContainerScreen::new);
        event.register(s_wildcardFilterContainerType.get(), WildcardFilterUpgradeContainerScreen::new);
        event.register(s_wormholeContainerType.get(), WormholeContainerScreen::new);
    }

    /*private static <T extends AbstractContainerMenu> MenuType<T> createContainer(IContainerFactory<T> factory)
    {
        return IForgeMenuType.create(factory);
    }*/
    
    private static DeferredRegister<MenuType<?>> s_containers = DeferredRegister.create(Registries.MENU, Swift.MOD_NAME);

    public static Supplier<MenuType<BasicItemPipeContainer>> s_basicItemPipeContainerType;
    public static Supplier<MenuType<AdvancedItemPipeContainer>> s_advancedItemPipeContainerType;
    public static Supplier<MenuType<BasicFluidPipeContainer>> s_basicFluidPipeContainerType;
    public static Supplier<MenuType<AdvancedFluidPipeContainer>> s_advancedFluidPipeContainerType;
    public static Supplier<MenuType<BasicEnergyPipeContainer>> s_basicEnergyPipeContainerType;
    public static Supplier<MenuType<AdvancedEnergyPipeContainer>> s_advancedEnergyPipeContainerType;
    public static Supplier<MenuType<BasicOmniPipeContainer>> s_basicOmniPipeContainerType;
    public static Supplier<MenuType<AdvancedOmniPipeContainer>> s_advancedOmniPipeContainerType;
    public static Supplier<MenuType<BasicItemFilterUpgradeContainer>> s_basicItemFilterContainerType;
    public static Supplier<MenuType<BasicFluidFilterUpgradeContainer>> s_basicFluidFilterContainerType;
    public static Supplier<MenuType<WildcardFilterUpgradeContainer>> s_wildcardFilterContainerType;
    public static Supplier<MenuType<WormholeContainer>> s_wormholeContainerType;
}
