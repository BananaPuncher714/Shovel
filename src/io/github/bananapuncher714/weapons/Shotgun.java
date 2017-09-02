package io.github.bananapuncher714.weapons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.bullets.Bullet;
import io.github.bananapuncher714.bullets.BulletBase;
import io.github.bananapuncher714.events.EventCaller;
import io.github.bananapuncher714.events.PlayerPressRightClickEvent;
import io.github.bananapuncher714.util.ProjectileUtil;

public class Shotgun implements Listener {
	HashSet< Projectile > projs = new HashSet< Projectile >();
	HashMap< UUID, Long > rightClick = new HashMap< UUID, Long >();
	BulletBase type = ShovelMain.getMain().getBulletBase( "rocket" );
	
	public Shotgun() {

	}
	
	@EventHandler
	public void onPlayerPressRightClickEvent( PlayerPressRightClickEvent e ) {
		Player p = e.getPlayer();
		if ( EventCaller.lastRightClicked( p ) < 500 ) return;
		ItemStack item = p.getItemInHand();
		if ( item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase( "shotgun" ) ) {
			for ( int i = 0; i < 2; i++ ) {
				Bullet bullet = type.shootBullet( p );
				
				double deg = 15;
				if ( p.isSneaking() ) deg = 7;
				else if ( p.isSprinting() ) deg += 8;
				if ( !p.isOnGround() ) deg += 9;
				ProjectileUtil.randomizeSpread( bullet.getProjectile(), ( float ) deg, 0 );
				
				projs.add( bullet.getProjectile() );
			}
		}
	}
}
