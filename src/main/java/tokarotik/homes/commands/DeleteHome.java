package tokarotik.homes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tokarotik.homes.storage.HomePosition;

public class DeleteHome implements CommandExecutor {

    private HomePosition home;
    private FileConfiguration config;
    private String defualt_homename;

    public DeleteHome(HomePosition home, FileConfiguration config, String defualt_homename)
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

        boolean sended = sendPlayerHas(sender, homename);
        if (!sended)
        {
            return true;
        }

        boolean isRemoved = deleteHome(sender.getName(), homename);
        if (!isRemoved)
        {
            sender.sendMessage(ChatColor.DARK_RED + "! By some reason can't to delete your home");
            return true;
        }

        sendMessage(sender, homename);

        return true;
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

    private boolean deleteHome(String player, String name)
    {
        return home.removeHome(player, name);
    }

    private void sendMessage(CommandSender sender, String homename)
    {
        String outhome = " " + homename;
        if (homename == defualt_homename)
        {
            outhome = "";
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "* Home" + outhome + " had deleted");
    }
}