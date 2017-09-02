package io.github.bananapuncher714.exceptions;

import io.github.bananapuncher714.objects.Dependency;

public class DependencyNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	Dependency dependency;
	
	public DependencyNotFoundException( Dependency dependency ) {
		this.dependency = dependency;
	}
	
	public Dependency getMissingDependency() {
		return dependency;
	}

}
