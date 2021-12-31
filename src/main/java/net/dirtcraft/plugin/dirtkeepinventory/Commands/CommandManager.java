package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import net.dirtcraft.plugin.dirtkeepinventory.DirtKeepInventory;
import net.dirtcraft.plugin.dirtkeepinventory.utility.PermissionHelper;
import net.dirtcraft.plugin.dirtkeepinventory.utility.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

    public CommandManager() {
        Sponge.getCommandManager().register(DirtKeepInventory.getInstance(), this.base(),"kp", "keepinv", "keepinventory");
    }

    private CommandSpec base() {
        return CommandSpec.builder()
                .child(set(), "set", "cost")
                .child(status(), "status", "enable")
                .build();
    }

    private CommandSpec status() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(
                        GenericArguments.bool(Text.of("value"))))
                .description(Text.of("Base command for " + DirtKeepInventory.getContainer().getName()))
                .executor(new Base())
                .build();
    }

    private CommandSpec set() {
        return CommandSpec.builder()
                .permission(Utility.Permissions.SET_COST)
                .arguments(GenericArguments.withSuggestions(GenericArguments.string(Text.of("group")), PermissionHelper.INSTANCE.getGroups()),
                        GenericArguments.integer(Text.of("cost")))
                .description(Text.of("Sets the cost of keep-inv for a specified group."))
                .executor(new Set())
                .build();
    }
}
