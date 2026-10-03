package dev.delesk1jx.miningspeedinfo.client;

import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/**
 * Whether the item the cursor is on is better than the one in the main hand, and which colour that
 * deserves.
 *
 * <p>Quark does the same for its own values, and the mod does it the same way for the plain tooltip
 * lines, so both ways of showing the values always agree with each other.
 */
final class Comparison {

    /** Same speed, or nothing to compare against. */
    static final int SAME = 0;

    /** The hovered item is the faster one. */
    static final int BETTER = 1;

    /** The hovered item is the slower one. */
    static final int WORSE = -1;

    private Comparison() {
    }

    static int speed(MiningSpeed speed, MiningSpeedConfig config) {
        ItemStack held = held(config);
        if (held == null) {
            return SAME;
        }
        MiningSpeed other = MiningSpeedInfo.provider.getMiningSpeed(held, config.showForNonMiningTools);
        if (other == null || other.base() <= 0.0F) {
            return SAME;
        }
        return Float.compare(shown(speed, config), shown(other, config));
    }

    static int harvestLevel(int level, MiningSpeedConfig config) {
        ItemStack held = held(config);
        if (held == null) {
            return SAME;
        }
        int other = MiningSpeedInfo.provider.getHarvestLevel(held);
        return other < 0 ? SAME : Integer.compare(level, other);
    }

    /**
     * The same three colours Quark uses for its own values
     * ({@code AttributeIconEntry.CompareType#getColor}): green while the hovered tool is the better
     * one, red while it is the worse one, and the colour from the settings when both are the same,
     * which is white by default.
     */
    static ChatFormatting color(int comparison, MiningSpeedConfig config) {
        return switch (comparison) {
            case BETTER -> ChatFormatting.GREEN;
            case WORSE -> ChatFormatting.RED;
            default -> config.colorFormatting();
        };
    }

    private static float shown(MiningSpeed speed, MiningSpeedConfig config) {
        return config.includeEfficiency ? speed.total() : speed.base();
    }

    /** The tool in the main hand, or {@code null} when there is nothing to compare against. */
    private static ItemStack held(MiningSpeedConfig config) {
        if (!config.quarkComparison || Minecraft.getInstance().player == null) {
            return null;
        }
        ItemStack held = Minecraft.getInstance().player.getMainHandItem();
        return held.isEmpty() ? null : held;
    }
}
