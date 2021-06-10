package me.DMan16.AxFastTravel;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Aldreda.AxUtils.AxUtils;
import org.bukkit.Location;

public class WorldGuardFastTravel {
	StateFlag FastTravelFlag;
	
	WorldGuardFastTravel() {
		this.FastTravelFlag = AxUtils.getWorldGuardManager().newStateFlag("allow-fast-travel",true);
	}
	
	boolean isAllowedFastTravel(Location loc) {
		if (this.FastTravelFlag == null) return true;
		try {
			for (ProtectedRegion region : AxUtils.getWorldGuardManager().getRegions(loc)) if (region.getFlag(FastTravelFlag) == State.DENY) return false;
		} catch (Exception e) {}
		return true;
	}
}