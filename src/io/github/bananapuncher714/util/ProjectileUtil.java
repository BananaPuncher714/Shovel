package io.github.bananapuncher714.util;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class ProjectileUtil {
	static Random r = new Random();
	
	public static boolean inWater( Entity ent ) {
		return Arrays.asList( new Material[] { Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA } ).contains( ent.getLocation().getBlock().getType() );
	}
	
	public static void randomizeSpread( Projectile projectile, float degree, float raise ) {
		Location location = projectile.getLocation();
		final double speed = projectile.getVelocity().length();
		float yaw = location.getYaw();
		float pitch = location.getPitch();
		float deg = yaw + ( r.nextFloat() * ( 2 * degree ) ) - degree;
		float ydeg = pitch + ( r.nextFloat() * degree ) + raise;
		location.setYaw( 360 - deg );
		location.setPitch( 360 - ydeg );
		Vector newVec = location.getDirection().clone();
		newVec.normalize().multiply( speed );
		projectile.setVelocity( newVec );
	}
}
