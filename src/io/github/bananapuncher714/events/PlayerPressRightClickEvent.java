package io.github.bananapuncher714.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

public class PlayerPressRightClickEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private boolean cancelInteractive;
	private boolean ahd;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public PlayerPressRightClickEvent( Player player, boolean alreadyHoldingDown ) {
		super( player );
		ahd = alreadyHoldingDown;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean arg0 ) {
		cancelled = arg0;
	}
	
	public void cancelInteractEvent( boolean cancel ) {
		cancelInteractive = cancel;
	}
	
	public boolean cancelInteractEvent() {
		return cancelInteractive;
	}
	
	public boolean alreadyHoldingDown() {
		return ahd;
	}
}
