package io.github.bananapuncher714.modules;

import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.objects.Dependency;
import io.github.bananapuncher714.threading.TaskMaster;
import io.github.bananapuncher714.util.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * This represents a module in its simplest form;
 * It is used to provide a template for other "bases"
 * ex GunBase, GrenadeBase, BulletBase
 * which in turn have their more specific classes
 * ex StickyGrenadeBase; AutomaticWeaponBase
 * And those specific bases will have their own configuration so you
 * can create different weapons based off the bases;
 * 
 * @author BananaPuncher714
 */
public abstract class Module implements Listener, Runnable {
	public static Module instantiate( Class< ? extends Module > clazz, Object... paramObjects ) throws Exception {
		ArrayList< Class< ? > > parameterTypes = new ArrayList< Class< ? > >();
		for ( Object parameter : paramObjects ) {
			if ( parameter.getClass().getSimpleName().equals( "YamlConfiguration" ) ) {
				parameterTypes.add( FileConfiguration.class );
			} else {
				parameterTypes.add( ReflectionUtil.getPrimitiveClass( parameter.getClass() ) );
			}
		}
		Class< ? >[] clazzType = parameterTypes.toArray( new Class< ? >[ paramObjects.length ] );
		
		Module module = null;
		Constructor< ? > cons = clazz.getConstructor( clazzType );
		module = ( Module ) cons.newInstance( paramObjects );
		return module;
	}
	
	protected String identifier, baseName;
	protected HashSet< Dependency > dependencies = new HashSet< Dependency >();
	
	public HashSet< Dependency > getDependencies() {
		return dependencies;
	}
	
	@Override
	public abstract void run();
	public abstract ItemStack getItem();
	public void load( FileConfiguration config ) {
		identifier = config.getString( "identifier" );
		baseName = config.getString( "base-name" );
	}
}
