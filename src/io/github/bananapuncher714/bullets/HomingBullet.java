package io.github.bananapuncher714.bullets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class HomingBullet extends BulletBase {
	private HashMap< Projectile, LivingEntity > fireballs = new HashMap< Projectile, LivingEntity >();
	double LS = Math.cos( Math.toRadians( 45 ) );
	double curve = .7;
	double speed = 1;
	int maxRange = 100;
	
	public HomingBullet( FileConfiguration config ) {
		load( config );
		speed = config.getDouble( "speed" );
		curve = config.getDouble( "homing.accuracy" );
		LS = Math.cos( Math.toRadians( config.getDouble( "homing.degree" ) ) );
		maxRange = config.getInt( "homing.max-range" ); 
	}
	
	@Override
	public Bullet shootBullet(LivingEntity shooter) {
		Projectile bullet = shooter.launchProjectile( projectileClazz );
		
		findTarget( shooter, bullet );
		
		if ( !( bullet instanceof Fireball ) ) {
			bullet.setGravity( gravity );
			bullet.setVelocity( bullet.getVelocity().normalize().multiply( speed ) );
		}
		Bullet theBullet = new Bullet( bullet, this );
		bullets.add( theBullet );
		return theBullet;
	}

	@Override
	public void loop() {
		for ( Iterator< Bullet > it = bullets.iterator(); it.hasNext(); ) {
			Bullet bullet = it.next();
			Projectile proj = bullet.getProjectile();
			if ( !( proj instanceof Fireball ) ) {
				proj.setVelocity( proj.getVelocity().normalize().multiply( speed ) );
			}
			if ( fireballs.containsKey( proj ) ) home( proj );
		}
	}

	@Override
	public void onBulletHitEvent( ProjectileHitEvent event ) {
	}
	
	public void home( Projectile f ) {
		LivingEntity p = fireballs.get( f );
		if ( p instanceof Player && !( ( Player ) p ).isOnline() ) {
			fireballs.remove( f );
			return;
		}
		if ( p == null || p.isDead() ) {
			fireballs.remove( f );
			return;
		}
		if ( p.getWorld() == f.getWorld() ) {
			Location floc = f.getLocation();
			Location ploc = p.getEyeLocation();
			f.setVelocity( f.getVelocity().add( ploc.subtract( floc ).toVector().normalize().multiply( curve * speed ) ).normalize().multiply( speed ) );
		} else {
			fireballs.remove( f );
		}
	}
	
	public void findTarget( ProjectileSource shooter, Projectile ent ) {
		LivingEntity finalist = null;
		for ( Player p: Bukkit.getServer().getOnlinePlayers() ) {
			if ( ent.getWorld() == p.getWorld() && p.hasLineOfSight( ent ) && p.getGameMode() != GameMode.CREATIVE ) {
				finalist = p;
			}
		}
		if ( !( shooter instanceof Player ) && !( shooter instanceof Wither ) && !( shooter instanceof Snowman ) ) {
			if ( finalist != null ) {
				fireballs.put( ent, finalist );
			} else {
				return;
			}
		}
		if ( ( shooter == finalist || ( finalist == null && shooter instanceof Player ) || shooter instanceof Wither ) ) {
			for ( int i = 1; i < maxRange; i++ ) {
				List< Entity > entities = ent.getNearbyEntities( i , i , i );
				for ( Iterator< Entity > it = entities.iterator(); it.hasNext(); ) {
					Entity newent = it.next();
					if ( newent instanceof LivingEntity && newent != shooter && ( ( LivingEntity ) shooter ).hasLineOfSight( newent ) ) {
						Vector fov = newent.getLocation().subtract( ( ( Entity ) shooter ).getLocation() ).toVector().normalize();
						Vector direction = ( ( Entity ) shooter ).getLocation().getDirection();
						if ( ( Math.abs( fov.getX() - direction.getX() ) <= LS && Math.abs( fov.getY() - direction.getY() ) <= LS && Math.abs( fov.getZ() - direction.getZ() ) <= LS ) || shooter instanceof Wither ) {
							fireballs.put( ent, ( LivingEntity ) newent );
							return;
						}
					}
				}
			}
		}
		if ( finalist != null && shooter != finalist ) {
			fireballs.put( ent, finalist );
		}
	}

	@Override
	public ItemStack getItem() {
		return null;
	}
}
