package dev.delesk1jx.miningspeedinfo.client;

import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import dev.delesk1jx.miningspeedinfo.text.SpeedFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Builds the tooltip line and decides where the value belongs: as a plain line, or inside Quark's
 * attribute tooltip.
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

        // Quark is drawing the value inside its own attribute panel, so a second plain line would
        // just repeat it. Sneaking still shows the plain line, same as with vanilla tooltips.
        if (config.quarkTooltip && !shift && QuarkIntegration.isActive()) {
            return;
        }

        lines.add(buildPlainLine(speed));
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
    public static Component buildPlainLine(MiningSpeed speed) {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        Component value = buildValue(speed, config.colorFormatting());
        return Component.literal(" ")
                .append(Component.translatable("miningspeedinfo.tooltip.mining_speed", value))
                .withStyle(config.colorFormatting());
    }

    /** Just the name of the stat, used by the row that is drawn inside Quark's tooltip. */
    public static MutableComponent miningSpeedName() {
        return Component.translatable("miningspeedinfo.tooltip.mining_speed_name");
    }

    /** "6", "6 (+26)", "6.5", ... depending on the settings. */
    public static MutableComponent buildValue(MiningSpeed speed, ChatFormatting color) {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        float shown = config.includeEfficiency ? speed.total() : speed.base();

        MutableComponent value = Component.literal(SpeedFormatter.format(shown, config.decimals));
        if (config.includeEfficiency && config.showEfficiencyBreakdown && speed.hasBonus()) {
            value.append(Component.literal(" "))
                    .append(Component.translatable("miningspeedinfo.tooltip.bonus",
                            SpeedFormatter.format(speed.bonus(), config.decimals)));
        }
        return value.withStyle(color);
    }
}
