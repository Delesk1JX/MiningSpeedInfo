package dev.delesk1jx.miningspeedinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraft.ChatFormatting;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Everything the player can change. Stored as plain JSON in {@code config/miningspeedinfo.json} so it
 * can also be edited by hand.
 */
public final class MiningSpeedConfig {

    /** Every colour a Minecraft chat colour supports, in the order the settings screen cycles them. */
    public static final List<ChatFormatting> COLORS = List.of(
            ChatFormatting.DARK_GREEN,
            ChatFormatting.GREEN,
            ChatFormatting.AQUA,
            ChatFormatting.DARK_AQUA,
            ChatFormatting.BLUE,
            ChatFormatting.DARK_BLUE,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.RED,
            ChatFormatting.DARK_RED,
            ChatFormatting.YELLOW,
            ChatFormatting.GOLD,
            ChatFormatting.WHITE,
            ChatFormatting.GRAY,
            ChatFormatting.DARK_GRAY,
            ChatFormatting.BLACK
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "miningspeedinfo.json";

    /** Master switch. */
    public boolean enabled = true;

    /** Chat colour of the added tooltip line. */
    public String color = "dark_green";

    /** Number of decimals, {@code -1} keeps whole numbers whole and trims the rest. */
    public int decimals = -1;

    /** Add the bonus granted by the Efficiency enchantment. */
    public boolean includeEfficiency = true;

    /** Show the Efficiency bonus as a separate "(+N)" part. */
    public boolean showEfficiencyBreakdown = false;

    /** Only add the tooltip line while the player holds sneak. */
    public boolean requireShift = false;

    /** Also report items that are not digging tools, such as swords and shears. */
    public boolean showForNonMiningTools = false;

    /** Add the value to Quark's attribute tooltip instead of adding a plain line. */
    public boolean quarkTooltip = true;

    /** Mark the value in Quark's tooltip as faster or slower than the tool in the main hand. */
    public boolean quarkComparison = true;

    public static Path file(Path configDir) {
        return configDir.resolve(FILE_NAME);
    }

    /**
     * Reads the settings and writes them the first time the mod is started. A file that cannot be read
     * is left exactly as it is and the defaults are used instead, so hand made settings are never
     * thrown away without a word.
     */
    public static MiningSpeedConfig load(Path configDir) {
        Path file = file(configDir);
        if (!Files.isRegularFile(file)) {
            MiningSpeedConfig config = new MiningSpeedConfig();
            config.save(configDir);
            return config;
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            MiningSpeedConfig parsed = GSON.fromJson(reader, MiningSpeedConfig.class);
            if (parsed != null) {
                parsed.validate();
                return parsed;
            }
            MiningSpeedInfo.LOGGER.warn("{} is empty, using the default settings", file);
        } catch (IOException | JsonSyntaxException e) {
            MiningSpeedInfo.LOGGER.warn("Could not read {}, using the default settings", file, e);
        }
        return new MiningSpeedConfig();
    }

    public void save(Path configDir) {
        Path file = file(configDir);
        try {
            Files.createDirectories(configDir);
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            MiningSpeedInfo.LOGGER.error("Could not write {}", file, e);
        }
    }

    /** Repairs values that were hand edited into something the mod cannot use. */
    public void validate() {
        if (this.color == null) {
            this.color = "dark_green";
        }
        if (colorFormatting() == null) {
            this.color = "dark_green";
        }
        this.decimals = Math.max(-1, Math.min(2, this.decimals));
        if (!this.includeEfficiency) {
            this.showEfficiencyBreakdown = false;
        }
    }

    public ChatFormatting colorFormatting() {
        for (ChatFormatting formatting : COLORS) {
            if (formatting.getName().equalsIgnoreCase(this.color)) {
                return formatting;
            }
        }
        return null;
    }
}
