package io.github.bananapuncher714.objects;

public class Dependency {
	String moduleName, baseName;
	
	public Dependency( String moduleName, String baseName ) {
		this.moduleName = moduleName;
		this.baseName = baseName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public String getBaseName() {
		return baseName;
	}
}
