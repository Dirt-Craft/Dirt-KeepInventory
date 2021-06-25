package net.dirtcraft.plugin.dirtkeepinventory.utility;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
//import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
    We abstract this away here for a few reasons.
    1. It's easier
    2. If the code points to classes that don't exist we get a classdef error which leads to classnotfound exceptions.
    the way this works, despite being in one file is each class is actually split into more during compile, so each class{}
    will be in it's own file, such as InventoryHelper.class, InventoryHelper$1.class, InventoryHelper$2.class.
 */
public abstract class InventoryHelper {
    public static InventoryHelper INSTANCE = getInstance();

    private static InventoryHelper getInstance(){
        try {
            //return the functioning helper, if the class exists.
            //We do this by using class.forname, which will err if the class is not found, but since it's not a class literal this class will compile
            Class.forName("baubles.api.BaublesApi");
            return new Baubles();
        } catch (Exception e) {
            //return a dummy impl. to prevent NPE's. This works because the interface is full of default methods.
            return new InventoryHelper() {};
        }
    }

    public void mapEnchanted(Player player, Function<ItemStack, ItemStack>  mapper){
        for (Inventory slot : player.getInventory().slots()) {
            //We utilize the optional to apply a filter, then which will make it not present if it does not quality.
            //Then we add to the list if it's present.
            if (!slot.peek().map(stack->stack.get(Keys.ITEM_ENCHANTMENTS)).isPresent()) continue;
            ItemStack stack = slot.poll().orElse(null);
            if (stack == null) continue;
            mapper.apply(stack);
            slot.set(stack);
        }
    }

    public static class Baubles extends InventoryHelper {
        @Override
        public void mapEnchanted(Player player, Function<ItemStack, ItemStack>  mapper){
            super.mapEnchanted(player, mapper);

            //Get and store the method, then check null later. Iterate and add to list
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) player);
            if (handler != null) {
                System.out.println("In Get baubles");
                //For each bauble slot.
                System.out.println("Got the Baubles");
                System.out.println(handler.getSlots());
                for (int i = 0; i < handler.getSlots(); i++){
                    // Casting the ForgeStack onto the Sponge Stack (Thanks Shiny <3)
                    ItemStack stack = (ItemStack)(Object) handler.getStackInSlot(i);
                    System.out.println("Created Stack thanks Shiny");
                    System.out.println(i);
                    if (stack.isEmpty()) continue;
                    System.out.println("Is not empty");
                    if (!stack.get(Keys.ITEM_ENCHANTMENTS).isPresent()) continue;
                    System.out.println("Is Enchanted");
                    mapper.apply(stack);
                    handler.setStackInSlot(i, (net.minecraft.item.ItemStack) (Object) stack);
                }
            }
        }
    }
}
