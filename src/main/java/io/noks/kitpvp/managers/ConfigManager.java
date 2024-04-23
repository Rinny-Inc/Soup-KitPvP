package io.noks.kitpvp.managers;

import io.noks.kitpvp.Main;

public class ConfigManager {
	public final String domainName;
	public final String tabHeader, tabFooter;
	public final String motdFirstLine, motdSecondLine;
	public final boolean sendJoinAndQuitMessageToOP;
	
	public ConfigManager(Main main) {
		this.domainName = main.getConfig().getString("domain-name");
		this.tabHeader = main.getConfig().getString("tab.header");
		this.tabFooter = main.getConfig().getString("tab.footer");
		this.motdFirstLine = main.getConfig().getString("motd.first-line");
		this.motdSecondLine = main.getConfig().getString("motd.second-line");
		this.sendJoinAndQuitMessageToOP = main.getConfig().getBoolean("send-join-and-quit-message-to-op");
	}
}
