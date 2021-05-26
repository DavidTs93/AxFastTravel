package me.DMan16.AxFastTravel;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Aldreda.AxUtils.AxUtils;
import org.bukkit.Location;

public class WorldGuardFastTravel {
	StateFlag FastTravelFlag;
	
	WorldGuardFastTravel() {
		this.FastTravelFlag = AxUtils.getWorldGuardManager().newStateFlag("FastTravelFlag",true);
	}
	
	boolean isAllowedFastTravel(Location loc) {
		if (this.FastTravelFlag == null) return true;
		try {
			ProtectedRegion[] regions = AxUtils.getWorldGuardManager().sortRegionsByPriority(AxUtils.getWorldGuardManager().getRegionSet(loc));
			for (ProtectedRegion region : regions) if (region.getFlag(FastTravelFlag) == State.DENY) return false;
		} catch (Exception e) {}
		return true;
	}
}
