package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import net.dirtcraft.plugin.dirtkeepinventory.utility.PermissionHelper;
import net.dirtcraft.plugin.dirtkeepinventory.utility.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.statistic.Statistics;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Base implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can set their Keep Inventory status!"));
        Player player = (Player) source;

        if (args.<Boolean>getOne("value").isPresent()) {
            boolean newValue = args.<Boolean>getOne("value").get();
            PermissionHelper.INSTANCE.setPerm(player, Utility.Permissions.ENABLED, newValue);
            Sponge.getCommandManager().process(player, "dirt-keepinventory:keepinv");
            return CommandResult.success();
        }

        List<String> statisticsHover = Arrays.asList(
                "&5&nStatistics&r\n&r",
                "&7Player&8: &6" + player.getName(),
                "&7Deaths&8: &6" + player.getStatisticData().get(Statistics.DEATHS).map(String::valueOf).orElse("N/A")
        );

        final String cost;
        final int fee = Utility.getKeepInvFee(player);
        if (Utility.isExempt(player)) cost = "Exempt";
        else if (fee == 0) cost = "None";
        else cost = String.valueOf(fee);

        Text.Builder text = Text.builder();
        ArrayList<String> contents = new ArrayList<>();
        contents.add("");

        PaginationList.Builder pagination = Utility.getPagination();

        if (player.hasPermission(Utility.Permissions.ENABLED)) {
            contents.add("&7Keep Inventory is currently &aenabled&7, click me to toggle!");
            text.onClick(TextActions.executeCallback(disable -> Sponge.getCommandManager().process(player, "dirt-keepinventory:keepinv enable 0")));
        } else {
            contents.add("&7Keep Inventory is currently &cdisabled&7, click me to toggle!");
            text.onClick(TextActions.executeCallback(enable -> Sponge.getCommandManager().process(player, "dirt-keepinventory:keepinv enable 1")));
        }
        contents.add("");

        text.append(Utility.format(String.join("\n", contents)));
        text.onHover(TextActions.showText(Utility.format("&7Keep Inventory Fee: &6" + cost)));

        pagination.contents(text.build());

        pagination.footer(
                Text.builder()
                .append(Utility.format("&8[&dHover For Statistics&8]"))
                .onHover(TextActions.showText(Utility.format(String.join("\n", statisticsHover))))
                .build());
        pagination.build().sendTo(player);

        return CommandResult.success();
    }

}
