package net.dirtcraft.plugin.dirtkeepinventory;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.storage.WorldProperties;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class Utility {

    public static ArrayList<UUID> deathList = new ArrayList<>();

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static boolean hasSoulboundItem(Player player) {
        boolean hasSoulboundItem = false;

        // For each normal inventory slot.
        for (Inventory slot : player.getInventory().slots()) {
            if (!slot.peek().isPresent()) continue;
            // Check if the item is even enchanted.
            if (!slot.peek().get().get(Keys.ITEM_ENCHANTMENTS).isPresent()) continue;
            // We send the stack because its both probably more efficient than rechecking every time needed.
            // And also because I can't send a bauble slot but only an ItemStack.
            hasSoulboundItem = checkInvSlots(hasSoulboundItem, slot);
        }

        // Check if the pack has Baubles.
        if (BaublesApi.getBaublesHandler((EntityPlayer) player) != null) {
            //For each bauble slot.
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) player);
            for (int i = 0; i < baubles.getSlots(); i++){
                if (baubles.getStackInSlot(i).isEmpty()) continue;
                if (!baubles.getStackInSlot(i).isItemEnchanted()) continue;
                // Gotta call another one because 'haha no Sponge ItemStacks funniii' :)
                hasSoulboundItem = checkBaubleSlots(hasSoulboundItem, baubles, i);
            }
        }
        return hasSoulboundItem;
    }

    //Bauble slots, using Forge                                     WHY can't it shit out sponge stacks -_-
    //This is making me cry.
    private static boolean checkBaubleSlots(boolean hasSoulbound, IBaublesItemHandler baubles, int curSlot){
        Optional<net.minecraft.enchantment.Enchantment> cofhSoulbound = Optional.ofNullable(net.minecraft.enchantment.Enchantment.getEnchantmentByLocation("cofh:soulbound"));
        Optional<net.minecraft.enchantment.Enchantment> enderioSoulbound = Optional.ofNullable(net.minecraft.enchantment.Enchantment.getEnchantmentByLocation("enderio:soulbound"));

        net.minecraft.item.ItemStack vanillaStack = baubles.getStackInSlot(curSlot);
        if(enderioSoulbound.isPresent()){
            NBTTagList enchantments = vanillaStack.getEnchantmentTagList();
            for (int i = 0; i < enchantments.tagCount(); i++) {
                NBTTagCompound enchant = enchantments.getCompoundTagAt(i);
                enchant.removeTag("ench");
            }

            baubles.extractItem(curSlot, vanillaStack.getCount(), true);
            baubles.insertItem(curSlot, vanillaStack, true);

            // ANd then here apply that shit back to the item

            // And then see what the fuck baubles is whining about about not being allowed to just set Items in their slots.
        }




        return hasSoulbound;
    }

    //Vanilla inventory slots, using Sponge.
    private static boolean checkInvSlots(boolean hasSoulbound, Inventory slot){
        // Better than checking everytime.
        ItemStack stack = slot.peek().get();
        // Get enchantment data from the item.
        EnchantmentData enchantmentData = stack.getOrCreate(EnchantmentData.class).get();
        // Cleanse the enchantmentData
        EnchantmentData newEnchantData = removeSoul(enchantmentData, stack);
        // Check if anything has changes, aka if any Souls have been harvested.
        if(enchantmentData.asList().containsAll(newEnchantData.asList())){
            if(!hasSoulbound) hasSoulbound = true;
            // setting stack to the new cleansed EnchantmentData
            stack.offer(newEnchantData);
            // Setting slot with the item - SoulBound enchants.
            slot.set(stack);
        }

        return hasSoulbound;
    }

    // As in the enchantments.
    private static EnchantmentData removeSoul(EnchantmentData enchantmentData, ItemStack stack){
        Optional<EnchantmentType> cofhSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "cofhcore:soulbound");
        Optional<EnchantmentType> enderioSoulbound = Sponge.getRegistry().getType(EnchantmentType.class, "enderio:soulbound");

        if (enderioSoulbound.isPresent()) {
            Enchantment soulbound = Enchantment.builder().type(enderioSoulbound.get()).level(1).build();
            if (stack.get(Keys.ITEM_ENCHANTMENTS).get().contains(soulbound)) {
                enchantmentData.remove(soulbound); // Remove it from the list.
            }
        }

        if (cofhSoulbound.isPresent()) {
            // Start at 1 go to 3 (For all 3 levels of CofHSoulBound - mainly done to reduce space + its cleaner)
            for (int i = 1; i < 4; i++) {
                Enchantment cofhSB = Enchantment.builder().type(cofhSoulbound.get()).level(i).build();
                if (stack.get(Keys.ITEM_ENCHANTMENTS).get().contains(cofhSB)) {
                    enchantmentData.remove(cofhSB); // Remove it from the list.
                }
            }
        }

        return enchantmentData;
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
        return new AbstractMap.SimpleEntry<>(Economy.withdrawBalance(player, fee), fee);
    }

    public static class Pagination {
        public static final String TITLE = "&cIf you read this, you're dead.";
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
