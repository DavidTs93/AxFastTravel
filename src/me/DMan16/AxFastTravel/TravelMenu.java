package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Utils.ListenerInventoryPages;
import me.Aldreda.AxUtils.Utils.Utils;
import me.DMan16.AxFastTravel.FastTravelEvent.FastTravelMethod;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

class TravelMenu extends ListenerInventoryPages {
	private static ItemStack border = Utils.makeItem(Material.BLACK_STAINED_GLASS_PANE,Component.empty(),ItemFlag.values());
	
	private Location start;
	private FastTravelMethod method;
	private List<Travel> travels;
	
	TravelMenu(Player player, NPC npc, FastTravelMethod method) {
		super(null,player,5,Component.translatable("menu.aldreda.fasttravel",NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC,
				false).decoration(TextDecoration.BOLD,true),AxFastTravel.instance,npc,method);
	}
	
	protected void first(Object... objs) {
		NPC start = (NPC) objs[0];
		if (start != null && !start.hasTrait(TravelAgent.class)) start = null;
		this.start = start == null ? this.player.getLocation().toCenterLocation() : getLanding(start);
		this.method = (FastTravelMethod) objs[1];
		this.travels = new ArrayList<Travel>();
		for (NPC npc : AxFastTravel.getTravelAgents()) if (npc.isSpawned() && npc != start) this.travels.add(new Travel(getLanding(npc)));
	}
	
	private Location getLanding(NPC npc) {
		if (npc == null) return null;
		Location loc = npc.getEntity().getLocation().toCenterLocation();
		float yaw = loc.getYaw() % 360;
		if (yaw > 180) yaw -= 360;
		else if (yaw < -180) yaw += 360;
		int x = 0;
		int z = 0;
		if (yaw >= -67.5 && yaw < 67.5) z = 1;
		else if (yaw >= 112.5 || yaw < -112.5) z = -1;
		if (yaw >= 22.5 && yaw < 157.5) x = -1;
		else if (yaw >= -157.5 && yaw < -22.5) x = 1;
		loc = loc.add(x,0.0,z);
		yaw = (yaw + 180) % 360;
		if (yaw > 180) yaw -= 360;
		else if (yaw < -180) yaw += 360;
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
		return Math.min(1,(int) Math.ceil(travels.size() / 28.0));
	}
	
	protected void otherSlot(InventoryClickEvent event, int slot, ItemStack slotItem) {
		if (slot >= inventory.getSize() || isBorder(slot)) return;
		Player player = (Player) event.getWhoClicked();
		int idx = slot + 28 * (currentPage - 1) - 8 - 2 * (slot / 9);
		Travel travel = travels.get(idx);
		FastTravelEvent fastTravelEvent = new FastTravelEvent(player,start,travel.location,method);
		if (AxFastTravel.WorldGuardFastTravel != null && !AxFastTravel.WorldGuardFastTravel.isAllowedFastTravel(start)) fastTravelEvent.setCancelled(true);
		Bukkit.getServer().getPluginManager().callEvent(fastTravelEvent);
		if (fastTravelEvent.isCancelled()) return;
		if (method.isFree || AxUtils.getEconomy().withdrawPlayer(player,travel.cost(start)).transactionSuccess()) {
			if (method == FastTravelMethod.SCROLL) player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand().subtract());
			player.teleport(travel.location);
		} else player.sendMessage(Component.translatable("bank.aldreda.no_funds",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
	}
	
	private boolean isBorder(int slot) {
		return slot >= 0 && slot < 9 || slot >= inventory.getSize() - 9 && slot < inventory.getSize() || slot % 9 == 0 || (slot + 1) % 9 == 0;
	}
}