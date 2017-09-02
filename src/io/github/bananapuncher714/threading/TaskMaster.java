package io.github.bananapuncher714.threading;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.ShovelMain;

public class TaskMaster {
	static ShovelMain main = ShovelMain.getMain();
	
	public static void runSyncTask( Runnable runnable ) {
		Bukkit.getScheduler().scheduleSyncDelayedTask( main, runnable );
	}
	
	public static void runSyncRepeatingTask( Runnable runnable, int delay ) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( main, runnable, 0, delay );
	}

}
