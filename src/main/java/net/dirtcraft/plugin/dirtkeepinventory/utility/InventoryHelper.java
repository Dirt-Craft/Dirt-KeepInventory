package net.dirtcraft.plugin.dirtkeepinventory.utility;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
    We abstract this away here for a few reasons.
    1. It's easier
    2. If the code points to classes that don't exist we get a classdef error which leads to classnotfound exceptions.
    the way this works, despite being in one file is each class is actually split into more during compile, so each class{}
    will be in it's own file, such as InventoryHelper.class, InventoryHelper$1.class, InventoryHelper$2.class.
 */
public interface InventoryHelper {
    InventoryHelper INSTANCE = INTERNAL.getInstance();
    default List<ItemStack> getEnchanted(Player player){
        // For each normal inventory slot.
        List<ItemStack> enchanted = new ArrayList<>();
        for (Inventory slot : player.getInventory().slots()) {
            //We utilize the optional to apply a filter, then which will make it not present if it does not quality.
            //Then we add to the list if it's present.
            slot.peek().filter(stack->stack.get(Keys.ITEM_ENCHANTMENTS).isPresent())
                    .ifPresent(enchanted::add);
        }
        return enchanted;
    }

    class Baubles implements InventoryHelper {
        @Override
        public List<ItemStack> getEnchanted(Player player){
            List<ItemStack> enchanted = new ArrayList<>();
            for (Inventory slot : player.getInventory().slots()) {
                //We utilize the optional to apply a filter, then which will make it not present if it does not quality.
                //Then we add to the list if it's present.
                slot.peek().filter(stack->stack.get(Keys.ITEM_ENCHANTMENTS).isPresent())
                        .ifPresent(enchanted::add);
            }

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
                    // Gotta call another one because Baubles can't use setSlot
                    enchanted.add(stack);
                }
            }

            return enchanted;
        }
    }

    // Interfaces cannot have private stuff, so we hide stuff in here to keep things clean so we can't accidently call any of this.
    class INTERNAL {
        private static List<ItemStack> empty = new ArrayList<>();
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
    }
}
