package tokarotik.homes.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tokarotik.homes.storage.HomePosition;

public class TpHome implements CommandExecutor {

    private HomePosition home;
    private FileConfiguration config;
    private String defualt_homename;

    public TpHome(HomePosition home, FileConfiguration config, String defualt_homename)
    {
        this.home = home;
        this.defualt_homename = defualt_homename;
        this.config = config;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can use only player!");
            return true;
        }

        String homename = setNameHome(args);

        if (!sendPlayerHas(sender, homename))
        {
            return true;
        }

        boolean is_teleported = teleportationPlayer(sender, homename);

        if (is_teleported)
        {
            sendMessage(sender, homename);
            return true;
        }

        return false;
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

    private boolean sendPlayerHas(CommandSender sender, String homename)
    {
        boolean isHas = home.hasHome(sender.getName(), homename);

        if (!isHas)
        {
            String homeout = " with name " + homename;
            if (homename == defualt_homename)
            {
                homeout = "";
            }

            sender.sendMessage(ChatColor.DARK_RED + "! You don't have home" + homeout);
            return false;
        }

        return true;
    }

    private boolean teleportationPlayer(CommandSender sender, String home)
    {
        Location location = this.home.readHome(sender.getName(), home);

        Player player = (Player) sender;

        if (location == null) {return false;}
        if (config.getBoolean("teleports-another-dimension") == false)
        {
            if (player.getWorld().getName() != location.getWorld().getName())
            {
                sender.sendMessage(ChatColor.DARK_RED + "! You can't teleport to home in another dimension");
                return false;
            }
        }

        player.teleport(location);

        return true;
    }

    private void sendMessage(CommandSender sender, String homename)
    {
        String outhome = " " + homename;
        if (homename == defualt_homename)
        {
            outhome = "";
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "* You have teleported to the home" + outhome + ".");
    }
}
