package me.DMan16.AxFastTravel;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FastTravelEvent extends PlayerTeleportEvent {
	public final FastTravelEvent.FastTravelMethod method;
	
	public FastTravelEvent(@NotNull Player player, @NotNull Location from, @Nullable Location to, @NotNull FastTravelMethod method) {
		super(Objects.requireNonNull(player),Objects.requireNonNull(from),Objects.requireNonNull(to),TeleportCause.PLUGIN);
		this.method = Objects.requireNonNull(method);
	}
	
	public static enum FastTravelMethod {
		AGENT,
		COMMAND(true),
		SCROLL;
		
		public final boolean isFree;
		
		private FastTravelMethod() {
			this(false);
		}
		
		private FastTravelMethod(boolean isFree) {
			this.isFree = isFree;
		}
	}
}