package io.github.bananapuncher714.modules;

import io.github.bananapuncher714.ShovelLogger;
import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.exceptions.DependencyNotFoundException;
import io.github.bananapuncher714.objects.Dependency;
import io.github.bananapuncher714.threading.TaskMaster;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;

public class ModuleManager {
	static HashMap< String, Class< ? extends Module > > moduleList = new HashMap< String, Class< ? extends Module > >();
	static HashMap< String, HashMap< String, Module > > modules = new HashMap< String, HashMap< String, Module > >();
	
	/**
	 * Registers an abstract module from which other modules can be built off
	 * 
	 * @param clazz
	 * An abstract module
	 */
	public static void registerModule( Class< ? extends Module > clazz ) {
		moduleList.put( clazz.getSimpleName(), clazz );
		try {
			// So we want each module to load itself and we tell it the data folder that belongs to it
			File dataFolder = new File( ShovelMain.getMain().getDataFolder() + "/data/" + clazz.getSimpleName() );
			dataFolder.mkdirs();
			clazz.getMethod( "load", File.class ).invoke( null, dataFolder );
		} catch( Exception exception ) {
			ShovelLogger.warning( "There was an error while loading module " + clazz.getName() );
		}
	}
	
	/**
	 * Gets the specified Module
	 * 
	 * @param clazzName
	 * The simple name of the module
	 * @return
	 * The module class
	 */
	private static Class< ? extends Module > getModule( String clazzName ) {
		return moduleList.containsKey( clazzName ) ? moduleList.get( clazzName ) : null;
	}
	
	/**
	 * Gets a module base from the list of registered bases
	 * 
	 * @param modName
	 * The simple name of the module
	 * @param baseName
	 * The friendly name of the base
	 * @return
	 * The module
	 */
	public static Module getBaseFromMod( String modName, String baseName ) {
		HashMap< String, Module > mods = modules.containsKey( modName ) ? modules.get( modName ) : null;
		if ( mods == null ) return null;
		return mods.containsKey( baseName ) ? mods.get( baseName ) : null;
	}
	
	/**
	 * Adds a base to a certain module
	 * 
	 * @param clazz
	 * The class of the base to add
	 */
	public static void addModBase( Class< ? extends Module > clazz ) {
		Class< ? extends Module > module = getModule( clazz.getSuperclass().getSimpleName() );
		if ( module != null ) {
			try {
				Method loadBase = module.getMethod( "registerBase", Class.class );
				loadBase.invoke( null, clazz );
			} catch( Exception exception ) {
				exception.printStackTrace();
			}
		} else {
			System.out.println( clazz.getSimpleName() + " is not a recognized module! Aborting!" );
		}
	}
	
	public static void loadModule( PreloadedModule pmod ) throws Exception {
		loadModule( pmod.getModuleName(), pmod.getBaseName(), pmod.getIdentifier(), pmod.getParameters() );
	}
	
	/**
	 * This will attempt to load a module and cache it under the identifier
	 * 
	 * @param moduleName
	 * The name of the module that it belongs to
	 * @param baseName
	 * The name of the base class
	 * @param identifier
	 * The friendly name to cache it under
	 * @param paramObjects
	 * The parameters as specified by the base's constructor
	 * @throws Exception 
	 */
	public static void loadModule( String moduleName, String baseName, String identifier, Object... paramObjects ) throws Exception {
		Class< ? extends Module > mod = getModule( moduleName );
		if ( mod == null ) {
			System.out.println( moduleName + " is not recognized! Aborting!" );
		}
		Module base = null;
		try {
			Method instantiate = mod.getMethod( "instantiateBase", String.class, Object[].class );
			base = ( Module ) instantiate.invoke( null, baseName, paramObjects );
			for ( Dependency dependency : base.getDependencies() ) {
				if ( !hasDependency( dependency ) ) throw new DependencyNotFoundException( dependency );
			}
		} catch( Exception exception ) {
			if ( !( exception instanceof DependencyNotFoundException ) ) {
				ShovelLogger.warning( baseName + "(" + moduleName + ") was unable to be instantiated!" );
				ShovelLogger.warning( "Incorrect parameters or missing constructor, perhaps?" );
			}
			throw exception;
		}
		HashMap< String, Module > mods = modules.containsKey( moduleName ) ? modules.get( moduleName ) : new HashMap< String, Module >();
		
		Bukkit.getPluginManager().registerEvents( base, ShovelMain.getMain() );
		TaskMaster.runSyncRepeatingTask( base, 1 );
		mods.put( identifier, base );
		modules.put( moduleName, mods );
	}
	
	public static boolean hasDependency( Dependency dependency ) {
		if ( !modules.containsKey( dependency.getModuleName() ) ) return false;
		HashMap< String, Module > bases = modules.get( dependency.getModuleName() );
		return bases.containsKey( dependency.getBaseName() );
	}
	
	public static ArrayList< String[] > listModules() {
		ArrayList< String[] > moduleList = new ArrayList< String[] >();
		for ( String key : modules.keySet() ) {
			for ( String identifier : modules.get( key ).keySet() ) {
				moduleList.add( new String[] { key, identifier } );
			}
		}
		return moduleList;
	}
}
