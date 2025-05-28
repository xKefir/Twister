package org.minerail.twister.file.config;

public enum ConfigKey {

    SETTINGS_GUI_MODULE_ENABLED("Settings.Gui.open"),
    SETTINGS_GUI_TITLE("Settings.Gui.title"),
    SETTINGS_GUI_SIZE("Settings.Gui.size"),
    SETTINGS_GUI_SIZESELECTOR_ITEMS_TYPE("Settings.Gui.SizeSelector.items.type"),
    SETTINGS_GUI_SIZESELECTOR_ITEMS_SHOW_AMOUNTS("Settings.Gui.SizeSelector.items.show-amounts"),

    SETTINGS_HOOK_WORLDGUARD_MODULE_ENABLED("Settings.Hook.WorldGuard.enabled"),
    SETTINGS_HOOK_WORLDGUARD_TAKE_PLAYERS_INV("Settings.Hook.WorldGuard.take-players-inventory"),
    SETTINGS_HOOK_WORLDGUARD_GAME_REGION("Settings.Hook.WorldGuard.game-region"),

    SETTINGS_GAME_SUBTRACTED_TIME_MULTIPLIER("Settings.Game.subtracted-time-multiplier"),
    SETTINGS_GAME_SECONDS_DURATION_ROUND("Settings.Game.seconds-duration-round"),
    SETTINGS_GAME_MIN_PLAYERS("Settings.Game.min-players"),
    SETTINGS_GAME_MAX_FIELD_SIZE("Settings.Game.max-field-size"),

    ARENA_WORLD("Arena.world"),

    ARENA_POS1_X("Arena.pos1.x"),
    ARENA_POS1_Y("Arena.pos1.y"),
    ARENA_POS1_Z("Arena.pos1.z"),

    ARENA_POS2_X("Arena.pos2.x"),
    ARENA_POS2_Y("Arena.pos2.y"),
    ARENA_POS2_Z("Arena.pos2.z"),

    ARENA_TP_POS_X("Arena.teleport-position.x"),
    ARENA_TP_POS_Y("Arena.teleport-position.y"),
    ARENA_TP_POS_Z("Arena.teleport-position.z"),

    ARENA_LOSE_POS_X("Arena.lose-position.x"),
    ARENA_LOSE_POS_Y("Arena.lose-position.y"),
    ARENA_LOSE_POS_Z("Arena.lose-position.z");

    final String path;
    ConfigKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
