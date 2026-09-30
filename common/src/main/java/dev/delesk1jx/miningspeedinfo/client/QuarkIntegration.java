package dev.delesk1jx.miningspeedinfo.client;

import com.mojang.datafixers.util.Either;
import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.config.MiningSpeedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Puts the value into Quark's attribute tooltip.
 *
 * <p>Quark is only ever touched by name: its module class is read through reflection to find out
 * whether its attribute tooltips are switched on, and its tooltip entry is recognised by class name.
 * That keeps Quark an optional dependency that is never needed at compile time, and means a Quark
 * update can never stop this mod from loading.
 */
public final class QuarkIntegration {

    private static final String MODULE_CLASS = "org.violetmoon.quark.content.client.module.ImprovedTooltipsModule";
    private static final String ATTRIBUTE_COMPONENT_CLASS =
            "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent";

    private static boolean lookedUpModule;
    private static Class<?> module;

    private QuarkIntegration() {
    }

    /** True when Quark is installed and its attribute tooltips are switched on. */
    public static boolean isActive() {
        return module() != null && flag("attributeTooltips", true);
    }

    /** Whether Quark is installed at all. */
    public static boolean isPresent() {
        return module() != null;
    }

    /**
     * Quark can be told to leave the comparison arrows alone and the player may well have done that
     * on purpose, so the setting is honoured instead of being hard coded.
     */
    public static boolean showComparison() {
        return flag("showUpgradeStatus", true);
    }

    private static Class<?> module() {
        if (!lookedUpModule) {
            lookedUpModule = true;
            try {
                module = Class.forName(MODULE_CLASS);
            } catch (Throwable ignored) {
                // Quark is not installed, which is perfectly fine.
            }
        }
        return module;
    }

    private static boolean flag(String name, boolean fallback) {
        Class<?> type = module();
        if (type == null) {
            return fallback;
        }
        try {
            return type.getField(name).getBoolean(null);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    /**
     * Called while the tooltip elements of the hovered item are collected. The value is placed right
     * behind Quark's own attribute panel so that both read as one block.
     */
    public static void onGatherTooltipComponents(ItemStack stack,
                                                 List<Either<FormattedText, TooltipComponent>> elements) {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        if (!config.enabled || !config.quarkTooltip || !isActive()) {
            return;
        }
        // Quark hides its whole panel while sneaking.
        if (Screen.hasShiftDown()) {
            return;
        }

        MiningSpeed speed = MiningSpeedInfo.provider.getMiningSpeed(stack, config.showForNonMiningTools);
        if (speed == null || speed.base() <= 0.0F) {
            return;
        }

        elements.add(insertIndex(elements), Either.right(new MiningSpeedTooltipComponent(
                speed,
                compare(speed, config),
                config.colorFormatting(),
                config.decimals,
                config.quarkComparison && showComparison())));
    }

    /** @return {@code 1} when the hovered tool is faster, {@code -1} when it is slower, {@code 0} otherwise */
    private static int compare(MiningSpeed speed, MiningSpeedConfig config) {
        Float equipped = equippedSpeed(config);
        if (equipped == null) {
            return 0;
        }
        return Float.compare(config.includeEfficiency ? speed.total() : speed.base(), equipped);
    }

    private static Float equippedSpeed(MiningSpeedConfig config) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return null;
        }
        ItemStack held = minecraft.player.getMainHandItem();
        if (held.isEmpty()) {
            return null;
        }
        MiningSpeed speed = MiningSpeedInfo.provider.getMiningSpeed(held, config.showForNonMiningTools);
        if (speed == null || speed.base() <= 0.0F) {
            return null;
        }
        return config.includeEfficiency ? speed.total() : speed.base();
    }

    private static int insertIndex(List<Either<FormattedText, TooltipComponent>> elements) {
        for (int i = 0; i < elements.size(); i++) {
            TooltipComponent component = elements.get(i).right().orElse(null);
            if (component != null && ATTRIBUTE_COMPONENT_CLASS.equals(component.getClass().getName())) {
                return i + 1;
            }
        }
        return elements.size();
    }
}
