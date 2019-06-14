package net.dirtcraft.plugin.dirtkeepinventory.Commands;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import net.dirtcraft.plugin.dirtkeepinventory.Utility;
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

public class Base implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player))
            throw new CommandException(Utility.format("&cOnly a player can set their Keep Inventory status!"));
        Player player = (Player) source;

        ArrayList<String> statisticsHover = new ArrayList<String>() {{
            add("&5&nStatistics&r\n&r");
            add("&7Player&8: &6" + player.getName());
        }};

        StringBuilder deathPenalty = new StringBuilder();
        deathPenalty.append("&7Death Penalty: &6");
        if (player.hasPermission(Utility.Groups.VETERAN)) {
            deathPenalty.append("$200 Coins");
        } else if (player.hasPermission(Utility.Groups.MASTER)) {
            deathPenalty.append("$150 Coins");
        } else if (player.hasPermission(Utility.Groups.EXPERIENCED)) {
            deathPenalty.append("$125 Coins");
        } else if (player.hasPermission(Utility.Groups.CITIZEN)) {
            deathPenalty.append("$100 Coins");
        } else if (player.hasPermission(Utility.Groups.AMATEUR)) {
            deathPenalty.append("$75 Coins");
        } else if (player.hasPermission(Utility.Groups.BEGINNER)) {
            deathPenalty.append("$50 Coins");
        } else {
            deathPenalty.append("None");
        }

        if (player.getStatisticData().get(Statistics.DEATHS).isPresent()) {
            statisticsHover.add("&7Deaths&8: &6" + player.getStatisticData().get(Statistics.DEATHS).get().toString());
        } else {
            statisticsHover.add("&7Deaths&8: &6" + "N/A");
        }

        Text.Builder text = Text.builder();
        ArrayList<String> contents = new ArrayList<>();
        contents.add("");

        PaginationList.Builder pagination = Utility.getPagination();

        User user = Utility.getLuckPerms().getUser(player.getUniqueId());
        if (user == null) throw new CommandException(Utility.format("&cThere was an error retrieving your user data!"));

        if (player.hasPermission(Utility.Permissions.ENABLED)) {
            contents.add("&7Keep Inventory is currently &aenabled&7, click me to toggle!");

            text.onClick(TextActions.executeCallback(disable -> {
                Node node = Utility.getLuckPerms().buildNode(Utility.Permissions.ENABLED)
                        .setValue(false)
                        .build();
                user.setPermission(node);
                Sponge.getCommandManager().process(player, "keepinv");
            }));
        } else {
            contents.add("&7Keep Inventory is currently &cdisabled&7, click me to toggle!");

            text.onClick(TextActions.executeCallback(enable -> {
                Node node = Utility.getLuckPerms().buildNode(Utility.Permissions.ENABLED)
                        .setValue(true)
                        .build();
                user.setPermission(node);
                Sponge.getCommandManager().process(player, "keepinv");
            }));
        }
        contents.add("");

        text.append(Utility.format(String.join("\n", contents)));
        text.onHover(TextActions.showText(Utility.format(deathPenalty.toString())));



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
