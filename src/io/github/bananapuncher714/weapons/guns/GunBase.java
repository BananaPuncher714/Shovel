package io.github.bananapuncher714.weapons.guns;

import java.io.File;
import java.util.HashMap;

import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.bullets.BulletBase;
import io.github.bananapuncher714.events.BulletHitEvent;
import io.github.bananapuncher714.events.PlayerHoldRightClickEvent;
import io.github.bananapuncher714.events.PlayerPressRightClickEvent;
import io.github.bananapuncher714.events.PlayerReleaseRightClickEvent;
import io.github.bananapuncher714.modules.Module;
import io.github.bananapuncher714.modules.ModuleManager;
import io.github.bananapuncher714.modules.PreloadedModule;
import io.github.bananapuncher714.objects.Dependency;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public abstract class GunBase extends Module {
	static File dataFolder;
	static HashMap< String, Class< ? extends GunBase > > children = new HashMap< String, Class< ? extends GunBase > >();
	
	public static void registerBase( Class< ? extends GunBase > base ) {
		children.put( base.getSimpleName(), base );
	}
	
	public static Module instantiateBase( String clazzName, Object... parameters ) throws Exception {
		Class< ? extends GunBase > baseClazz = children.get( clazzName );
		if ( baseClazz == null ) return null;
		
		return Module.instantiate( baseClazz, parameters );
	}
	
	public static void load( File dir ) {
		dataFolder = dir;
		for ( File gun : dir.listFiles() ) {
			if ( gun.getName().equalsIgnoreCase( "config.yml" ) ) continue;
			
			FileConfiguration config = YamlConfiguration.loadConfiguration( gun );
			
			PreloadedModule gunBase = new PreloadedModule( "GunBase", config.getString( "base-name" ), config.getString( "identifier" ), config );
			ShovelMain.getMain().loadModule( gunBase );
		}
	}
	
	// NO MORE ANNOYING REFLECTION!! YAY!
	protected BulletBase base;
	protected Material material;
	protected short data;
	
	public abstract void onHoldEvent( PlayerHoldRightClickEvent event );
	public abstract void onPressEvent( PlayerPressRightClickEvent event );
	public abstract void onReleaseEvent( PlayerReleaseRightClickEvent event );
	public abstract void onBulletHitEvent( BulletHitEvent event );
	public abstract boolean checkItem( ItemStack item );
	public abstract ItemStack getItem();
	
	public void load( FileConfiguration config ) {
		super.load( config );
		material = Material.getMaterial( config.getString( "item.material" ) );
		data = ( short ) config.getInt( "item.data" );
		base = ( BulletBase ) ModuleManager.getBaseFromMod( "BulletBase", config.getString( "bullet.type" ) );
		dependencies.add( new Dependency( "BulletBase", config.getString( "bullet.type" ) ) );
	}
	
	@Override
	public void run() {
	}
	
	@EventHandler
	public void onPlayerHoldRightClickEvent( PlayerHoldRightClickEvent event ) {
		onHoldEvent( event );
	}
	
	@EventHandler
	public void onPlayerPressRightClickEvent( PlayerPressRightClickEvent event ) {
		onPressEvent( event );
	}
	
	@EventHandler
	public void onPlayerHoldRightClickEvent( PlayerReleaseRightClickEvent event ) {
		onReleaseEvent( event );
	}
	
	@EventHandler
	public void onABulletHitEvent( BulletHitEvent event ) {
		onBulletHitEvent( event );
	}
	
	public int getSpray( Player player, int spray ) {
		double percent = 1;
		if ( player.isSneaking() ) percent -= .7;
		if ( player.isSprinting() ) percent += .5;
		if ( player.isOnGround() ) percent += .5;
		return ( int ) ( percent * spray );
	}
}
