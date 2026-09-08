package net.hilman.hilmanrpg.rpgclass;

import net.minecraft.entity.player.PlayerEntity;

public final class ClassManager {
    private ClassManager() {
    }

    public static boolean hasAnyClass(PlayerEntity player) {
        for (PlayerClass playerClass : PlayerClass.values()) {
            if (player.getCommandTags().contains(playerClass.scoreboardTag())) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(PlayerEntity player, PlayerClass playerClass) {
        return player.getCommandTags().contains(playerClass.scoreboardTag());
    }

    public static boolean assign(PlayerEntity player, PlayerClass playerClass) {
        if (hasAnyClass(player)) {
            return false;
        }
        return player.addCommandTag(playerClass.scoreboardTag());
    }
}