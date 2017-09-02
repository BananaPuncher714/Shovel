package io.github.bananapuncher714;

import io.github.bananapuncher714.bullets.BouncingBullet;
import io.github.bananapuncher714.bullets.BulletBase;
import io.github.bananapuncher714.bullets.HomingBullet;
import io.github.bananapuncher714.bullets.StandardBullet;
import io.github.bananapuncher714.command.Main;
import io.github.bananapuncher714.events.EventCaller;
import io.github.bananapuncher714.exceptions.DependencyNotFoundException;
import io.github.bananapuncher714.modules.ModuleManager;
import io.github.bananapuncher714.modules.PreloadedModule;
import io.github.bananapuncher714.objects.Dependency;
import io.github.bananapuncher714.weapons.AutomaticWeapon;
import io.github.bananapuncher714.weapons.guns.GunBase;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.plugin.java.JavaPlugin;

public class ShovelMain extends JavaPlugin {
	static ShovelMain main;
	ArrayList< PreloadedModule > pmods = new ArrayList< PreloadedModule >();
	
	@Override
	public void onEnable() {
		main = this;
		getCommand( "gun" ).setExecutor( new Main() );
		
		ModuleManager.registerModule( GunBase.class );
		ModuleManager.registerModule( BulletBase.class );
		
		ModuleManager.addModBase( StandardBullet.class );
		ModuleManager.addModBase( HomingBullet.class );
		ModuleManager.addModBase( BouncingBullet.class );
		
		ModuleManager.addModBase( AutomaticWeapon.class );

//		// NOTE THAT 4.5 IS THE MAXIMUM SPEED FOR BULLETS WITHOUT WARPING!!!

		
		// TIME TO MAKE SOME SIMPLE GUNS!!
//		ModuleManager.loadModule( "GunBase", "AutomaticWeapon", "rocket launcher", "missle", "rocket launcher", 0, 1, 1500, false );
//		ModuleManager.loadModule( "GunBase", "AutomaticWeapon", "ar-15", "30 cal", "ar-15", 18, 1, 0, true );
//		ModuleManager.loadModule( "GunBase", "AutomaticWeapon", "nova", "shell", "nova", 14, 8, 700, false );
//		ModuleManager.loadModule( "GunBase", "AutomaticWeapon", "flamethrower", "fire", "flamethrower", 2, 14, 90, false );
//		ModuleManager.loadModule( "GunBase", "AutomaticWeapon", "bouncer", "ricochet", "bouncer", 0, 1, 0, false );
//		} catch ( Exception e ) {
//			e.printStackTrace();
//		}
		
		Bukkit.getPluginManager().registerEvents( new EventCaller(), this );
		Bukkit.getScheduler().scheduleAsyncDelayedTask( this, new Runnable() {
			@Override
			public void run() {
				loadModules( pmods );
			}
		}, 20 );
	}
	
	public boolean onCommand( CommandSender s, Command c, String l, String[] a ) {
		if ( !( s instanceof Player ) ) return false;
		Player p = ( Player ) s;
		if ( c.getName().equalsIgnoreCase( "shovel" ) ) {
		} else if ( c.getName().equalsIgnoreCase( "gettag" ) ) {
		}
		return true;
	}
	
	public static ShovelMain getMain() {
		return main;
	}
	
	public void loadModule( PreloadedModule module ) {
		pmods.add( module );
	}
	
	private void loadModules( ArrayList< PreloadedModule > pmods ) {
		while ( !pmods.isEmpty() ) {
			int attempt = pmods.size();
			for ( Iterator< PreloadedModule > it = pmods.iterator(); it.hasNext(); ) {
				PreloadedModule mod = it.next();
				try {
					ModuleManager.loadModule( mod );
					ShovelLogger.info( "Loaded " + mod.getModuleName() + "." + mod.getBaseName() + "." + mod.getIdentifier() );
					it.remove();
				} catch ( DependencyNotFoundException exception ) {
					if ( --attempt <= 0 ) {
						Dependency missingDependency = exception.getMissingDependency();
						ShovelLogger.severe( mod.getModuleName() + "." + mod.getBaseName() + "." + mod.getIdentifier() + " was unable to be loaded!" );
						ShovelLogger.severe( missingDependency.getModuleName() + "." + missingDependency.getBaseName() + " was not located!" );
						it.remove();
					}
				} catch ( Exception exception ) {
					it.remove();
					ShovelLogger.severe( "There was an unexpected error while attempting to load a module!" );
					ShovelLogger.severe( mod.getModuleName() + "." + mod.getBaseName() + "." + mod.getIdentifier() );
					exception.printStackTrace();
				}
			}
		}
	}
}
