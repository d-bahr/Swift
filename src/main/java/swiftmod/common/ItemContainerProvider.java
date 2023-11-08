package swiftmod.common;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class ItemContainerProvider<T extends AbstractContainerMenu> implements MenuProvider
{
    @FunctionalInterface
    public interface ContainerSupplier<T extends AbstractContainerMenu>
    {
        T createContainerServerSide(int windowID, Inventory playerInventory, Player playerEntity);
    }

    @FunctionalInterface
    public interface SimpleContainerSupplier<T extends AbstractContainerMenu>
    {
        T createContainerServerSide(int windowID);
    }

    @FunctionalInterface
    public interface ContainerGuiEncoder
    {
        void encode(Player player, ItemStack itemStack, FriendlyByteBuf FriendlyByteBuf);
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
    public Component getDisplayName()
    {
        return itemStack.getDisplayName();
    }

    @Override
    public T createMenu(int windowID, Inventory playerInventory, Player playerEntity)
    {
        if (supplier != null)
            return supplier.createContainerServerSide(windowID, playerInventory, playerEntity);
        else
            return simpleSupplier.createContainerServerSide(windowID);
    }

    public static <T extends AbstractContainerMenu> InteractionResultHolder<ItemStack> openContainerGui(Level world, Player player,
            InteractionHand hand, ContainerSupplier<T> supplier, ContainerGuiEncoder encoder)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (hand == InteractionHand.OFF_HAND)
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);

        if (!world.isClientSide)
        {
            MenuProvider containerProvider = new ItemContainerProvider<T>(itemStack, supplier);
            NetworkHooks.openScreen((ServerPlayer) player, containerProvider, (FriendlyByteBuf) ->
            {
                encoder.encode(player, itemStack, FriendlyByteBuf);
            });
        }

        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);
    }

    public static <T extends AbstractContainerMenu> InteractionResultHolder<ItemStack> openContainerGui(Level world, Player player,
            InteractionHand hand, SimpleContainerSupplier<T> supplier, ContainerGuiEncoder encoder)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (hand == InteractionHand.OFF_HAND)
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);

        if (!world.isClientSide)
        {
            MenuProvider containerProvider = new ItemContainerProvider<T>(itemStack, supplier);
            NetworkHooks.openScreen((ServerPlayer) player, containerProvider, (FriendlyByteBuf) ->
            {
                encoder.encode(player, itemStack, FriendlyByteBuf);
            });
        }

        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemStack);
    }

    private ItemStack itemStack;
    private ContainerSupplier<T> supplier;
    private SimpleContainerSupplier<T> simpleSupplier;
}
