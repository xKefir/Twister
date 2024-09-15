package org.minerail.twister.file.Message;

public enum MessageKey {

    MESSAGES_INPUT_TYPE("Messages.Input-type"),
    MESSAGES_PREFIX_STRING("Messages.prefix.string"),

    MESSAGES_CONSTANTS_LAST_WINNER("Messages.constants.last-winner"),

    MESSAGES_COMMAND_WITHOUT_ARGS_EVENT_NOT_STARTED("Messages.Command.without-args.event-not-started"),
    MESSAGES_COMMAND_WITHOUT_ARGS_JOINED("Messages.Command.without-args.joined"),
    MESSAGES_COMMAND_WITHOUT_ARGS_GAME_ALREADY_STARTED("Messages.Command.without-args.game-already-started"),
    MESSAGES_COMMAND_WITHOUT_ARGS_PLAYER_IS_ALREADY_IN_GAME("Messages.Command.without-args.player-is-already-in-game"),

    MESSAGES_COMMAND_HELP("Messages.Command.help"),

    MESSAGES_COMMAND_START_FIRST_TIME_TO_SENDER("Messages.Command.start.first-time-to-sender"),
    MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER("Messages.Command.start.second-time-to-sender"),
    MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER_ERROR("Messages.Command.start.second-time-to-sender-error"),
    MESSAGES_COMMAND_START_FIRT_TIME_BROADCAST("Messages.Command.start.first-time-broadcast"),
    MESSAGES_COMMAND_START_SECOND_TIME_BROADCAST("Messages.Command.start.second-time-broadcast"),
    MESSAGES_COMMAND_START_FIELD_SIZE_TOO_LARGE("Messages.Command.start.field-size-too-large"),

    MESSAGES_COMMAND_STOP_TO_SENDER("Messages.Command.stop.to-sender"),
    MESSAGES_COMMAND_STOP_BROADCAST("Messages.Command.stop.to-all-players"),
    MESSAGES_COMMAND_STOP_EVENT_IS_NOT_ACTIVE("Messages.Command.stop.event-is-not-active"),

    MESSAGES_COMMAND_TP_TO_SENDER("Messages.Command.tp.to-sender"),

    MESSAGES_COMMAND_KICK_TO_SENDER("Messages.Command.kick.to-sender"),
    MESSAGES_COMMAND_KICK_PLAYER_IS_NOT_IN_GAME_TO_SENDER("Messages.Command.kick.player-is-not-in-game-to-sender"),
    MESSAGES_COMMAND_KICK_TO_TARGETED_PLAYER("Messages.Command.kick.to-targeted-player"),

    MESSAGES_COMMAND_TOP_LIST("Messages.Command.top.List"),

    MESSAGES_COMMAND_RELOAD_SUCCESS("Messages.Command.reload.success"),
    MESSAGES_COMMAND_RELOAD_ERROR("Messages.Command.reload.error"),

    MESSAGES_COMMAND_LEAVE_SUCCESS("Messages.Command.leave.success"),
    MESSAGES_COMMAND_LEAVE_BROADCAST("Messages.Command.leave.broadcast"),
    MESSAGES_COMMAND_LEAVE_PLAYER_IS_NOT_IN_GAME("Messages.Command.leave.player-is-not-in-game"),

    MESSAGES_GAME_PLAYER_LOSE_BROADCAST("Messages.Game.player-lose-broadcast"),
    MESSAGES_GAME_PLAYER_LOSE_TO_PLAYER("Messages.Game.player-lose-to-player"),
    MESSAGES_GAME_PLAYER_WIN_BROADCAST("Messages.Game.player-win-broadcast"),
    MESSAGES_GAME_PLAYER_WIN_TO_PLAYER("Messages.Game.player-win-to-player"),
    MESSAGES_GAME_NO_ONE_LOST("Messages.Game.no-one-lost"),
    MESSAGES_GAME_NO_ONE_IS_WINNER("Messages.Game.no-one-is-winner"),

    MESSAGES_ERRORS_EVENT_CANNOT_START("Messages.Errors.event-cannot-start");
    final String path;
    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
