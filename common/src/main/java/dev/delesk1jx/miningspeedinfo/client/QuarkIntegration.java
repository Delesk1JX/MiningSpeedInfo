package dev.delesk1jx.miningspeedinfo.client;

import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

/**
 * Everything the mod needs to know about Quark, read by name only.
 *
 * <p>Quark is never a compile time dependency: the class of its attribute panel is looked up through
 * reflection, and the mod does nothing at all when Quark is missing. Nothing of Quark's code is
 * touched, so a Quark update can at worst leave the values out of its panel, never break the game.
 */
public final class QuarkIntegration {

    private static final String MODULE_CLASS = "org.violetmoon.quark.content.client.module.ImprovedTooltipsModule";
    private static final String PANEL_CLASS =
            "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent";

    private static boolean lookedUp;
    private static Class<?> moduleClass;
    private static Class<?> panelClass;
    private static Method stackAccessor;

    private QuarkIntegration() {
    }

    /** Whether Quark draws its attribute panel for tools, which is where our values go. */
    public static boolean isActive() {
        lookUp();
        return moduleClass != null && flag("attributeTooltips", true);
    }

    /**
     * Whether our value really ends up inside Quark's panel. Quark has to be active and the panel has
     * to have been wrapped at least once, which also covers the rare case of Quark changing the class
     * the loader asks for: the plain tooltip line is then used instead.
     */
    public static boolean drawsValue() {
        return isActive() && QuarkRowRenderer.isActive();
    }

    /**
     * The class of Quark's attribute panel, or {@code null} when Quark is missing. It is handed back
     * untyped on purpose: naming Quark's own class in a signature would make the game fail to start
     * when Quark is not installed.
     */
    public static Class<?> panelClass() {
        lookUp();
        return panelClass;
    }

    /** Turns one of Quark's panels into one that carries the mining speed as well. */
    public static <T extends TooltipComponent> ClientTooltipComponent wrapPanel(T component) {
        lookUp();
        if (!(component instanceof ClientTooltipComponent panel)) {
            return (ClientTooltipComponent) component;
        }
        ItemStack stack = stackOf(component);
        if (stack == null || QuarkRowRenderer.width(stack, Minecraft.getInstance().font) <= 0) {
            return panel;
        }
        return new QuarkPanelWrapper(panel, stack);
    }

    private static ItemStack stackOf(Object component) {
        try {
            return (ItemStack) stackAccessor.invoke(component);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void lookUp() {
        if (lookedUp) {
            return;
        }
        lookedUp = true;
        try {
            moduleClass = Class.forName(MODULE_CLASS);
            panelClass = Class.forName(PANEL_CLASS);
            stackAccessor = panelClass.getMethod("stack");
            MiningSpeedInfo.LOGGER.info("Quark found, its attribute panel will carry the mining speed");
        } catch (Throwable ignored) {
            // Quark is not installed, or its panel looks different than expected. Either way the mod
            // falls back to the plain tooltip lines.
            moduleClass = null;
            panelClass = null;
        }
    }

    private static boolean flag(String name, boolean fallback) {
        if (moduleClass == null) {
            return fallback;
        }
        try {
            return moduleClass.getField(name).getBoolean(null);
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}
