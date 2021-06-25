package net.dirtcraft.plugin.dirtkeepinventory;

import com.google.inject.Inject;
import net.dirtcraft.plugin.dirtkeepinventory.handlers.EventHandler;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
        id = "dirt-keepinventory",
        name = "Dirt KeepInventory",
        description = "DirtCraft's keep inventory managment plugin",
        url = "https://dirtcraft.net",
        authors = {
                "juliann"
        },
        dependencies = {
                @Dependency(id = "luckperms", optional = true)
        }
)
public class DirtKeepInventory {

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private static DirtKeepInventory instance;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        if (!Sponge.getPluginManager().isLoaded("luckperms")) {
            logger.error("LuckPerms is not installed, " + container.getName() + " will not run!");
            return;
        }
        Sponge.getEventManager().registerListeners(instance, new EventHandler());
    }

    public static DirtKeepInventory getInstance() {
        return instance;
    }

    public static PluginContainer getContainer() {
        return instance.container;
    }
}
