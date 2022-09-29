package io.noks.kitpvp.managers.caches;

public class Settings {
	private boolean compass;
	private int swordSlot;
	private int itemSlot;
	private int compassSlot;

	public Settings() {
		this.compass = true;
		this.swordSlot = 0;
		this.itemSlot = 1;
		this.compassSlot = 8;
	}
	public Settings(boolean compassEnabled, int sword, int item, int compass) {
		this.compass = compassEnabled;
		this.swordSlot = sword;
		this.itemSlot = item;
		this.compassSlot = compass;
	}

	public boolean hasCompass() {
		return this.compass;
	}

	public void updateCompass() {
		this.compass = !this.compass;
	}

	public int getSlot(SlotType type) {
		switch (type) {
		case SWORD:
			return this.swordSlot;
		case ITEM:
			return this.itemSlot;
		case COMPASS:
			return this.compassSlot;
		}

		return 0;
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
			} else if (toSwitch == SlotType.COMPASS) {
				this.compassSlot = this.swordSlot;
			}
			this.swordSlot = slot;
			break;
		case ITEM:
			if (toSwitch == SlotType.SWORD) {
				this.swordSlot = this.itemSlot;
			} else if (toSwitch == SlotType.COMPASS) {
				this.compassSlot = this.itemSlot;
			}
			this.itemSlot = slot;
			break;
		case COMPASS:
			if (toSwitch == SlotType.SWORD) {
				this.swordSlot = this.compassSlot;
			} else if (toSwitch == SlotType.ITEM) {
				this.itemSlot = this.compassSlot;
			}
			this.compassSlot = slot;
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
		if (slot == this.compassSlot) {
			return SlotType.COMPASS;
		}
		return null;
	}

	private boolean isSlotTaken(int slot) {
		return (this.swordSlot == slot || this.itemSlot == slot || this.compassSlot == slot);
	}

	public enum SlotType {
		SWORD("Sword"), ITEM("Item"), COMPASS("Compass");

		private String name;

		SlotType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static SlotType getSlotTypeFromName(String name) {
			for (SlotType type : values()) {
				if (type.getName().toLowerCase().equals(name.toLowerCase())) {
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
