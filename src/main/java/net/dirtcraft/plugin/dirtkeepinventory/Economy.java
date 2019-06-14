package net.dirtcraft.plugin.dirtkeepinventory;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;

public class Economy {

    public static Optional<EconomyService> getEconomyService() {
        return Sponge.getServiceManager().provide(EconomyService.class);
    }

    public static BigDecimal getBalance(Player player) {
        if (!getEconomyService().isPresent()) return null;
        Optional<UniqueAccount> uOpt = getEconomyService().get().getOrCreateAccount(player.getUniqueId());
        if (!uOpt.isPresent()) return null;

        UniqueAccount acc = uOpt.get();
        return acc.getBalance(getEconomyService().get().getDefaultCurrency());

    }

    public static boolean withdrawBalance(Player player, int amount) {
        if (!getEconomyService().isPresent()) return false;
        if (!getEconomyService().get().getOrCreateAccount(player.getUniqueId()).isPresent()) return false;

        PluginContainer pluginContainer = DirtKeepInventory.getContainer();
        EconomyService service = getEconomyService().get();
        UniqueAccount uniqueAccount = getEconomyService().get().getOrCreateAccount(player.getUniqueId()).get();
        BigDecimal requiredAmount = BigDecimal.valueOf(amount);
        EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build();

        TransactionResult result = uniqueAccount.withdraw(service.getDefaultCurrency(), requiredAmount, Cause.of(eventContext, pluginContainer));

        return result.getResult() == ResultType.SUCCESS;
    }
}
