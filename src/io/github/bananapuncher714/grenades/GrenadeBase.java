package io.github.bananapuncher714.grenades;

import java.util.HashMap;

import io.github.bananapuncher714.modules.Module;

public abstract class GrenadeBase extends Module {
	static HashMap< String, Class< ? extends GrenadeBase > > children = new HashMap< String, Class< ? extends GrenadeBase > >();
	
	public static void registerNewBase( Class< ? extends GrenadeBase > base ) {
		children.put( base.getSimpleName(), base );
	}
	
	public static Module instantiateBase( String clazzName, Object... parameters ) throws Exception {
		Class< ? extends GrenadeBase > baseClazz = children.get( clazzName );
		if ( baseClazz == null ) return null;
		
		return Module.instantiate( baseClazz, parameters );
	}
	
}
