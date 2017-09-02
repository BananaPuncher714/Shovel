package io.github.bananapuncher714.modules;

public class PreloadedModule {
	String moduleName, baseName, identifier;
	Object[] parameters;
	
	public PreloadedModule( String moduleName, String baseName, String identifier, Object... parameters ) {
		this.moduleName = moduleName;
		this.baseName = baseName;
		this.parameters = parameters;
		this.identifier = identifier;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getBaseName() {
		return baseName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Object[] getParameters() {
		return parameters;
	}

}
