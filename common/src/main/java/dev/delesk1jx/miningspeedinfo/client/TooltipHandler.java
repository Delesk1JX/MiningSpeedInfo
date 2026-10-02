package dev.delesk1jx.miningspeedinfo.client;

import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import dev.delesk1jx.miningspeedinfo.text.SpeedFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Builds the plain tooltip lines and decides where they belong: as lines of their own, or inside
 * Quark's attribute tooltip.
 */
public final class TooltipHandler {

    private TooltipHandler() {
    }

    /** Called from the loader's item tooltip event. */
    public static void onItemTooltip(List<Component> lines, ItemStack stack) {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        if (!config.enabled) {
            return;
        }
        boolean shift = Screen.hasShiftDown();
        if (config.requireShift && !shift) {
            return;
        }

        MiningSpeed speed = MiningSpeedInfo.provider.getMiningSpeed(stack, config.showForNonMiningTools);
        if (speed == null || speed.base() <= 0.0F) {
            return;
        }

        // Quark draws the values inside its own attribute panel, so a second line would only repeat it.
        // Sneaking still shows the plain line, same as with the rest of the tooltip.
        if (config.quarkTooltip && !shift && QuarkIntegration.drawsValue()) {
            return;
        }

        ChatFormatting color = Comparison.color(Comparison.speed(speed, config), config);
        add(lines, stack, buildSpeedLine(speed, config, color));

        int level = config.showHarvestLevel ? MiningSpeedInfo.provider.getHarvestLevel(stack) : -1;
        if (level >= 0) {
            add(lines, stack, buildLevelLine(level, config,
                    Comparison.color(Comparison.harvestLevel(level, config), config)));
        }
    }

    /**
     * The plain tooltip line.
     *
     * <p>It starts with a space on purpose: Quark draws the values of its own attribute panel a bit
     * further to the right than a normal tooltip line, so without the space the line would sit against
     * the edge of the tooltip while the values above it look indented. The space is a literal and not
     * part of the translation, because a translation gets its arguments glued into the text and a
     * leading space there would be lost.
     */
    public static Component buildSpeedLine(MiningSpeed speed, MiningSpeedConfig config, ChatFormatting color) {
        return line("miningspeedinfo.tooltip.mining_speed", buildValue(speed, config, color), config, color);
    }

    /** Same indent, saying which blocks the tool can break. */
    public static Component buildLevelLine(int level, MiningSpeedConfig config, ChatFormatting color) {
        return line("miningspeedinfo.tooltip.harvest_level", SpeedFormatter.format(level, 0), config, color);
    }

    /** "6", "6 (+26)", "6.5", ... depending on the settings. */
    public static MutableComponent buildValue(MiningSpeed speed, MiningSpeedConfig config, ChatFormatting color) {
        float shown = config.includeEfficiency ? speed.total() : speed.base();

        MutableComponent value = Component.literal(SpeedFormatter.format(shown, config.decimals));
        if (config.includeEfficiency && config.showEfficiencyBreakdown && speed.hasBonus()) {
            value.append(Component.literal(" "))
                    .append(Component.translatable("miningspeedinfo.tooltip.bonus",
                            SpeedFormatter.format(speed.bonus(), config.decimals)));
        }
        return value.withStyle(color);
    }

    private static Component line(String key, Object argument, MiningSpeedConfig config, ChatFormatting color) {
        return Component.literal(" ")
                .append(Component.translatable(key, argument))
                .withStyle(color);
    }

    /**
     * Puts a line in front of the item id and the NBT count that the game adds with the advanced
     * tooltips, so the values stay next to the other attributes instead of ending up at the very
     * bottom of a debug tooltip.
     */
    private static void add(List<Component> lines, ItemStack stack, Component line) {
        lines.add(Math.max(0, debugLinesStart(lines, stack)), line);
    }

    private static int debugLinesStart(List<Component> lines, ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (id.equals(lines.get(i).getString())) {
                return i;
            }
        }
        return lines.size();
    }
}
