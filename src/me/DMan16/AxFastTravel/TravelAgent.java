package me.DMan16.AxFastTravel;

import me.Aldreda.AxUtils.Utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class TravelAgent extends Trait {
	
	public TravelAgent() {
		super("travel_agent");
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void click(NPCRightClickEvent event) {
		if (event.isCancelled() || event.getNPC() != this.getNPC() || Utils.isPlayerNPC(event.getClicker())) return;
		event.setCancelled(true);
		new TravelMenu(event.getClicker(),getNPC(),FastTravelEvent.FastTravelMethod.AGENT);
	}
}