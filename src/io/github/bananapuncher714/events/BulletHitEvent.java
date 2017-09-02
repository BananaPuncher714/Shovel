package io.github.bananapuncher714.events;

import io.github.bananapuncher714.bullets.Bullet;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;

public class BulletHitEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	ProjectileHitEvent event;
	Bullet bullet;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public BulletHitEvent( ProjectileHitEvent event, Bullet bullet ) {
		this.event = event;
		this.bullet = bullet;
	}
	
	public ProjectileHitEvent getEvent() {
		return event;
	}
	
	public Bullet getBullet() {
		return bullet;
	}
}
