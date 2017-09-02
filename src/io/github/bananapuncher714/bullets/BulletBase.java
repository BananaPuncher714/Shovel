package io.github.bananapuncher714.bullets;

import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.modules.Module;
import io.github.bananapuncher714.modules.PreloadedModule;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * This is the root of all bullet types
 * This simply provides a class to extend from, and manages all other bullets.
 * 
 * @author BananaPuncher714
 */
public abstract class BulletBase extends Module implements Runnable {
	static File dataFolder;
	static HashMap< String, Class< ? extends BulletBase > > children = new HashMap< String, Class< ? extends BulletBase > >();
	
	public static void registerBase( Class< ? extends BulletBase > base ) {
		children.put( base.getSimpleName(), base );
	}
	
	public static Module instantiateBase( String clazzName, Object... parameters ) throws Exception {
		Class< ? extends BulletBase > baseClazz = children.get( clazzName );
		if ( baseClazz == null ) return null;
		
		return Module.instantiate( baseClazz, parameters );
	}
	
	public static void load( File dir ) {
		dataFolder = dir;
		for ( File bullet : dir.listFiles() ) {
			if ( bullet.getName().equalsIgnoreCase( "config.yml" ) ) continue;
			
			FileConfiguration config = YamlConfiguration.loadConfiguration( bullet );
			
			PreloadedModule gunBase = new PreloadedModule( "BulletBase", config.getString( "base-name" ), config.getString( "identifier" ), config );
			ShovelMain.getMain().loadModule( gunBase );
		}
	}
	
	// END OF BORING REFLECTION STUFF THAT MAY OR MAY NOT WORK
	protected HashSet< Bullet > bullets = new HashSet< Bullet >();
	protected int maxTicks = 100, damage = 5;
	protected boolean explosive = false, gravity = false;
	protected float explosionPower = 1f;
	protected Class< ? extends Projectile > projectileClazz;
	
	public abstract Bullet shootBullet( LivingEntity shooter );
	public abstract void loop();
	public abstract void onBulletHitEvent( ProjectileHitEvent event );
	
	public void load( FileConfiguration config ) {
		super.load( config );
		try {
			projectileClazz = ( Class< ? extends Projectile > ) Class.forName( "org.bukkit.entity." + config.getString( "projectile-type" ) );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		maxTicks = config.getInt( "max-ticks" );
		explosive = config.getBoolean( "explosive.enabled" );
		explosionPower = config.getInt( "explosive.power" );
		gravity = config.getBoolean( "gravity" );
		damage = config.getInt( "damage" );
	}
	
	public void run() {
		for ( Iterator< Bullet > it = bullets.iterator(); it.hasNext(); ) {
			Bullet bullet = it.next();
			Projectile proj = bullet.getProjectile();
			if ( proj.isDead() || proj.getTicksLived() >= maxTicks ) {
				proj.remove();
				it.remove();
				continue;
			}
		}
		loop();
	}
	
	public Bullet getBullet( Projectile proj ) {
		for ( Bullet bullet : bullets ) {
			if ( proj.equals( bullet.getProjectile() ) ) return bullet;
		}
		return null;
	}
	
	@EventHandler
	public void onProjectileHitEvent( ProjectileHitEvent e ) {
		Projectile projectile = e.getEntity();
		Bullet bullet = getBullet( projectile );
		
		if ( bullet == null ) return;
		if ( e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity ) {
			LivingEntity ent = ( LivingEntity ) e.getHitEntity();
			ent.damage( damage );
			ent.setNoDamageTicks( 0 );
		}
		if ( explosive ) {
			projectile.getWorld().createExplosion( projectile.getLocation(), explosionPower );
		}
		onBulletHitEvent( e );
		bullets.remove( bullet );
		projectile.remove();
	}
}
