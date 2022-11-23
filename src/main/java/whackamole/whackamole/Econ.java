package whackamole.whackamole;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {
    private static Econ instance;
    private Logger logger = Logger.getInstance();
    public Economy econ = null;


    public static Econ getInstance() {
        if (Econ.instance == null) {
            Econ.instance = new Econ();
        }
        return Econ.instance;
    }


    public boolean setupEconomy(Main plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.error("vault not found");
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                logger.error("something rsp");
                return false;
            } else {
                logger.info("successfull?");
                econ = (Economy)rsp.getProvider();
                return econ != null;
            }
        }
    }
}
