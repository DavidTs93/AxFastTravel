package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Pair;
import me.Aldreda.AxUtils.Utils.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AxFastTravel extends JavaPlugin implements CommandExecutor {
	static MySQL SQL;
	static AxFastTravel instance;
	static WorldGuardFastTravel WorldGuardFastTravel = null;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) new TravelMenu((Player) sender,null,FastTravelEvent.FastTravelMethod.COMMAND);
		return true;
	}
	
	public void onLoad() {
		if (AxUtils.getWorldGuardManager() != null) WorldGuardFastTravel = new WorldGuardFastTravel();
	}
	
	public void onEnable() {
		instance = this;
		try {
			SQL = new MySQL();
		} catch (SQLException e) {
			Utils.chatColorsLogPlugin("&fAAxFastTravel &bMySQL connection: &cFAILURE!!!");
			this.getLogger().severe("MySQL error: ");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (AxUtils.getCitizensManager() != null) AxUtils.getCitizensManager().registerTrait(TravelAgent.class,"travel_agent");
		PluginCommand command = this.getCommand("AxFastTravel");
		command.setExecutor(this);
		if (getServer().getPluginManager().getPlugin("AxItems") != null) new me.DMan16.AxItems.Items.AxItem(Utils.makeItem(Material.PAPER,null,ItemFlag.values()),"fasttravel_scroll",
				Component.translatable("item.aldreda.item.fasttravel_scroll", TextColor.color(0xB99AFF)).decoration(TextDecoration.ITALIC,false),null,null,
				(info) -> {
					Player player = info.second().getPlayer();
					new TravelMenu(player,null,FastTravelEvent.FastTravelMethod.SCROLL);
				},null, Arrays.asList("FastTravel","Scroll","Teleportation","Travel")).addEnchantments(Pair.of(Enchantment.DURABILITY,0)).register();
		Utils.chatColorsLogPlugin("&fAxFastTravel &aloaded!");
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		Utils.chatColorsLogPlugin("&fAxFastTravel &adisabed");
	}
	
	public static List<NPC> getTravelAgents() {
		List<NPC> npcs = new ArrayList();
		for (NPC npc : CitizensAPI.getNPCRegistry()) if (npc.hasTrait(TravelAgent.class)) npcs.add(npc);
		return npcs;
	}
	
	class MySQL {
		private int defaultPricePerBlock = 1;
		private int defaultPriceBetweenWorlds = 10000;
		
		private MySQL() throws SQLException {
			Statement statement = AxUtils.getMySQL().getConnection().createStatement();
			DatabaseMetaData data = AxUtils.getMySQL().getConnection().getMetaData();
			statement.execute("CREATE TABLE IF NOT EXISTS FastTravel (ID TEXT NOT NULL UNIQUE);");
			if (!data.getColumns(null,null,"FastTravel","ID").next())
				statement.execute("ALTER TABLE FastTravel ADD ID TEXT NOT NULL UNIQUE;");
			if (!data.getColumns(null,null,"FastTravel","Value").next())
				statement.execute("ALTER TABLE FastTravel ADD Value INT NOT NULL;");
			if (!statement.executeQuery("SELECT * FROM FastTravel WHERE ID=\"Price Per Block\";").next())
				statement.execute("INSERT INTO FastTravel (ID,Value) VALUES (\"Price Per Block\"," + defaultPricePerBlock + ");");
			if (!statement.executeQuery("SELECT * FROM FastTravel WHERE ID=\"Price Between Worlds\";").next())
				statement.execute("INSERT INTO FastTravel (ID,Value) VALUES (\"Price Between Worlds\"," + defaultPriceBetweenWorlds + ");");
			statement.close();
		}
		
		public int getPricePerBlock() {
			int val = defaultPricePerBlock;
			try {
				Statement statement = AxUtils.getMySQL().getConnection().createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM FastTravel WHERE ID=\"Price Per Block\";");
				if (result.next()) val = Math.max(0,result.getInt("Value"));
				else statement.execute("INSERT INTO FastTravel (ID,Value) VALUES (\"Price Per Block\"," + defaultPricePerBlock + ");");
				statement.close();
			} catch (Exception e) {}
			return val;
		}
		
		public int getPriceBetweenWorlds() {
			int val = defaultPriceBetweenWorlds;
			try {
				Statement statement = AxUtils.getMySQL().getConnection().createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM FastTravel WHERE ID=\"Price Per Block\";");
				if (result.next()) val = Math.max(0,result.getInt("Value"));
				else statement.execute("INSERT INTO FastTravel (ID,Value) VALUES (\"Price Between Worlds\"," + defaultPriceBetweenWorlds + ");");
				statement.close();
			} catch (Exception e) {}
			return val;
		}
	}
}