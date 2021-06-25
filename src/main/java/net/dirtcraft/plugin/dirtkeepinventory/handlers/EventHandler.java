package net.dirtcraft.plugin.dirtkeepinventory.handlers;

import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.plugin.dirtkeepinventory.Commands.CommandManager;
import net.dirtcraft.plugin.dirtkeepinventory.DirtKeepInventory;
import net.dirtcraft.plugin.dirtkeepinventory.utility.Utility;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventHandler {

    private final boolean isPixelmon = SpongeDiscordLib.getServerName().toLowerCase().contains("pixel");

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        Utility.setGamerule();
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        new CommandManager();
    }

    @Listener (order = Order.FIRST, beforeModifications = true)
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Living cause = event.getTargetEntity();

        if (!(cause instanceof Player)) {
            event.setMessageCancelled(true);
            return;
        }
        Player player = (Player) cause;
        event.setMessage(Utility.format("&7" + event.getMessage().toPlain().replace(player.getName(), "&r&6" + player.getName() + "&7")));

        if (!player.hasPermission(Utility.Permissions.ENABLED)) {
            event.setKeepInventory(false);
            return;
        }

        if (SpongeDiscordLib.getServerName().equalsIgnoreCase("rl craft")) {
            event.setKeepInventory(true);
            return;
        }

        if (Utility.deathList.contains(player.getUniqueId()) && !isPixelmon) {
            event.setKeepInventory(true);
            player.sendMessage(Utility.format("&7You are currently under a &b60 &7second grace period"));
            return;
        }

        //

        PaginationList.Builder pagination = Utility.getPagination();
        ArrayList<String> contents = new ArrayList<>();
        contents.add("");

        if (isPixelmon) {
            contents.add("&b" + player.getName() + "&7's inventory has been &arestored");
            event.setKeepInventory(true);
        } else {

        if (Utility.hasSoulboundItem(player)) {
            contents.add("&cAn item with Soulbound has been detected & unenchanted.");
        }

        Map.Entry<Boolean, Integer> keepInv = Utility.canKeepInventory(player);

            if (keepInv.getKey()) {
                int value = keepInv.getValue();
                if (value > 0) {
                    contents.add("&b" + player.getName() + "&7's inventory has been restored for &a$" + value);
                } else {
                    contents.add("&b" + player.getName() + "&7's inventory has been restored for &a" + "free&7!");
                }
                Utility.deathList.add(player.getUniqueId());
                Task.builder()
                        .async()
                        .delay(1, TimeUnit.MINUTES)
                        .execute(() -> Utility.deathList.remove(player.getUniqueId()))
                        .submit(DirtKeepInventory.getInstance());
                event.setKeepInventory(true);
            } else {
                contents.add("&b" + player.getName() + "&7 does &cnot &7have enough funds to restore their inventory!");
                event.setKeepInventory(false);
            }
        }

        /*
                ░░░░░░░░░░░░░░░░░░░░░░░░░░░█░░░░
              ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░█░░░█░░
             ░░█████░░░░░░░░█░░░░░░░░░░█████████░░░
            ░░░█░░░░██░░░░░░░░░░░░░░░░░░░░░██░░░░░░
            ░░░█░░░░░██░░░░░░░░░░░░░░███░░░░█░░░░░░
            ░░░█░░░░░░█░░░░░░░░░░░████░█░░░░█░░░░░░░
            ░░░█░░░░░░░█░░░░█░░░░░█░░░░░░░░░██░░░░░░
            ░░░█░░░████░░░░░█░░░░░█░░░░░░░░░░██░░░░░
            ░░░█░███░░░░░░░░█░░░░░█░░░░░░░░░░░█░░░
             ░░███░░░░░░░░░░█░░░░░██░░░░░░░░░░░░░
              ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
         */

        contents.add("");

        Text.Builder text = Text.builder();
        text.append(Utility.format(String.join("\n", contents)));
        text.onHover(TextActions.showText(Utility.format("&7Keep Inventory is &aenabled&7, click me to toggle")));
        text.onClick(TextActions.runCommand("/dirt-keepinventory:keepinv off"));

        pagination.contents(text.build());

        pagination.build().sendTo(player);
    }

}

