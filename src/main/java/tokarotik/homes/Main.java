package tokarotik.homes;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

import tokarotik.homes.commands.DeleteHome;
import tokarotik.homes.commands.ListHomes;
import tokarotik.homes.commands.SetHome;
import tokarotik.homes.commands.TpHome;
import tokarotik.homes.storage.HomePosition;

public class Main extends JavaPlugin implements Listener
{

    private HomePosition home;
    private String defualt_homename = "HOME";

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        home = new HomePosition(getDataFolder().getPath(), defualt_homename);

        FileConfiguration config = getConfig();

        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("sethome").setExecutor(new SetHome(home, config, defualt_homename)); // command set home
        this.getCommand("home").setExecutor(new TpHome(home, config, defualt_homename)); // command tp to home
        this.getCommand("delhome").setExecutor(new DeleteHome(home, config, defualt_homename)); // command to delete home
        this.getCommand("homes").setExecutor(new ListHomes(home, config, defualt_homename)); // command to show all homes

        getLogger().info("Homes plugin loaded!"); // console: plugin loaded

    }


}