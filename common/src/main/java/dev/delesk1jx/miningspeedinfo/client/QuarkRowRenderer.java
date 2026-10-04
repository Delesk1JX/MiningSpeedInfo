package dev.delesk1jx.miningspeedinfo.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import dev.delesk1jx.miningspeedinfo.text.SpeedFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The values of this mod as they are drawn inside Quark's attribute tooltip, right behind the values
 * Quark shows itself: an icon and the number next to it, on the same row.
 *
 * <p>The mixin only calls {@link #width} and {@link #renderBehindPanel} and passes the position Quark
 * has already used up. That keeps every Minecraft call inside this class, where the build can map it
 * for the runtime, instead of inside the mixin.
 *
 * <p>All images are ordinary resource pack textures, so they can be recoloured or replaced without
 * touching the mod.
 */
public final class QuarkRowRenderer {

    /** The height of one of Quark's rows. */
    public static final int ROW_HEIGHT = 10;

    /** Quark draws its values one pixel above the y of the row, which is where the text sits. */
    public static final int ROW_OFFSET = 1;

    /** The gap Quark leaves after a value before the next icon. */
    public static final int GAP = 8;

    /** Matches the images Quark draws next to its own values. */
    private static final int IMAGE = 9;
    private static final int ARROW = 13;

    /** Where Quark puts the text of a value, counted from the x of its icon. */
    private static final int TEXT_OFFSET = 12;


    private static final ResourceLocation SPEED_ICON =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/mining_speed.png");
    private static final ResourceLocation LEVEL_ICON =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/harvest_level.png");
    private static final ResourceLocation UPGRADE =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/upgrade.png");
    private static final ResourceLocation DOWNGRADE =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/downgrade.png");

    /**
     * Whether the mixin ever managed to draw. It only decides if the plain tooltip lines are still
     * needed, so a Quark update that moves the code around costs the values inside Quark's tooltip
     * instead of the game.
     */
    private static boolean active;

    /** Counts the ticks that passed without a single value being drawn. */
    private static int ticks = -1;

    /** Whether the arrow clock was already written to the log. */
    private static boolean loggedClock;

    private QuarkRowRenderer() {
    }

    /** One icon with one number, and whether the held tool is better or worse. */
    private record Value(ResourceLocation icon, MutableComponent text, ChatFormatting color, int comparison) {
    }

    public static boolean isActive() {
        return active;
    }

    /**
     * Watches from the client tick whether the values ever reach Quark's tooltip, so that a mixin that
     * quietly does nothing is said out loud instead of leaving the player with plain lines and no idea
     * why.
     */
    public static void watchdog() {
        QuarkIntegration.tick();

        if (active || !QuarkIntegration.isActive()) {
            return;
        }
        if (ticks < 0) {
            ticks = 0;
            return;
        }
        if (++ticks == 200) {
            MiningSpeedInfo.LOGGER.warn("Quark draws its attribute panel, but the mining speed never reached "
                    + "it. Something in Quark changed, so only the plain tooltip lines are shown. "
                    + "The latest log is in .minecraft/logs/latest.log.");
        }
    }

    /** How much room the values need, so that Quark can make the tooltip wide enough. */
    public static int width(ItemStack stack, Font font) {
        int width = 0;
        for (Value value : values(stack)) {
            width += advance(font, value);
        }
        return Math.max(0, width - GAP);
    }

    /**
     * Draws the values directly behind the ones Quark has already drawn.
     *
     * <p>Quark keeps the running x in a local variable while it renders, which cannot be read from
     * outside without hooking into the code. It does publish the width of the panel instead, and the
     * position where its last value ended is exactly the tooltip x plus that width plus the gap Quark
     * would leave, so the values can be placed without touching any of Quark's variables.
     *
     * @param panelWidth the width Quark's own panel reports, without the values of this mod
     * @param tooltipX    the x Quark renders its panel at
     * @param tooltipY    the y Quark renders its panel at
     */
    public static void renderBehindPanel(ItemStack stack, Font font, int panelWidth, int tooltipX, int tooltipY,
                                          GuiGraphics graphics) {
        // Quark draws nothing at all while sneak is held, and neither should the values then. That is
        // also where the mod switches to the plain tooltip lines with the full names.
        if (Screen.hasShiftDown()) {
            return;
        }

        List<Value> values = values(stack);
        if (values.isEmpty()) {
            return;
        }
        if (!active) {
            MiningSpeedInfo.LOGGER.info("Mining speed values are now drawn inside Quark's attribute panel");
            ticks = -1;
        }
        active = true;

        int x = tooltipX + panelWidth + GAP;
        int y = tooltipY - ROW_OFFSET;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, 500.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Value value : values) {
            graphics.blit(value.icon(), x, y, 0.0F, 0.0F, IMAGE, IMAGE, IMAGE, IMAGE);

            if (value.comparison() != 0) {
                // Quark draws its arrow at the icon position moved two pixels up and left, and dips it
                // one pixel for half of every 20 ticks. Asking it for that offset is what keeps the two
                // arrows on the same pixel.
                int offset = QuarkIntegration.arrowOffset();
                if (!loggedClock) {
                    loggedClock = true;
                    MiningSpeedInfo.LOGGER.info("The arrows run on {}", QuarkIntegration.clockSource());
                }
                graphics.blit(value.comparison() > 0 ? UPGRADE : DOWNGRADE,
                        x - 2, y - 2 + offset, 0.0F, 0.0F, ARROW, ARROW, ARROW, ARROW);
            }

            graphics.drawString(font, value.text(), x + TEXT_OFFSET, y + 1, -1);
            x += advance(font, value);
        }

        pose.popPose();
    }

    /** The values to show for the item the cursor is on, empty when there is nothing to say. */
    private static List<Value> values(ItemStack stack) {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        List<Value> values = new ArrayList<>(2);

        MiningSpeed speed = MiningSpeedInfo.provider.getMiningSpeed(stack, config.showForNonMiningTools);
        if (speed != null && speed.base() > 0.0F) {
            int comparison = Comparison.speed(speed, config);
            ChatFormatting color = Comparison.color(comparison, config);
            values.add(new Value(SPEED_ICON, speedText(speed, config, color), color, comparison));
        }

        int level = MiningSpeedInfo.provider.getHarvestLevel(stack);
        if (config.showHarvestLevel && level >= 0) {
            int comparison = Comparison.harvestLevel(level, config);
            ChatFormatting color = Comparison.color(comparison, config);
            values.add(new Value(LEVEL_ICON,
                    Component.literal(SpeedFormatter.format(level, 0)).withStyle(color), color, comparison));
        }
        return values;
    }

    private static MutableComponent speedText(MiningSpeed speed, MiningSpeedConfig config, ChatFormatting color) {
        float shown = config.includeEfficiency ? speed.total() : speed.base();
        MutableComponent text = Component.literal(SpeedFormatter.format(shown, config.decimals));
        if (config.includeEfficiency && config.showEfficiencyBreakdown && speed.hasBonus()) {
            text = text.append(Component.literal(" "))
                    .append(Component.translatable("miningspeedinfo.tooltip.bonus",
                            SpeedFormatter.format(speed.bonus(), config.decimals)));
        }
        return text.withStyle(color);
    }

    private static int advance(Font font, Value value) {
        return TEXT_OFFSET + font.width(value.text()) + GAP;
    }
}
