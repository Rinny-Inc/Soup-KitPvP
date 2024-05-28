package io.noks.kitpvp.interfaces;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;

public interface SignRotation {
	default Block getBlockBehindSign(Block signBlock, Sign sign) {
        MaterialData signData = sign.getData();

        switch (signData.getData()) {
            case 2:
                return signBlock.getRelative(0, 0, 1);
            case 3:
                return signBlock.getRelative(0, 0, -1);
            case 4:
                return signBlock.getRelative(1, 0, 0);
            case 5:
                return signBlock.getRelative(-1, 0, 0);
            default:
                return null;
        }
    }
}
