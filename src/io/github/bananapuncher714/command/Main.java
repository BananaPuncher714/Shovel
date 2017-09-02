package io.github.bananapuncher714.command;

import io.github.bananapuncher714.modules.Module;
import io.github.bananapuncher714.modules.ModuleManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Main implements CommandExecutor {

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( !sender.hasPermission( "shovel.admin" ) ) {
			sender.sendMessage( ChatColor.RED + "You do not have permission to use this command!" );
			return false;
		}
		if ( args.length == 0 ) {
			sender.sendMessage( ChatColor.RED + "Incorrect arguments! Usage: " + "/shovel <list|get>" );
		} else if ( args[ 0 ].equalsIgnoreCase( "list" ) ) {
			list( sender );
		} else if ( args[ 0 ].equalsIgnoreCase( "get" ) && args.length == 2 ) {
			if ( sender instanceof Player ) {
				give( ( Player ) sender, args[ 1 ] );
			} else {
				sender.sendMessage( ChatColor.RED + "You must be a player to run this command!" );
			}
		} else {
			sender.sendMessage( ChatColor.RED + "Incorrect arguments! Usage: " + "/shovel <list|get>" );
		}
		return false;
	}
	
	private void list( CommandSender sender ) {
		sender.sendMessage( ChatColor.GREEN + "Available items:" );
		for ( String[] keys : ModuleManager.listModules() ) {
			Module module = ModuleManager.getBaseFromMod( keys[ 0 ], keys[ 1 ] );
			if ( module.getItem() == null ) {
				continue;
			}
			sender.sendMessage( ChatColor.BLUE + keys[ 0 ] + "." + keys[ 1 ] );
		}
	}
	
	private boolean give( Player player, String modName ) {
		for ( String[] keys : ModuleManager.listModules() ) {
			if ( modName.equalsIgnoreCase( keys[ 0 ] + "." + keys[ 1 ] ) ) {
				ItemStack item = ModuleManager.getBaseFromMod( keys[ 0 ], keys[ 1 ] ).getItem();
				if ( item == null ) {
					player.sendMessage( ChatColor.RED + "That is not a valid item!" );
				} else {
					player.getInventory().addItem( item );
					player.sendMessage( ChatColor.BLUE + "You have recieved a " + keys[ 0 ] + "." + keys[ 1 ] + "!" );
				}
				return true;
			}
		}
		return false;
	}

}
