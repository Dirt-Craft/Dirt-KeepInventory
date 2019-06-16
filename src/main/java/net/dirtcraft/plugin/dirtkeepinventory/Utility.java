package net.dirtcraft.plugin.dirtkeepinventory;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.*;

public class Utility {

    public static ArrayList<UUID> deathList = new ArrayList<>();

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static boolean hasSoulboundItem(Player player) {

        boolean hasSoulboundItem = false;

        Optional<EnchantmentType> cofhSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound");
        Optional<EnchantmentType> enderioSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "enderio:soulbound");
        Optional<EnchantmentType> sharpness = Sponge.getRegistry().getType(EnchantmentType.class, "minecraft:sharpness");

        for (Inventory slot : player.getInventory().slots()) {
            if (!slot.peek().isPresent()) continue;
            if (!slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).isPresent()) continue;

            if (enderioSoulbound.isPresent()) {
                Enchantment soulbound = Enchantment.builder()
                        .type(enderioSoulbound.get())
                        .level(1)
                        .build();
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(soulbound)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
            }

            if (cofhSoulbound.isPresent()) {
                Enchantment cofh1Soulbound = Enchantment.builder()
                        .type(cofhSoulbound.get())
                        .level(1)
                        .build();
                Enchantment cofh2Soulbound = Enchantment.builder()
                        .type(cofhSoulbound.get())
                        .level(2)
                        .build();
                Enchantment cofh3Soulbound = Enchantment.builder()
                        .type(cofhSoulbound.get())
                        .level(3)
                        .build();
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(cofh1Soulbound)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(cofh2Soulbound)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(cofh3Soulbound)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
            }

            if (sharpness.isPresent()) {
                Enchantment s = Enchantment.builder()
                        .level(1)
                        .type(sharpness.get())
                        .build();
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(s)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
            }
        }

        return hasSoulboundItem;
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
        int fee;
        if (player.hasPermission(Permissions.EXEMPT)) {
            fee = 0;
            return new AbstractMap.SimpleEntry<>(true, fee);
        }

        if (player.hasPermission(Groups.VETERAN)) {
            fee = Groups.GROUP_FEE.get("veteran");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        } else if (player.hasPermission(Groups.MASTER)) {
            fee = Groups.GROUP_FEE.get("master");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        } else if (player.hasPermission(Groups.EXPERIENCED)) {
            fee = Groups.GROUP_FEE.get("experienced");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        } else if (player.hasPermission(Groups.CITIZEN)) {
            fee = Groups.GROUP_FEE.get("citizen");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        } else if (player.hasPermission(Groups.AMATEUR)) {
            fee = Groups.GROUP_FEE.get("amateur");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        } else if (player.hasPermission(Groups.BEGINNER)) {
            fee = Groups.GROUP_FEE.get("beginner");
            return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
        }

        fee = 0;
        return new AbstractMap.SimpleEntry<>(true, fee);
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
