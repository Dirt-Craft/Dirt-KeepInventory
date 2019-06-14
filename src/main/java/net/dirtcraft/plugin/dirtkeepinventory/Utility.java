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

import java.util.HashMap;
import java.util.Optional;

public class Utility {

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static boolean hasSoulboundItem(Player player) {

        boolean hasSoulboundItem = false;

        Optional<EnchantmentType> cofhSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound");
        Optional<EnchantmentType> enderioSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "enderio:soulbound");

        for (Inventory slot : player.getInventory().slots()) {
            if (!slot.peek().isPresent()) continue;
            if (!slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).isPresent()) continue;

            if (enderioSoulbound.isPresent()) {
                Enchantment soulbound = Enchantment.builder()
                        .type(Sponge.getRegistry().getType(EnchantmentType.class, "enderio:soulbound").get())
                        .level(1)
                        .build();
                if (slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).get().contains(soulbound)) {
                    slot.poll();
                    if (!hasSoulboundItem) hasSoulboundItem = true;
                }
            }

            if (cofhSoulbound.isPresent()) {
                Enchantment cofh1Soulbound = Enchantment.builder()
                        .type(Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound").get())
                        .level(1)
                        .build();
                Enchantment cofh2Soulbound = Enchantment.builder()
                        .type(Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound").get())
                        .level(2)
                        .build();
                Enchantment cofh3Soulbound = Enchantment.builder()
                        .type(Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound").get())
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

        public static final HashMap<String, Integer> groupFee = new HashMap<String, Integer>() {{
            put("beginner", 50);
            put("amateur", 75);
            put("citizen", 100);
            put("experienced", 125);
            put("master", 150);
            put("veteran", 200);
        }};
    }

}
