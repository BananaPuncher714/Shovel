package io.github.bananapuncher714.bullets;

import org.bukkit.entity.Projectile;

public class Bullet {
	BulletBase type;
	Projectile projectile;
	
	public Bullet( Projectile bullet, BulletBase type ) {
		this.type = type;
		projectile = bullet;
	}
	
	public Projectile getProjectile() {
		return projectile;
	}
	
	public BulletBase getType() {
		return type;
	}
}
