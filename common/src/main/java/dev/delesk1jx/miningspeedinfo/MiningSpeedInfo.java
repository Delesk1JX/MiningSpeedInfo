package dev.delesk1jx.miningspeedinfo;

import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * Shared entry point.
 *
 * <p>Only common Minecraft classes may be reachable from here: the mod class of the loader calls
 * {@link #bootstrap(Path, MiningSpeedProvider)} while the mod is being constructed, and that also
 * happens on a dedicated server. Everything that draws or opens a screen lives in the client package
 * and is only loaded on a client.
 */
public final class MiningSpeedInfo {

    public static final String MOD_ID = "miningspeedinfo";
    public static final String MOD_NAME = "Mining Speed Info";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    /** Version specific mining speed lookup, assigned by the platform mod class. */
    public static MiningSpeedProvider provider = MiningSpeedProvider.NONE;

    /** Live configuration, backed by {@code config/miningspeedinfo.json}. */
    public static MiningSpeedConfig config = new MiningSpeedConfig();

    /** Directory the configuration file lives in, assigned by the platform mod class. */
    public static Path configDir;

    private MiningSpeedInfo() {
    }

    /** Called while the mod is loading. */
    public static void bootstrap(Path configDir, MiningSpeedProvider provider) {
        MiningSpeedInfo.configDir = configDir;
        MiningSpeedInfo.provider = provider;
        MiningSpeedInfo.config = MiningSpeedConfig.load(configDir);
    }

    /** Called by the settings screen when the player is done editing. */
    public static void saveConfig() {
        if (configDir != null) {
            config.save(configDir);
        }
    }
}
