package io.github.bananapuncher714.weapons;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bananapuncher714.bullets.Bullet;
import io.github.bananapuncher714.bullets.BulletBase;
import io.github.bananapuncher714.events.BulletHitEvent;
import io.github.bananapuncher714.events.EventCaller;
import io.github.bananapuncher714.events.PlayerHoldRightClickEvent;
import io.github.bananapuncher714.events.PlayerPressRightClickEvent;
import io.github.bananapuncher714.events.PlayerReleaseRightClickEvent;
import io.github.bananapuncher714.modules.ModuleManager;
import io.github.bananapuncher714.objects.Dependency;
import io.github.bananapuncher714.util.NBTEditor;
import io.github.bananapuncher714.util.ProjectileUtil;
import io.github.bananapuncher714.weapons.guns.GunBase;

public class AutomaticWeapon extends GunBase implements Listener {
	HashMap< UUID, Long > lastFired = new HashMap< UUID, Long >();
	String name;
	int spray, delay;
	int bullets;
	boolean timeSpray;
	double sprayDelay, raiseTime;
	double recoil;
	float raise;
	
	public AutomaticWeapon( String base, String name, int spray, int bullets, int delay ) {
		this.base = ( BulletBase ) ModuleManager.getBaseFromMod( "BulletBase", base );
		this.name = name;
		this.spray = spray;
		this.delay = delay;
		this.bullets = bullets;
	}
	
	public AutomaticWeapon( FileConfiguration yamlFile ) {
		load( yamlFile );
		name = yamlFile.getString( "display-name" );
		bullets = yamlFile.getInt( "bullet.amount" );
		recoil = yamlFile.getDouble( "recoil" );
		spray = yamlFile.getInt( "spray.degree" );
		sprayDelay = yamlFile.getInt( "spray.delay" );
		raise = ( float ) yamlFile.getDouble( "spray.bullet-recoil-degree" );
		raiseTime = yamlFile.getDouble( "spray.bullet-recoil-delay" );
		delay = yamlFile.getInt( "shoot-delay" );
		
	}
	
	@Override
	public void onHoldEvent( PlayerHoldRightClickEvent e ) {
		ItemStack item = e.getPlayer().getItemInHand();
		if ( !checkItem( item ) ) return;
		Player p = e.getPlayer();
		if ( lastFired.containsKey( p.getUniqueId() ) && System.currentTimeMillis() - lastFired.get( p.getUniqueId() ) < delay ) return;
		lastFired.put( p.getUniqueId(), System.currentTimeMillis() );
		for ( int count = 0; count < bullets; count++ ) {
			Bullet bullet = base.shootBullet( p );
			
			p.setVelocity( p.getVelocity().add( p.getLocation().getDirection().multiply( - recoil ) ) );
			
			float bulletRecoil = ( float ) Math.min( EventCaller.lastRightClicked( p ) / raiseTime, 1 ) * raise;
			double deg = getSpray( p, spray ) * Math.min( EventCaller.lastRightClicked( p ) / sprayDelay, 1 );
			
			ProjectileUtil.randomizeSpread( bullet.getProjectile(), ( float ) deg, bulletRecoil );
		}
	}
	
	@Override
	public void run() {
	}
	
	@Override
	public void onPressEvent(PlayerPressRightClickEvent event) {
		if ( checkItem( event.getPlayer().getItemInHand() ) ) event.cancelInteractEvent( true );
	}

	@Override
	public void onReleaseEvent(PlayerReleaseRightClickEvent event) {

	}

	@Override
	public void onBulletHitEvent(BulletHitEvent event) {

	}
	
	@Override
	public boolean checkItem( ItemStack item ) {
		String mod = ( String ) NBTEditor.getItemTag( item, "Shovel", "ModuleName" );
		String id = ( String ) NBTEditor.getItemTag( item, "Shovel", "Identifier" );
		return "GunBase".equalsIgnoreCase( mod ) && identifier.equalsIgnoreCase( id );
	}
	
	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack( material, 1, data );
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( name );
		item.setItemMeta( meta );
		
		item = NBTEditor.setItemTag( item, "GunBase", "Shovel", "ModuleName" );
		item = NBTEditor.setItemTag( item, identifier, "Shovel", "Identifier" );
		return item;
	}

}
