package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxFastTravel.FastTravelEvent.FastTravelMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

class Travel {
	private static Material material = Material.PAPER;
	final Location location;
	
	public Travel(Location location) {
		this.location = location;
	}
	
	ItemStack item(Location loc, FastTravelMethod method) {
		String color = "&" + (this.location.getWorld().getEnvironment() == Environment.NETHER ? "&c" : (this.location.getWorld().getEnvironment() == Environment.THE_END ? "8" : "f"));
		Component name = Component.text(Utils.chatColors(color + this.location.getWorld().getName() + " &6(&a" + this.location.getBlockX() + "&6,&a" +
				this.location.getBlockY() + "&6,&a" + this.location.getBlockZ() + "&6)"));
		return Utils.makeItem(material,name,Arrays.asList((Component.translatable("bank.aldreda.cost",NamedTextColor.GOLD).append(Component.text(": ",NamedTextColor.GOLD).append(
				Component.text(AxUtils.getEconomy().format(method.isFree ? 0.0D : this.cost(loc)))))).decoration(TextDecoration.ITALIC,false)),ItemFlag.values());
	}
	
	double cost(Location loc) {
		return loc.getWorld().equals(this.location.getWorld()) ? Math.ceil(loc.distance(this.location)) * AxFastTravel.instance.getConfig().getDouble("price-per-block") :
				AxFastTravel.instance.getConfig().getDouble("price-between-worlds");
	}
}