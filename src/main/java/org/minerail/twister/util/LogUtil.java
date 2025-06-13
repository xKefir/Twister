package org.minerail.twister.util;

import com.sun.security.auth.login.ConfigFile;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigKey;

import java.util.logging.Logger;

public class LogUtil {
    private static final Logger LOGGER = Twister.get().getLogger();
    private static boolean debug = Twister.getConfigFile().getBoolean(ConfigKey.SETTINGS_PLUGIN_DEBUG);

    public static void setDebug(boolean state) {
        debug = state;
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warning(msg);
    }

    public static void error(String msg) {
        LOGGER.severe(msg);
    }

    public static void debug(String msg) {
        if (debug) LOGGER.info("[DEBUG] " + msg);
    }

    private LogUtil() {}
}
