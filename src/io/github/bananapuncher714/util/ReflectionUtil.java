package io.github.bananapuncher714.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUtil {
	private static ArrayList<Object> particlesCache;
	private static HashMap< String, Class<?> > classCache;
	
	public ReflectionUtil() {

	}

	static {
		particlesCache = new ArrayList<Object>();
		classCache = new HashMap< String, Class<?> >();
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			classCache.put("PacketPlayOutWorldParticles",Class.forName("net.minecraft.server." + version + "." + "PacketPlayOutWorldParticles"));
			classCache.put("PacketPlayOutChat",Class.forName("net.minecraft.server." + version + "." + "PacketPlayOutChat"));
			classCache.put("IChatBaseComponent",Class.forName("net.minecraft.server." + version + "." + "IChatBaseComponent"));
			classCache.put("EnumParticle",Class.forName("net.minecraft.server." + version + "." + "EnumParticle"));
			classCache.put("Packet",Class.forName("net.minecraft.server." + version + "." + "Packet"));
			classCache.put("PlayerConnection",Class.forName("net.minecraft.server." + version + "." + "PlayerConnection"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Object[] obj = getNMSClass("EnumParticle").getEnumConstants();
		for (Object object : obj) {
			particlesCache.add(object);
		}
	}

	private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		Object nmsPlayer = getHandle.invoke(player);
		Field conField = nmsPlayer.getClass().getField("playerConnection");
		Object con = conField.get(nmsPlayer);
		return con;
	}

	public static void sendParticles(Player player, Object particle, boolean distEx, float x, float y, float z, 
			float xOffset, float yOffset, float zOffset, float speed, int amount, int[] moreData) 
					throws ClassNotFoundException, SecurityException, NoSuchMethodException, 
					IllegalArgumentException, InstantiationException, IllegalAccessException, 
					InvocationTargetException, NoSuchFieldException {
		Class<?> packetClass = getNMSClass("PacketPlayOutWorldParticles");
		Constructor<?> packetConstructor = packetClass.getConstructor(particle.getClass(), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
		Object packet = packetConstructor.newInstance(particle, distEx, x, y, z, xOffset, yOffset, zOffset, speed, amount, moreData);
		Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
		sendPacket.invoke(getConnection(player), packet);

	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getPrimitiveClass( Class<?> clazz ) {
        if ( clazz.getSimpleName().equals( "Byte" ) )
            return byte.class;
        if ( clazz.getSimpleName().equals( "Short" ) )
            return short.class;
        if ( clazz.getSimpleName().equals( "Integer" ) )
            return int.class;
        if ( clazz.getSimpleName().equals( "Long" ) )
            return long.class;
        if ( clazz.getSimpleName().equals( "Character" ) )
            return char.class;
        if ( clazz.getSimpleName().equals( "Float" ) )
            return float.class;
        if ( clazz.getSimpleName().equals( "Double" ) )
            return double.class;
        if ( clazz.getSimpleName().equals( "Boolean" ) )
            return boolean.class;
        if ( clazz.getSimpleName().equals( "Void" ) )
            return void.class;
        return clazz;
    }
	
	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		if (!classCache.isEmpty()) {
			for ( String clazz : classCache.keySet()) {
				if ( clazz.equalsIgnoreCase( name ) ) return classCache.get( name );
			}
		} 

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getEnumParticleConst(String name) {

		if (!particlesCache.isEmpty()) {
			for (Object object : particlesCache) {
				if (object.toString().equalsIgnoreCase(name)) {
					return object;
				} else {
					continue;
				}
			}
			return null;
		} else {
			Object[] obj = getNMSClass("EnumParticle").getEnumConstants();

			for (Object object : obj) {
				if (object.toString().toLowerCase().equals(name.toLowerCase())) {
					return object;
				} else {
					continue;
				}
			}
			return null;
		}
	}

}
