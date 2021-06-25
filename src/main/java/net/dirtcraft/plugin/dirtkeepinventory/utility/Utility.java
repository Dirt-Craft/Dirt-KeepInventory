package net.dirtcraft.plugin.dirtkeepinventory.utility;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.*;

public class Utility {

    public static ArrayList<UUID> deathList = new ArrayList<>();
    // But like honestly, despite how disgusting this looks.
    // Throwing it around a bunch in a bunch of functions is not it either.
    private static boolean hasSoulbound;

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static boolean hasSoulboundItem(Player player) {
        hasSoulbound = false;

        List<ItemStack> items = InventoryHelper.INSTANCE.getEnchanted(player);
        for (ItemStack stack : items) {
            if (stripSoulbound(stack)) hasSoulbound = true;
        }

        return hasSoulbound;
    }

    //By abstracting things, we can combine previously seperated code.
    public static boolean stripSoulbound(ItemStack stack){
        System.out.println("Getting enchantData");
        EnchantmentData oldData = stack.getOrCreate(EnchantmentData.class).orElseThrow(()->new IllegalArgumentException("getOrCreate did not return"));
        EnchantmentData newData = removeSoul(oldData, stack);
        //Not that we need this, since we can just set but this works O(1) and is just as good as the old O(n2) method, which has to iterate over everything.
        //Since we are only removing, not modifying we can ascertain that if the size is the same, it is unchanged. If it changes, the data has changed.
        if(oldData.asList().size() != newData.asList().size()){
            System.out.println("Enchants Differ!");
            // setting stack to the new cleansed EnchantmentData
            stack.offer(newData);
            // Setting slot with the item - SoulBound enchants.
            /* Why are we extracting and putting back the same thing?
            System.out.println("Extracting");
            baubles.extractItem(curSlot, stack.getQuantity(), false);
            System.out.println("Inserting");
            baubles.insertItem(curSlot, (net.minecraft.item.ItemStack)(Object) stack, false);
            System.out.println("Done, returning SoulBound");
             */
            return true;
        }
        return false;
    }

    // As in the enchantments.
    private static EnchantmentData removeSoul(EnchantmentData enchantmentData, ItemStack stack){
        Optional<EnchantmentType> cofhSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound");
        Optional<EnchantmentType> enderioSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "enderio:soulbound");

        if (enderioSoulbound.isPresent()) {
            Enchantment soulbound = Enchantment.builder().type(enderioSoulbound.get()).level(1).build();
            if (stack.get(Keys.ITEM_ENCHANTMENTS).get().contains(soulbound)) {
                enchantmentData.remove(soulbound); // Remove it from the list.
                setSoulboundTrue();
            }
        }

        if (cofhSoulbound.isPresent()) {
            // Start at 1 go to 3 (For all 3 levels of CofHSoulBound - mainly done to reduce space + its cleaner)
            for (int i = 1; i < 4; i++) {
                Enchantment cofhSB = Enchantment.builder().type(cofhSoulbound.get()).level(i).build();
                if (stack.get(Keys.ITEM_ENCHANTMENTS).get().contains(cofhSB)) {
                    System.out.println("removing CofH " + i);
                    enchantmentData.remove(cofhSB); // Remove it from the list.
                    setSoulboundTrue();
                }
            }
        }

        return enchantmentData;
    }

    private static void setSoulboundTrue(){
        if(!hasSoulbound) hasSoulbound = true;
    }

    public static void setGamerule() {
        for (WorldProperties worldProperties : Sponge.getServer().getAllWorldProperties()) {
            worldProperties.setGameRule("keepInventory", "false");
        }
    }

    public static PaginationList.Builder getPagination() {
        return PaginationList.builder()
                .title(format(Pagination.TITLE))
                .padding(format(Pagination.PADDING));
    }

    public static LuckPermsApi getLuckPerms() {
        return LuckPerms.getApi();
    }

    public static Map.Entry<Boolean, Integer> canKeepInventory(Player player) {
        int fee = 0;
        if (player.hasPermission(Permissions.EXEMPT)) {
            return new AbstractMap.SimpleEntry<>(true, fee);
        }

        if (player.hasPermission(Groups.VETERAN)) {
            fee = Groups.GROUP_FEE.get("veteran");
        } else if (player.hasPermission(Groups.MASTER)) {
            fee = Groups.GROUP_FEE.get("master");
        } else if (player.hasPermission(Groups.EXPERIENCED)) {
            fee = Groups.GROUP_FEE.get("experienced");
        } else if (player.hasPermission(Groups.CITIZEN)) {
            fee = Groups.GROUP_FEE.get("citizen");
        } else if (player.hasPermission(Groups.AMATEUR)) {
            fee = Groups.GROUP_FEE.get("amateur");
        } else if (player.hasPermission(Groups.BEGINNER)) {
            fee = Groups.GROUP_FEE.get("beginner");
        }
        // Added this here, and removed it from all the if's.
        return new AbstractMap.SimpleEntry<>(EconomyHelper.withdrawBalance(player, fee), fee);
    }

    public static class Pagination {
        public static final String TITLE = "&cDirtCraft &6Keep Inventory";
        public static final String PADDING = "&4&m-";
    }

    public static class Permissions {
        public static final String ENABLED = "dirtkeepinventory.enabled";
        public static final String EXEMPT = "dirtkeepinventory.exempt";
    }

    public static class Groups {
        public static final String BEGINNER = "group.beginner";
        public static final String AMATEUR = "group.amateur";
        public static final String CITIZEN = "group.citizen";
        public static final String EXPERIENCED = "group.experienced";
        public static final String MASTER = "group.master";
        public static final String VETERAN = "group.veteran";

        public static final HashMap<String, Integer> GROUP_FEE = new HashMap<String, Integer>() {{
            put("beginner", 50);
            put("amateur", 75);
            put("citizen", 100);
            put("experienced", 125);
            put("master", 150);
            put("veteran", 200);
        }};
    }

}
