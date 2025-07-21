package tokarotik.homes.commands;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tokarotik.homes.storage.HomePosition;

public class SetHome implements CommandExecutor {

    private HomePosition home;
    private FileConfiguration config;
    private String defualt_homename;

    public SetHome(HomePosition home, FileConfiguration config, String defualt_homename)
    {
        this.home = home;
        this.config = config;
        this.defualt_homename = defualt_homename;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can use only player!");
            return true;
        }

        String homename = setNameHome(args);

        boolean isRewrite = home.hasHome(sender.getName(), homename);

        if (checkOnMaxHomes(sender) && !isRewrite)
        {
            sender.sendMessage(ChatColor.DARK_RED + "! You have reached the maximum homes count");
            return true;
        }


        setHome(sender, homename);

        sendMessage(sender, homename, isRewrite);

        return true;
    }

    private boolean checkOnMaxHomes(CommandSender sender)
    {
        int player_homes = home.getLengthHomes(sender.getName());
        int config_max_homes = config.getInt("limits-homes");

        return player_homes >= config_max_homes;
    }

    private String setNameHome(String[] args)
    {
        String homename = defualt_homename;

        if (args.length > 0)
        {
            homename = args[0];
        }

        return homename;
    }
    private void setHome(CommandSender sender, String homename)
    {
        Player player = (Player) sender;
        Location location = player.getLocation();
        World world = location.getWorld();

        home.writeHome(sender.getName(), homename, world.getName(), location);
    }

    private void sendMessage(CommandSender sender, String homename, boolean isRewrite)
    {
        String outhome = homename + " ";
        if (homename == defualt_homename)
        {
            outhome = "";
        }

        String status = "setted";
        if (isRewrite)
        {
            status = "rewrited";
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "* Home " + outhome + status);
    }
}