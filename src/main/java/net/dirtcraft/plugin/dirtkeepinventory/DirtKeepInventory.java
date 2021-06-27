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
        description = "DirtCraft's keep inventory management plugin",
        url = "https://dirtcraft.net",
        authors = {
                "juliann",
                "shinyafro",
                "hipjehopje"
        },
        dependencies = {@Dependency(id = "luckperms")}
)
public class DirtKeepInventory {
    @Inject private PluginContainer container;
    private static DirtKeepInventory instance;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        Sponge.getEventManager().registerListeners(instance, new EventHandler());
    }

    public static DirtKeepInventory getInstance() {
        return instance;
    }

    public static PluginContainer getContainer() {
        return instance.container;
    }
}
