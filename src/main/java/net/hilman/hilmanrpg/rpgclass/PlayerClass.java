package net.hilman.hilmanrpg.rpgclass;

public enum PlayerClass {
    MAGE("hills-rpg:class_mage"),
    WARRIOR("hills-rpg:class_warrior"),
    ROGUE("hills-rpg:class_rogue");

    private final String scoreboardTag;

    PlayerClass(String scoreboardTag) {
        this.scoreboardTag = scoreboardTag;
    }

    public String scoreboardTag() {
        return scoreboardTag;
    }
}