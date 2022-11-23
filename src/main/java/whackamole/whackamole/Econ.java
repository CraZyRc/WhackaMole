package whackamole.whackamole;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {
    private Logger logger = Logger.getInstance();
    private static Econ instance;
    
    public Economy econ = null;


    public static Econ getInstance() {
        if (Econ.instance == null) {
            Econ.instance = new Econ();
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
}
