package io.noks.kitpvp.interfaces;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;

public interface SignRotation {
	default Block getBlockBehindSign(Block signBlock, Sign sign) {
        MaterialData signData = sign.getData();

        return switch (signData.getData()) {
            case 2 -> signBlock.getRelative(0, 0, 1);
            case 3 -> signBlock.getRelative(0, 0, -1);
            case 4 -> signBlock.getRelative(1, 0, 0);
            case 5 -> signBlock.getRelative(-1, 0, 0);
            default -> throw new IllegalArgumentException("Unexpected value: " + signData.getData());
        };
    }
}
