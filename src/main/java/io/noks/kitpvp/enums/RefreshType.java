package io.noks.kitpvp.enums;

public enum RefreshType {
	KILLS("kills", true),
	KILLSTREAK("ks", true),
	DEATHS("deaths", true),
	CREDITS("coins", false),
	COMBATTAG("tag", false);
	
	private String name;
	private boolean scan;
	RefreshType(String name, boolean scan){
		this.name = name;
		this.scan = scan;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean canBeScanned() {
		return this.scan;
	}
}
