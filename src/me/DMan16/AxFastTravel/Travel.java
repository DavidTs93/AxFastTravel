package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxFastTravel.FastTravelEvent.FastTravelMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

class Travel {
	final Location location;
	private Material material = Material.PAPER;
	private boolean glow;
	private Component name;
	
	public Travel(Location location, Material material, Component name, TextColor color, List<TextDecoration> styles, boolean glow, TextColor locationColorCoords, TextColor locationColorOther) {
		this.location = location;
		if (material != null) this.material = material;
		if (name == null) {
			String textColor = "&" + (this.location.getWorld().getEnvironment() == Environment.NETHER ? "&c" : (this.location.getWorld().getEnvironment() == Environment.THE_END ? "8" : "f"));
			name = Component.text(Utils.chatColors(textColor + this.location.getWorld().getName() + " ")).append(locationToString(this.location,locationColorCoords,locationColorOther));
		}
		if (color != null) name = name.color(color);
		if (styles != null) for (TextDecoration style : styles) name = name.decoration(style,true);
		if (styles == null || !styles.contains(TextDecoration.ITALIC)) name = name.decoration(TextDecoration.ITALIC,false);
		this.name = name;
		this.glow = glow;
	}
	
	static Component locationToString(Location loc, TextColor locationColorCoords, TextColor locationColorOther) {
		if (locationColorCoords == null) locationColorCoords = NamedTextColor.GREEN;
		if (locationColorOther == null) locationColorOther = NamedTextColor.GOLD;
		return Component.text("(",locationColorOther).append(Component.text(loc.getBlockX(),locationColorCoords)).append(Component.text(",",
				locationColorOther)).append(Component.text(loc.getBlockY(),locationColorCoords)).append(Component.text(",",locationColorOther)).append(Component.text(loc.getBlockZ(),
				locationColorCoords)).append(Component.text(")",locationColorOther));
	}
	
	ItemStack item(Location loc, FastTravelMethod method) {
		ItemStack item = Utils.makeItem(material,name,Arrays.asList((Component.translatable("bank.aldreda.cost",NamedTextColor.GOLD).append(Component.text(": ",
				NamedTextColor.GOLD).append(Component.text(AxUtils.getEconomy().format(method.isFree ? 0.0D : cost(loc)))))).decoration(TextDecoration.ITALIC,false)),ItemFlag.values());
		if (glow) item.addUnsafeEnchantment(Enchantment.DURABILITY,0);
		return item;
	}
	
	double cost(Location loc) {
		return loc.getWorld().equals(this.location.getWorld()) ? Math.ceil(loc.distance(this.location)) * AxFastTravel.instance.getConfig().getDouble("price-per-block") :
				AxFastTravel.instance.getConfig().getDouble("price-between-worlds");
	}
}