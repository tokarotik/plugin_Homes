package tokarotik.homes.storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class HomePosition
{
    private String path;
    private String defualt_home;
    private String property_list;
    private String property_files;
    private String folder_data;

    public HomePosition(String path, String defualt_home, String property_list, String property_files, String folder_data)
    {
        this.path = path;
        this.property_list = property_list;
        this.property_files = property_files;
        this.folder_data = folder_data;
        this.defualt_home = defualt_home;

        createFolder(folder_data);
    }

    public HomePosition(String path, String defualt_home) {
        this(path, defualt_home, "homes", "yml", "data");
    }

    public void writeHome(String player, String name, String dimension, Location location)
    {
        String filename = folder_data + "/" + player + "." + property_files;

        File file = createFile(filename);
        if (file == null) {return;}

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Map<?, ?>> homesList = read(player);

        if (homesList != null) {
            if (existsHome(homesList, name)) {
                removeHome(homesList, name);
            }
        }
        else
        {
            homesList = new ArrayList<>();
        }

        Map<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("dimension", dimension);

        data.put("x", location.getBlockX());
        data.put("y", location.getBlockY());
        data.put("z", location.getBlockZ());

        homesList.add(data);
        config.set(property_list, homesList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location readHome(String player, String name)
    {
        List<Map<?, ?>> homesList = read(player);
        if (homesList == null) {return null;}

        Map<?, ?> home = getHome(homesList, name);
        if (home == null) {return null;}

        String worldName = safeCast(home.get("dimension"), String.class);
        if (worldName == null) {return null;}

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            System.out.println("World not was found: " + worldName);
            return null;
        }

        Object x;
        Object y;
        Object z;

        try {
            x = home.get("x");
            y = home.get("y");
            z = home.get("z");
        }

        catch(Exception e){
            return null;
        }

        Location location = new Location(world, (int) x, (int) y, (int) z);

        return location;
    }

    public boolean removeHome(String player, String name)
    {
        String filename = folder_data + "/" + player + "." + property_files;

        File file = createFile(filename);
        if (file == null) {return false;}

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Map<?, ?>> homesList = read(player);
        if (homesList == null) {return false;}

        removeHome(homesList, name);

        config.set(property_list, homesList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean removeHome(List<Map<?, ?>> homesList, String name) {
        Iterator<Map<?, ?>> iterator = homesList.iterator();

        while (iterator.hasNext()) {
            Map<?, ?> home = iterator.next();
            Object nameObj = home.get("name");

            if (nameObj instanceof String && ((String) nameObj).equalsIgnoreCase(name)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }



    private Map<?, ?> getHome(List<Map<?, ?>> homesList, String name)
    {
        for (Map<?, ?> home : homesList) { // cycle in list homesList
            Object nameObj = home.get("name"); // get 'name' every home
            if (nameObj != null && nameObj instanceof String) { // check if is it type String
                String namehome = (String) nameObj; // set to Java type String
                if (namehome.equalsIgnoreCase(name)) { // namehome == namehome
                    return home;
                }
            }
        }
        return null;
    }

    public int getLengthHomes(String player)
    {
        List<Map<?, ?>> homes = read(player);
        if (homes == null) {return 0;}

        return homes.size();
    }

    public boolean hasHome(String player, String name)
    {
        List<Map<?, ?>> homes = read(player);
        if (homes == null) {return false;}

        return existsHome(homes, name);
    }


    private boolean existsHome(List<Map<?, ?>> homesList, String name)
    {
        Map<?, ?> home = getHome(homesList, name);
        if (home == null)
        {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object value, Class<T> targetType) {
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    public String getNamesAndDimensionsInString(String player)
    {
        List<Map<?, ?>> homes = read(player);
        if (homes == null){return ChatColor.GRAY +  "* You don't have any home";}

        String output = ChatColor.GRAY + "* Your homes:";

        for (int i = 0; i < homes.size(); i++)
        {
            Map<?, ?> home = homes.get(i);

            String name;
            name = (String) home.get("name");

            if (name == defualt_home)
            {
                name = "HOME (/home)";
            }

            output += "\n    " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + name + ChatColor.DARK_GRAY + " : " + ChatColor.DARK_AQUA + home.get("dimension");
        }

        return output;
    }

    private List<Map<?, ?>> read(String player)
    {
        String filename = folder_data + "/" + player + "." + property_files;

        File file = getFile(filename);
        if (file == null) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getMapList(property_list);
    }

    private File createFile(String filename)
    {
        File file = new File(path, filename);
        if (file == null)
        {
            try
            {
                boolean result = file.createNewFile();
                if (result == false) {
                    System.out.println("Can't to create file " + filename + " .");
                    return null;
                }
            }
            catch (IOException e)
            {
                System.out.println("Failed to create file '" + filename + "' with error " + e.getMessage());
                return null;
            }
        }

        return file;
    }

    private File getFile(String filename)
    {
        File file = new File(path, filename);
        if (!file.exists())
        {
            return null;
        }

        return file;
    }

    private void createFolder(String path_folder)
    {
        File folder = new File(path, path_folder);
        if (!folder.exists())
        {
            folder.mkdirs();
        }
    }
}
