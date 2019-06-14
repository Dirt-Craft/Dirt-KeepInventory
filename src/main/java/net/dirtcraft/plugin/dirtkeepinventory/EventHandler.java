package net.dirtcraft.plugin.dirtkeepinventory;

import net.dirtcraft.plugin.dirtkeepinventory.Commands.CommandManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;

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
        Object cause = event.getTargetEntity();

        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;
        if (!player.hasPermission(Utility.Permissions.ENABLED)) return;
        //event.setMessage(Utility.format("&7" + event.getMessage().toPlain().replace(player.getName(), "&r&6" + player.getName() + "&7")));

        if (Utility.hasSoulboundItem(player)) {
            player.sendMessage(Utility.format("&cAn item with Soulbound has been detected and removed from your inventory!"));
        }

        event.setKeepInventory(true);
    }

}

