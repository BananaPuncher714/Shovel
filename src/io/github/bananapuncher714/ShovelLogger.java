package io.github.bananapuncher714;

import org.bukkit.Bukkit;

public class ShovelLogger {
	
	public static void info( String msg ) {
		Bukkit.getLogger().info( msg );
	}
	
	public static void warning( String msg ) {
		Bukkit.getLogger().warning( msg );
	}
	
	public static void severe( String msg ) {
		Bukkit.getLogger().severe( msg );
	}
}
