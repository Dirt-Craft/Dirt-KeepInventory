package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import net.dirtcraft.plugin.dirtkeepinventory.DirtKeepInventory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

    public CommandManager() {
        Sponge.getCommandManager().register(DirtKeepInventory.getInstance(), this.Base(), "keepinv");
    }

    private CommandSpec Base() {
        return CommandSpec.builder()
                .description(Text.of("Base command for " + DirtKeepInventory.getContainer().getName()))
                .executor(new Base())
                .build();
    }
}
