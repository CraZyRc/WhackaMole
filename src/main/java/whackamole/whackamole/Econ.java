package whackamole.whackamole;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

public class Econ {
    private Logger logger = Logger.getInstance();
    private static Econ Instance;
    private Config config = Config.getInstance();
    private Objective Objective;
    
    public Economy econ = null;
    public Currency currencyType = Currency.NULL;

    enum Currency {
        NULL,
        SCOREBOARD,
        VAULT
    }

    private Econ(Main main) {
        try {
            this.currencyType = Currency.valueOf(this.config.ECONOMY);
        } catch (IllegalArgumentException e) {
            this.logger.error("Invalid economy set in config");
        }
        switch (this.currencyType) {
            case VAULT        ->  {
                if (!this.setupEconomy(main)) {
                    this.logger.error("Missing Vault dependency! (missing Economy plugin)");
                }
            }
            case SCOREBOARD   -> {
                if (!this.setupScoreboard(this.config.OBJECTIVE)) {
                    this.logger.error("Invalid objective in Config");
                }
            }
        }

    }

    public static Econ getInstance(Main main) {
        if (Econ.Instance == null) {
            Econ.Instance = new Econ(main);
        }
        return Econ.Instance;
    }

    public static Econ getInstance() {
        if (Econ.Instance == null) {
            Logger.getInstance().error("Econ not yet instantiated in Main");
        }
        return Econ.Instance;
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

    public boolean setupScoreboard(String objective) {
        if (!(this.config.OBJECTIVE == null)) {
            this.Objective = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective(objective);
            return true;
        } else {
            return false;
        }
    }
    public boolean has(Player player, double amount) {
        return switch (this.currencyType) {
            case NULL           ->      false;
            case VAULT          ->      econ.has(player, amount);
            case SCOREBOARD     ->      this.getScore(player).getScore() >= amount;

        };
    }

    public void withdrawPlayer(Player player, double amount) {
        switch (this.currencyType) {
            case VAULT          ->          econ.withdrawPlayer(player, amount);
            case SCOREBOARD     ->          this.remScore(this.getScore(player), amount);
        }
    }

    public void depositPlayer(Player player, double amount) {
        switch (this.currencyType) {
            case VAULT          ->          econ.depositPlayer(player, amount);
            case SCOREBOARD     ->          this.addScore(this.getScore(player), amount);
        }
    }

    public Score getScore(Player player) {
        return Objective.getScore(player.getName());
    }
    public void addScore(Score score, double amount) {
        score.setScore((int) (score.getScore() + amount));
    }
    public void remScore(Score score, double amount) {
        score.setScore((int) (score.getScore() - amount));
    }

    public static Econ reload(Main main) {
        Econ.Instance = null;
        return Econ.getInstance(main);
    }

}
