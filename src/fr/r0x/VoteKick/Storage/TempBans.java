package fr.r0x.VoteKick.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.r0x.VoteKick.Main.Main;
import fr.r0x.VoteKick.Vote.TempBan;

public class TempBans {

	protected Main plugin;
	private FileConfiguration tempbans = null;
	private File tempbansFile = null;
	
	
	public TempBans(Main plugin)
	{
		this.plugin = plugin;
	}
	
	
	public void reloadtempbansFile()
	{
	    if (tempbansFile == null)
	    {
	    	tempbansFile = new File(plugin.getDataFolder(), "tempbans.yml");
	    }
	    tempbans = YamlConfiguration.loadConfiguration(tempbansFile);
	    
	    if (!tempbansFile.exists())
	    {
	    	InputStream deftempbansFile = plugin.getResource("tempbans.yml");
	    	if (deftempbansFile != null)
	    	{
	    		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(deftempbansFile);
	    		tempbans.setDefaults(defConfig);
	    	}
	    }
	    try {
			tempbans.save(tempbansFile);
		} catch (IOException e) {
			System.out.println("Error during TempBans saving, disabling VoteKick");
			e.printStackTrace();
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	   
	}
	
	
	public FileConfiguration gettempbans()
	{
		if(tempbans == null)
		{
			reloadtempbansFile();
		}
		return tempbans;
	}
	
	public long CurrentTime()
	{
		return Calendar.getInstance().getTimeInMillis();
	}
	
	
	public void Tempban(TempBan vote)
	{
		
		String path = vote.getVoted().getName();
		long timer = (CurrentTime() + (vote.getTime() * 60000));
		gettempbans().createSection(path);
		gettempbans().set(path + ".Date", plugin.register.date());
		gettempbans().set(path + ".Until", timer);
		gettempbans().set(path + ".Reason", vote.getReason());
		gettempbans().set(path + ".Voters", vote.getList());
		try {
			tempbans.save(tempbansFile);
		} catch (IOException e) {
			System.out.println("Error during TempBans saving, disabling VoteKick");
			e.printStackTrace();
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
	
	
	public boolean isBanned(Player p)
	{
		String path = p.getName();
		if (gettempbans().contains(p.getName()))
		{
			if (CurrentTime() >= gettempbans().getLong(path + ".Until"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	
	public String getReason(Player p)
	{
		String path = p.getName();
		return gettempbans().getString(path+".Reason");
	}
	
	
}
