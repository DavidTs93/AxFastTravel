package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Utils.ListenerInventoryPages;
import me.Aldreda.AxUtils.Utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TravelMenu extends ListenerInventoryPages {
	private static ItemStack border = Utils.makeItem(Material.BLACK_STAINED_GLASS_PANE,Component.empty(),ItemFlag.values());
	
	private Location start;
	private FastTravelEvent.FastTravelMethod method;
	private List<Travel> travels;
	
	TravelMenu(Player player, NPC npc, FastTravelEvent.FastTravelMethod method) {
		super(null,player,5,Component.translatable("menu.aldreda.fasttravel",NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC,
				false).decoration(TextDecoration.BOLD,true),AxFastTravel.instance,npc,method);
	}
	
	protected void first(Object... objs) {
		NPC start = (NPC) objs[0];
		if (start != null && !start.hasTrait(TravelAgent.class)) start = null;
		this.start = start == null ? this.player.getLocation().toCenterLocation() : getLanding(start);
		this.method = (FastTravelEvent.FastTravelMethod) objs[1];
		this.travels = new ArrayList<Travel>();
		for (NPC npc : AxFastTravel.getTravelAgents()) if (npc.isSpawned() && npc != start) {
			Material material = null;
			Location loc = getLanding(npc);
			if (npc.data().has("FastTravel Agent Material")) try {
				material = Material.getMaterial(npc.data().get("FastTravel Agent Material").toString().toUpperCase());
			} catch (Exception e) {}
			List<TextDecoration> styles = new ArrayList<TextDecoration>();
			boolean glow = false;
			if (npc.data().has("FastTravel Agent Style")) try {
				String str = npc.data().get("FastTravel Agent Style");
				List<String> style = Arrays.asList(str.toLowerCase().replace(" ", "").split(","));
				if (style.contains("italic")) styles.add(TextDecoration.ITALIC);
				if (style.contains("bold")) styles.add(TextDecoration.BOLD);
				if (style.contains("underlined")) styles.add(TextDecoration.UNDERLINED);
				if (style.contains("obfuscated")) styles.add(TextDecoration.OBFUSCATED);
				if (style.contains("strikethrough")) styles.add(TextDecoration.STRIKETHROUGH);
				if (style.contains("glow")) glow = true;
			} catch (Exception e) {}
			TextColor locationColorCoords = null;
			TextColor locationColorOther = null;
			if (npc.data().has("FastTravel Agent Location")) try {
				String str = npc.data().get("FastTravel Agent Location");
				String[] colors = str.toLowerCase().replace(" ","").split(",");
				locationColorCoords = colorFromString(colors[0]);
				if (colors.length > 1) locationColorOther = colorFromString(colors[1]);
			} catch (Exception e) {}
			Component name = null;
			if (npc.data().has("FastTravel Agent Name")) try {
				String str = npc.data().get("FastTravel Agent Name");
				name = Component.empty();
				int idx;
				while ((idx = str.indexOf("%%")) != -1) {
					if (idx > 0) name = name.append(replaceLocation(str.substring(0,idx),loc,locationColorCoords,locationColorOther));
					str = str.substring(idx + 2);
					idx = str.indexOf("%%");
					if (idx == -1) break;
					name = name.append(Component.translatable(str.substring(0,idx).replace("\\%","%")));
					str = str.substring(idx + 2);
				}
				if (!str.isEmpty()) name = name.append(replaceLocation(str,loc,locationColorCoords,locationColorOther));
			} catch (Exception e) {}
			TextColor color = null;
			if (npc.data().has("FastTravel Agent Color")) try {
				color = colorFromString(npc.data().get("FastTravel Agent Color"));
			} catch (Exception e) {}
			this.travels.add(new Travel(loc,material,name,color,styles,glow,locationColorCoords,locationColorOther));
		}
	}
	
	private Component replaceLocation(String str, Location loc, TextColor locationColorCoords, TextColor locationColorOther) {
		Component comp = Component.empty();
		int idx;
		while ((idx = str.toLowerCase().indexOf("%location%")) != -1) {
			if (idx > 0) comp = comp.append(Component.text(str.substring(0,idx).replace("\\%","%")));
			str = str.substring(idx + "%location%".length());
			comp = comp.append(Travel.locationToString(loc,locationColorCoords,locationColorOther));
		}
		if (!str.isEmpty()) comp = comp.append(Component.text(str.replace("\\%","%")));
		return comp;
	}
	
	private TextColor colorFromString(String str) {
		String textColor = str.toLowerCase();
		return textColor.startsWith("0x") ? TextColor.color(Integer.parseInt(textColor.replace("0x",""),16)) :
				TextColor.color(ChatColor.valueOf(textColor.toUpperCase()).asBungee().getColor().getRGB());
	}
	
	private Location getLanding(NPC npc) {
		if (npc == null) return null;
		Location loc = npc.getEntity().getLocation().toCenterLocation();
		float yaw = loc.getYaw() % 360;
		if (yaw > 180) yaw -= 360;
		else if (yaw < -180) yaw += 360;
		int x = 0;
		int z = 0;
		yaw = (yaw + 180) % 360;
		if (yaw > 180) yaw -= 360;
		else if (yaw < -180) yaw += 360;
		if (yaw >= -67.5 && yaw < 67.5) z = 1;
		else if (yaw >= 112.5 || yaw < -112.5) z = -1;
		if (yaw >= 22.5 && yaw < 157.5) x = -1;
		else if (yaw >= -157.5 && yaw < -22.5) x = 1;
		loc = loc.add(x,0.0,z);
		loc.setYaw(yaw);
		return loc;
	}
	
	protected void reset() {
		for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i,isBorder(i) ? border.clone() : null);
	}
	
	protected void setPageContents(int page) {
		for (int i = 0; i < 28; i++) {
			int idx = i + 28 * (page - 1);
			if (idx < travels.size()) inventory.addItem(travels.get(idx).item(start,method));
		}
	}
	
	public int maxPage() {
		return Math.max(1,(int) Math.ceil(travels.size() / 28.0));
	}
	
	protected void otherSlot(InventoryClickEvent event, int slot, ItemStack slotItem) {
		if (slot >= inventory.getSize() || isBorder(slot)) return;
		Player player = (Player) event.getWhoClicked();
		int idx = slot + 28 * (currentPage - 1) - 8 - 2 * (slot / 9);
		Travel travel = travels.get(idx);
		FastTravelEvent fastTravelEvent = new FastTravelEvent(player,start,travel.location,method);
		if (method != FastTravelEvent.FastTravelMethod.COMMAND && AxFastTravel.WorldGuardFastTravel != null && !AxFastTravel.WorldGuardFastTravel.isAllowedFastTravel(start))
			fastTravelEvent.setCancelled(true);
		Bukkit.getServer().getPluginManager().callEvent(fastTravelEvent);
		if (fastTravelEvent.isCancelled()) return;
		if (method.isFree || AxUtils.getEconomy().withdrawPlayer(player,travel.cost(start)).transactionSuccess()) {
			if (method == FastTravelEvent.FastTravelMethod.SCROLL) player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand().subtract());
			player.teleport(travel.location);
		} else player.sendMessage(Component.translatable("bank.aldreda.no_funds",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
	}
}