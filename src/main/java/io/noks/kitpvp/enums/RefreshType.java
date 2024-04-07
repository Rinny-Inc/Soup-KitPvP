package io.noks.kitpvp.enums;

public enum RefreshType {
	KILLS("kills"),
	KILLSTREAK("ks"),
	DEATHS("deaths"),
	CREDITS("coins"),
	COMBATTAG("tag");
	
	private String name;
	RefreshType(String name){
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
