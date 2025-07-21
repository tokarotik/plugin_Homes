package tokarotik.homes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tokarotik.homes.storage.HomePosition;

public class ListHomes implements CommandExecutor
{

    private HomePosition home;
    private FileConfiguration config;
    private String defualt_homename;

    public ListHomes(HomePosition home, FileConfiguration config, String defualt_homename)
    {
        this.home = home;
        this.config = config;
        this.defualt_homename = defualt_homename;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("This command can use only player!");
            return true;
        }

        sender.sendMessage(home.getNamesAndDimensionsInString(sender.getName()));

        return true;
    }
}
