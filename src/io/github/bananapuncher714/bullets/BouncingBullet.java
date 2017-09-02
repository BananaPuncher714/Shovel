package io.github.bananapuncher714.bullets;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BouncingBullet extends BulletBase {
	HashMap< Projectile, Integer > bounces = new HashMap< Projectile, Integer >();
	int maxBounces;
	double bounceReduc = .7;
	
	public BouncingBullet( Class< ? extends Projectile > clazz, int bounces ) {
		maxBounces = bounces;
	}
	
	public BouncingBullet( FileConfiguration config ) {
		load( config );
		maxBounces = config.getInt( "max-bounces" );
		bounceReduc = config.getDouble( "bounce-reduction" );
	}
	
	@Override
	public Bullet shootBullet(LivingEntity shooter) {
		Projectile bullet = shooter.launchProjectile( projectileClazz );
		bullet.setGravity( gravity );
		Bullet theBullet = new Bullet( bullet, this );
		bounces.put( bullet, maxBounces );
		bullets.add( theBullet );
		return theBullet;
	}

	@Override
	public void loop() {

	}

	@Override
	public void onBulletHitEvent(ProjectileHitEvent event) {
		Projectile bullet = event.getEntity();
		if ( !bounces.containsKey( bullet ) ) return;
		int bouncesLeft = bounces.get( bullet );
		if ( event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity ) {
			bounces.remove( bullet );
			return;
		}
		bouncesLeft = bouncesLeft - 1;
		if ( bouncesLeft < 0 ) {
			bounces.remove( bullet );
			return;
		}
		Vector vec = bullet.getVelocity().clone();
		Location loc = bullet.getLocation().getBlock().getLocation();
		Projectile ball = bullet.getWorld().spawn( bullet.getLocation(), projectileClazz );
        if ( loc.clone().add( 1, 0, 0 ).getBlock().getType() == Material.AIR && loc.clone().add( -1, 0, 0 ).getBlock().getType() == Material.AIR ) {
        	vec.setX( - vec.getX() );
        } 
        if ( loc.clone().add( 0, 1, 0 ).getBlock().getType() == Material.AIR && loc.clone().add( 0, -1, 0 ).getBlock().getType() == Material.AIR ) {
        	vec.setY( - vec.getY() );
        } 
        if ( loc.clone().add( 0, 0, 1 ).getBlock().getType() == Material.AIR && loc.clone().add( 0, 0, -1 ).getBlock().getType() == Material.AIR ) {
        	vec.setZ( - vec.getZ() );
        }
        ball.setShooter( bullet.getShooter() );
        vec.setX( vec.getX() * ( - bounceReduc ) );
        vec.setY( vec.getY() * ( - bounceReduc ) );
        vec.setZ( vec.getZ() * ( - bounceReduc ) );
        ball.setVelocity( vec );
        ball.setGravity( true );
        bounces.put( ball, bouncesLeft );
        bounces.remove( bullet );
        Bullet theBullet = new Bullet( ball, this );
        bullets.add( theBullet );
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

}
