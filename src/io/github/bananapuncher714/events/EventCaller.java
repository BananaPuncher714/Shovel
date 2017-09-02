package io.github.bananapuncher714.events;

import io.github.bananapuncher714.ShovelMain;
import io.github.bananapuncher714.util.NBTEditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.NBTTagByte;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EventCaller implements Listener {
	static HashMap< UUID, Long > players = new HashMap< UUID, Long >();
	static HashMap< UUID, Long > rightClicked = new HashMap< UUID, Long >();
	
	public EventCaller() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( ShovelMain.getMain(), new Runnable() {

			@Override
			public void run() {
				for ( Iterator< UUID > it = players.keySet().iterator(); it.hasNext(); ) {
					UUID u = it.next();
					Player player = Bukkit.getPlayer( u );
					if ( player == null ) {
						it.remove();
						continue;
					}
					
					if ( System.currentTimeMillis() - players.get( u ) > 300 ) {
						it.remove();
						PlayerReleaseRightClickEvent event = new PlayerReleaseRightClickEvent( player );
						Bukkit.getPluginManager().callEvent( event );
					} else {
						PlayerHoldRightClickEvent event = new PlayerHoldRightClickEvent( player );
						Bukkit.getPluginManager().callEvent( event );
					}
				}
			}
		}, 0, 1 );
	}
	
	@EventHandler
	public void onPlayerInteractEvent( PlayerInteractEvent e ) {
		if ( !e.getAction().equals( Action.RIGHT_CLICK_AIR ) && !e.getAction().equals( Action.RIGHT_CLICK_BLOCK ) ) return;
		if ( !e.getHand().equals( EquipmentSlot.HAND ) ) return;
		Player player = e.getPlayer();
		boolean ahd = players.containsKey( player.getUniqueId() ) && System.currentTimeMillis() - players.get( player.getUniqueId() ) <= 300;
		PlayerPressRightClickEvent event = new PlayerPressRightClickEvent( player, ahd );
		Bukkit.getPluginManager().callEvent( event );
		if ( event.cancelInteractEvent() ) e.setCancelled( true );
		if ( !event.isCancelled() ) {
			if ( !ahd ) rightClicked.put( player.getUniqueId() , System.currentTimeMillis() );
			players.put( player.getUniqueId(), System.currentTimeMillis() );
		}
	}
	
	public static long getTimeSinceLastClicked( Player player ) {
		if ( !players.containsKey( player.getUniqueId() ) ) return 1000000;
		return System.currentTimeMillis() - players.get( player.getUniqueId() );
	}
	
	public static long lastRightClicked( Player player ) {
		if ( !rightClicked.containsKey( player.getUniqueId() ) ) return 1000000;
		return System.currentTimeMillis() - rightClicked.get( player.getUniqueId() );
	}
}
