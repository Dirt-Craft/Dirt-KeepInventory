package net.dirtcraft.plugin.dirtkeepinventory;

import net.dirtcraft.plugin.dirtkeepinventory.Commands.CommandManager;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.Map;

public class EventHandler {

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

        if (!player.hasPermission(Utility.Permissions.ENABLED)) return;

        //
        PaginationList.Builder pagination = Utility.getPagination();
        ArrayList<String> contents = new ArrayList<>();
        contents.add("");

        Map.Entry<Boolean, Integer> keepInv = Utility.canKeepInventory(player);

        if (keepInv.getKey()) {
            int value = Utility.canKeepInventory(player).getValue();
            if (value > 0) {
                contents.add("&b" + player.getName() + "&7's inventory has been restored for &a$" + value);
            } else {
                contents.add("&b" + player.getName() + "&7's inventory has been restored for &a" + "free&7!");
            }
            event.setKeepInventory(true);
        } else {
            contents.add("&b" + player.getName() + "&7 does &cnot &7have enough funds to restore their inventory!");
            event.setKeepInventory(false);
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

        if (Utility.hasSoulboundItem(player)) {
            pagination.footer(Utility.format("&cAn item with Soulbound has been detected and removed from your inventory!"));
        }

        contents.add("");

        Text.Builder text = Text.builder();
        text.append(Utility.format(String.join("\n", contents)));
        text.onHover(TextActions.showText(Utility.format("&7Keep Inventory is &aenabled&7, click me to toggle")));
        text.onClick(TextActions.runCommand("/dirt-keepinventory:keepinv off"));

        pagination.contents(text.build());

        pagination.build().sendTo(player);
    }

}

