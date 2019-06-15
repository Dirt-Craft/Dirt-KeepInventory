package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import net.dirtcraft.plugin.dirtkeepinventory.DirtKeepInventory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    public CommandManager() {
        Sponge.getCommandManager().register(DirtKeepInventory.getInstance(), this.Base(),
                "kp", "keepinv", "keepinventory");
    }

    private CommandSpec Base() {
        Map<String, String> toggle = new HashMap<String, String>() {{
            put("on", "on");
            put("off", "off");
        }};
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(
                        GenericArguments.choices(Text.of("status"), toggle)))
                .description(Text.of("Base command for " + DirtKeepInventory.getContainer().getName()))
                .executor(new Base())
                .build();
    }
}
