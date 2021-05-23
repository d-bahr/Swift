package swiftmod.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemContainerProvider<T extends Container> implements INamedContainerProvider
{
    @FunctionalInterface
    public interface ContainerSupplier<T extends Container>
    {
        T createContainerServerSide(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity);
    }

    @FunctionalInterface
    public interface SimpleContainerSupplier<T extends Container>
    {
        T createContainerServerSide(int windowID);
    }

    @FunctionalInterface
    public interface ContainerGuiEncoder
    {
        void encode(PlayerEntity player, ItemStack itemStack, PacketBuffer packetBuffer);
    }

    public ItemContainerProvider(ItemStack itemStack, ContainerSupplier<T> supplier)
    {
        this.itemStack = itemStack;
        this.supplier = supplier;
        this.simpleSupplier = null;
    }

    public ItemContainerProvider(ItemStack itemStack, SimpleContainerSupplier<T> supplier)
    {
        this.itemStack = itemStack;
        this.supplier = null;
        this.simpleSupplier = supplier;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return itemStack.getDisplayName();
    }

    @Override
    public T createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        if (supplier != null)
            return supplier.createContainerServerSide(windowID, playerInventory, playerEntity);
        else
            return simpleSupplier.createContainerServerSide(windowID);
    }

    public static <T extends Container> ActionResult<ItemStack> openContainerGui(World world, PlayerEntity player,
            Hand hand, ContainerSupplier<T> supplier, ContainerGuiEncoder encoder)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (hand == Hand.OFF_HAND)
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);

        if (!world.isClientSide)
        {
            INamedContainerProvider containerProvider = new ItemContainerProvider<T>(itemStack, supplier);
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, (packetBuffer) ->
            {
                encoder.encode(player, itemStack, packetBuffer);
            });
        }

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);
    }

    public static <T extends Container> ActionResult<ItemStack> openContainerGui(World world, PlayerEntity player,
            Hand hand, SimpleContainerSupplier<T> supplier, ContainerGuiEncoder encoder)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (hand == Hand.OFF_HAND)
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);

        if (!world.isClientSide)
        {
            INamedContainerProvider containerProvider = new ItemContainerProvider<T>(itemStack, supplier);
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, (packetBuffer) ->
            {
                encoder.encode(player, itemStack, packetBuffer);
            });
        }

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);
    }

    private ItemStack itemStack;
    private ContainerSupplier<T> supplier;
    private SimpleContainerSupplier<T> simpleSupplier;
}
