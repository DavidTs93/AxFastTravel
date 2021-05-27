package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Pair;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxFastTravel.FastTravelEvent.FastTravelMethod;
import me.DMan16.AxItems.Items.AxItem;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AxFastTravel extends JavaPlugin implements CommandExecutor {
	static AxFastTravel instance;
	static AxItem FastTravelScroll = null;
	static WorldGuardFastTravel WorldGuardFastTravel = null;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) this.reloadConfig();
		else if (sender instanceof Player) new TravelMenu((Player) sender,null,FastTravelMethod.COMMAND);
		return true;
	}
	
	public void onLoad() {
		if (AxUtils.getWorldGuardManager() != null) WorldGuardFastTravel = new WorldGuardFastTravel();
	}
	
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		if (AxUtils.getCitizensManager() != null) AxUtils.getCitizensManager().registerTrait(TravelAgent.class,"travel_agent");
		PluginCommand command = this.getCommand("AxFastTravel");
		command.setExecutor(this);
		if (FastTravelScroll == null) FastTravelScroll = new AxItem(Utils.makeItem(Material.PAPER,null,ItemFlag.values()),"fasttravel_scroll",
				Component.translatable("item.aldreda.item.fasttravel_scroll",TextColor.color(0xB99AFF)).decoration(TextDecoration.ITALIC,false),null,null,
				(info) -> {
					Player player = ((PlayerInteractEvent)info.second()).getPlayer();
					new TravelMenu(player,null, FastTravelMethod.SCROLL);
					},
				null,Arrays.asList("FastTravel","Scroll","Teleportation","Travel")).addEnchantments(Pair.of(Enchantment.DURABILITY,0)).register();
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
}