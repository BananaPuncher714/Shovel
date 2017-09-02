package io.github.bananapuncher714.util;

import java.util.Random;

import org.bukkit.util.Vector;

public class VectorUtils {
	
	public static Vector randomizeSpread( Vector vec, double deg ) {
		double length = vec.length();
		vec.normalize();
		double x = vec.getX();
		double y = vec.getY();
		double z = vec.getZ();
		
		double totalAmount = x + z + y;
		
		double xratio = x / totalAmount;
		double yratio = y / totalAmount;
		double zratio = z / totalAmount;
		
		double flatLength = Math.sqrt( x * x + z * z );
		double ratio = y / flatLength;
		
		double degree = FastMath.atan2( ( float ) x, ( float ) z ) * FastMath.DEG;
		degree = degree + ( new Random().nextDouble() * ( 2 * deg ) ) - deg;
		double ydeg = FastMath.atan2( ( float ) flatLength, ( float ) vec.getY() ) * FastMath.DEG + ( new Random().nextDouble() * deg );
		double rad = ( degree * Math.PI ) / 180.0;
		double yrad = ( ydeg * Math.PI ) / 180.0;
		
		vec.setZ( flatLength * Math.cos( rad ) );
		vec.setX( flatLength * Math.sin( rad ) );
		vec.setY( ratio * Math.sin( yrad ) );
		vec.normalize().multiply( length );
		return vec;
	}
}
