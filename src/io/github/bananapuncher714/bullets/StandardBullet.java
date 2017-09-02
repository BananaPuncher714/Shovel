package io.github.bananapuncher714.bullets;

import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class StandardBullet extends BulletBase {
	double speed;
	
	public StandardBullet( Class< ? extends Projectile > clazz, double speed, boolean explosive, int maxTicks ) {
		projectileClazz = clazz;
		this.speed = speed;
		this.explosive = explosive;
		this.maxTicks = maxTicks;
	}
	
	public StandardBullet( FileConfiguration config ) {
		load( config );
		speed = config.getDouble( "speed" );
	}
	
	@Override
	public Bullet shootBullet(LivingEntity shooter) {
		Projectile bullet = shooter.launchProjectile( projectileClazz );
		bullet.setGravity( gravity );
		if ( bullet instanceof Fireball ) {
			( ( Fireball ) bullet ).setDirection( ( ( Fireball ) bullet ).getDirection().normalize().multiply( speed ) );
		} else {
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
			if ( proj instanceof Fireball ) {
				( ( Fireball ) proj ).setDirection( ( ( Fireball ) proj ).getDirection().normalize().multiply( speed ) );
			} else {
				proj.setVelocity( proj.getVelocity().normalize().multiply( speed ) );
			}
		}
	}

	@Override
	public void onBulletHitEvent( ProjectileHitEvent event ) {
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

}
