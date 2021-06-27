package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import net.dirtcraft.plugin.dirtkeepinventory.utility.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Set implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String group = args.requireOne("group");
        int value = args.requireOne("cost");
        boolean success = Utility.setKeepInvFee(group, value);
        return success? CommandResult.success(): CommandResult.empty();
    }
}
