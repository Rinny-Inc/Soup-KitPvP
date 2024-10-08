package io.noks.kitpvp.managers.caches;

public class PlayerSettings {
	private boolean scoreboard;
	private int swordSlot;
	private int itemSlot;

	public PlayerSettings() {
		this.scoreboard = true;
		this.swordSlot = 0;
		this.itemSlot = 1;
	}
	public PlayerSettings(boolean scoreboard, int sword, int item) {
		this.scoreboard = scoreboard;
		this.swordSlot = sword;
		this.itemSlot = item;
	}
	public int getSlot(SlotType type) {
		return switch (type) {
			case SWORD -> this.swordSlot;
			case ITEM -> this.itemSlot;
			default -> 0;
		};
	}
	
	public boolean hasScoreboardEnabled() {
		return this.scoreboard;
	}
	
	public void updateScoreboardState() {
		this.scoreboard = !this.scoreboard;
	}

	public void setSlot(SlotType type, int slot) {
		SlotType toSwitch = null;
		if (isSlotTaken(slot)) {
			toSwitch = slotToType(slot);
		}
		if (toSwitch == type) {
			return;
		}
		switch (type) {
		case SWORD:
			if (toSwitch == SlotType.ITEM) {
				this.itemSlot = this.swordSlot;
			}
			this.swordSlot = slot;
			break;
		case ITEM:
			if (toSwitch == SlotType.SWORD) {
				this.swordSlot = this.itemSlot;
			}
			this.itemSlot = slot;
			break;
		}
	}

	private SlotType slotToType(int slot) {
		if (slot == this.swordSlot) {
			return SlotType.SWORD;
		}
		if (slot == this.itemSlot) {
			return SlotType.ITEM;
		}
		return null;
	}

	private boolean isSlotTaken(int slot) {
		return (this.swordSlot == slot || this.itemSlot == slot);
	}

	public enum SlotType {
		SWORD("Sword"), ITEM("Item");

		private String name;

		SlotType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static SlotType getSlotTypeFromName(String name) {
			for (SlotType type : values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}

		public static boolean contains(String name) {
			return (getSlotTypeFromName(name) != null);
		}
	}
}
