package whackamole.whackamole;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import static org.bukkit.Bukkit.getServer;


public class Econ {
    private static Objective Objective;
    public static Economy econ = null;

    public static Currency currencyType = Currency.NULL;

    enum Currency {
        NULL,
        SCOREBOARD,
        VAULT
    }

    public Econ() {
    }

    public static void onEnable(Plugin plugin) {
        try {
            currencyType = Currency.valueOf(Config.Currency.ECONOMY);
        } catch (IllegalArgumentException e) {
            Logger.error(Translator.ECON_INVALIDECONOMY);
        }

        switch (currencyType) {
            case VAULT -> {
                if (!setupEconomy()) {
                    Logger.error(Translator.ECON_INVALIDVAULT);
                    currencyType = Currency.NULL;
                    plugin.getPluginLoader().disablePlugin(plugin);
                }
            }
            case SCOREBOARD -> {
                if (!setupScoreboard(Config.Currency.OBJECTIVE)) {
                    Logger.error(Translator.ECON_INVALIDOBJECTIVE);
                    currencyType = Currency.NULL;
                }
            }
            case NULL -> {}
        }
    }


    private static boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
                    .getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            econ = rsp.getProvider();
            return econ != null;

        }
    }

    private static boolean setupScoreboard(String objective) {
        try {
            if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective) != null) {
                Objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean has(Player player, double amount) {
        return switch (currencyType) {
            case NULL -> false;
            case VAULT -> econ.has(player, amount);
            case SCOREBOARD -> this.getScore(player).getScore() >= amount;

        };
    }

    public void withdrawPlayer(Player player, double amount) {
        switch (currencyType) {
            case VAULT -> econ.withdrawPlayer(player, amount);
            case SCOREBOARD -> this.remScore(this.getScore(player), amount);
            case NULL -> {
            }
        }
    }

    public void depositPlayer(Player player, double amount) {
        try {
            switch (currencyType) {
                case VAULT -> econ.depositPlayer(player, amount);
                case SCOREBOARD -> this.addScore(this.getScore(player), amount);
                case NULL -> {
                }
            }
        } catch (Exception e) {
        }

    }

    private Score getScore(Player player) {
        return Objective.getScore(player.getName());
    }

    private void addScore(Score score, double amount) {
        score.setScore((int) (score.getScore() + amount));
    }

    private void remScore(Score score, double amount) {
        score.setScore((int) (score.getScore() - amount));
    }
}
