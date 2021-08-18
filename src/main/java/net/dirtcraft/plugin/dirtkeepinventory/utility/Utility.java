package net.dirtcraft.plugin.dirtkeepinventory.utility;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.*;

public class Utility {
    private static Set<String> blacklisted = new HashSet<>(Arrays.asList(
            "cofhcore:soulbound",
            "enderio:soulbound"));

    public static ArrayList<UUID> deathList = new ArrayList<>();
    // But like honestly, despite how disgusting this looks.
    // Throwing it around a bunch in a bunch of functions is not it either.

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static void filterSoulboundItems(Player player) {
        InventoryHelper.INSTANCE.mapEnchanted(player, Utility::removeSoul);
    }

    // As in the enchantments.
    private static ItemStack removeSoul(ItemStack stack){
        EnchantmentData enchantmentData = stack.getOrCreate(EnchantmentData.class)
                .orElseThrow(()->new IllegalArgumentException("getOrCreate did not return"));
        if(!enchantmentData.enchantments().isEmpty())
            stack.offer(enchantmentData.removeAll(e->blacklisted.contains(e.getType().getId())));
        return stack;
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

    public static boolean isExempt(Player player){
        return player.hasPermission(Permissions.EXEMPT);
    }

    public static int getKeepInvDiscount(Player playa){
        return PermissionHelper.INSTANCE.getMetaInt(playa, Permissions.META_DISCOUNT, 0);
    }

    public static int getKeepInvFee(Player playa){
        int base = PermissionHelper.INSTANCE.getMetaInt(playa, Permissions.META_COST, 0);
        int discount = getKeepInvDiscount(playa);
        if (discount == 0 || base == 0) return base;
        else return (int) (base * ((100 - discount) / 100f));
    }

    public static boolean setKeepInvFee(String group, int val){
        return PermissionHelper.INSTANCE.setMetaInt(group, Permissions.META_COST, val);
    }

    public static Map.Entry<Boolean, Integer> tryChargePlayer(Player player) {
        int fee = getKeepInvFee(player);
        if (isExempt(player)) return new AbstractMap.SimpleEntry<>(true, 0);
        else return new AbstractMap.SimpleEntry<>(EconomyHelper.withdrawBalance(player, fee), fee);
    }

    public static class Pagination {
        public static final String TITLE = "&cDirtCraft &6Keep Inventory";
        public static final String PADDING = "&4&m-";
    }

    public static class Permissions {
        public static final String SET_COST = "dirtkeepinventory.cost.set";
        public static final String META_COST = "dirtkeepinventory.cost.value";
        public static final String META_DISCOUNT = "dirtkeepinventory.cost.discount";
        public static final String ENABLED = "dirtkeepinventory.enabled";
        public static final String EXEMPT = "dirtkeepinventory.exempt";
    }

    /*
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

     */

}
