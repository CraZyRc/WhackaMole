package whackamole.whackamole;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Objective;

import java.util.Objects;

public class Econ {
    private Logger logger = Logger.getInstance();
    private static Econ instance;
    private Config config = Config.getInstance();
    private Objective Objective;
    
    public Economy econ = null;
    private Currency currencyType;

    enum Currency {
        SCOREBOARD,
        VAULT
    }

    private Econ(Main main) {
        try {
            this.currencyType = Currency.valueOf(this.config.CURRENCY);
        } catch (IllegalArgumentException e) {
            logger.error("ERROR: Invalid economy set in config");
        }
        switch (this.currencyType) {
            case VAULT        ->  {
                if (!this.setupEconomy(main)) {
                    logger.warning("Missing Vault dependency! (missing Economy plugin)");
                }
            }
            case SCOREBOARD   ->  this.Objective = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective(this.config.OBJECTIVE);
        }

    }

    public static Econ getInstance(Main main) {
        if (Econ.instance == null) {
            Econ.instance = new Econ(main);
        }
        return Econ.instance;
    }

    public static Econ getInstance() {
        if (Econ.instance == null) {
            Logger.getInstance().error("ERROR: Econ not yet instantiated in Main");
        }
        return Econ.instance;
    }


    public boolean setupEconomy(Main plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = rsp.getProvider();
                return econ != null;
            }
        }
    }

    public boolean has(Player player, double amount) {
        return switch (this.currencyType) {
            case VAULT -> econ.has(player, amount);

            case SCOREBOARD -> true;
        };
    }

    public void withdrawPlayer(Player player, double amount) {
        switch (this.currencyType) {
            case VAULT -> econ.withdrawPlayer(player, amount);
            case SCOREBOARD -> {

            }
        }
    }

}
