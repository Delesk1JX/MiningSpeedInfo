package dev.delesk1jx.miningspeedinfo.client;

import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
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
    private static final String CLIENT_CLASS = "org.violetmoon.quark.base.QuarkClient";
    private static final String TICK_HOLDER_CLASS = "org.violetmoon.quark.catnip.animation.AnimationTickHolder";

    /** Quark moves its arrows one pixel down for half of every 20 ticks. */
    private static final float CYCLE = 20.0F;
    private static final float CYCLE_LOW = 10.0F;

    private static boolean lookedUp;
    private static Class<?> moduleClass;
    private static Class<?> panelClass;
    private static Method stackAccessor;

    /** The clock Quark's arrows run on, which changed shape between Quark 4.0 and 4.1. */
    private static Method animationTicks;
    private static Method timer;
    private static Method gameTimeDelta;
    private static Field legacyTotal;

    /** Fallback for when Quark cannot be asked: counts the ticks ourselves. */
    private static int ownTicks;

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

    /** Turns one pixel down for half of every 20 ticks, exactly the way Quark moves its arrows. */
    public static int arrowOffset() {
        if (!flag("animateUpDownArrows", true)) {
            return 0;
        }
        return animationTime() % CYCLE < CYCLE_LOW ? 1 : 0;
    }

    /** Counts the frames for the case where Quark cannot be asked. */
    public static void tick() {
        ownTicks++;
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

    /**
     * The clock Quark uses for its arrows, so that both arrows sit on the same pixel instead of slowly
     * drifting apart. Quark 4.1 counts whole client ticks and adds the fraction the timer reports,
     * Quark 4.0 keeps both in a single field of its own.
     */
    private static float animationTime() {
        if (animationTicks != null) {
            try {
                return (int) animationTicks.invoke(null)
                        + (float) gameTimeDelta.invoke(timer.invoke(Minecraft.getInstance()));
            } catch (Throwable ignored) {
                animationTicks = null;
            }
        }
        if (legacyTotal != null) {
            try {
                return legacyTotal.getFloat(null);
            } catch (Throwable ignored) {
                legacyTotal = null;
            }
        }
        return ownTicks;
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
            lookUpClock();
            MiningSpeedInfo.LOGGER.info("Quark found, its attribute panel will carry the mining speed");
        } catch (Throwable ignored) {
            // Quark is not installed, or its panel looks different than expected. Either way the mod
            // falls back to the plain tooltip lines.
            moduleClass = null;
            panelClass = null;
        }
    }

    private static void lookUpClock() {
        try {
            Class<?> holder = Class.forName(TICK_HOLDER_CLASS);
            animationTicks = holder.getMethod("getTicks");
            timer = Minecraft.class.getMethod("getTimer");
            gameTimeDelta = timer.getReturnType().getMethod("getGameTimeDeltaTicks");
            return;
        } catch (Throwable ignored) {
            animationTicks = null;
        }
        try {
            legacyTotal = Class.forName(CLIENT_CLASS).getField("ticker").getType().getField("total");
        } catch (Throwable ignored) {
            legacyTotal = null;
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