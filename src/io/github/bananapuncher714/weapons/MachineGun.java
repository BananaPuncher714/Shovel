package io.github.bananapuncher714.weapons;

import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.bullets.Bullet;
import io.github.bananapuncher714.bullets.BulletBase;
import io.github.bananapuncher714.events.EventCaller;
import io.github.bananapuncher714.events.PlayerHoldRightClickEvent;
import io.github.bananapuncher714.util.ProjectileUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MachineGun implements Listener {
	HashSet< Projectile > projs = new HashSet< Projectile >();
	HashMap< UUID, Long > rightClick = new HashMap< UUID, Long >();
	BulletBase type = ShovelMain.getMain().getBulletBase( "rocket" );
	
	public MachineGun() {
		
	}
	
	@EventHandler
	public void onPlayerHoldRightClickEvent( PlayerHoldRightClickEvent e ) {
		ItemStack item = e.getPlayer().getItemInHand();
		if ( item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase( "machine gun" ) ) {
			Player p = e.getPlayer();
			Bullet bullet = type.shootBullet( p );
			
			float raise = ( float ) Math.min( EventCaller.lastRightClicked( p ) / 100.0, 12 );
			double deg = 18;
			if ( p.isSneaking() ) deg = 11;
			else if ( p.isSprinting() ) deg += 10;
			if ( !p.isOnGround() ) deg += 9;
			ProjectileUtil.randomizeSpread( bullet.getProjectile(), ( float ) ( deg * Math.min( EventCaller.lastRightClicked( p ) / 550.0, 1 ) ), 0 );
			projs.add( bullet.getProjectile() );
		}
	}
}
