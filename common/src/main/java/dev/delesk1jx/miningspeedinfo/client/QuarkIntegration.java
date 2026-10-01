package dev.delesk1jx.miningspeedinfo.client;

/**
 * Everything the mod needs to know about Quark, read by name only.
 *
 * <p>Quark is never a compile time dependency: the module class is looked up through reflection to
 * find out whether its attribute tooltips are switched on, and the mixin skips itself when the class
 * is not there. A Quark update that renames something therefore costs the value inside Quark's
 * tooltip instead of the game.
 */
public final class QuarkIntegration {

    private static final String MODULE_CLASS = "org.violetmoon.quark.content.client.module.ImprovedTooltipsModule";

    private static boolean lookedUpModule;
    private static Class<?> module;

    private QuarkIntegration() {
    }

    /** Whether Quark is installed at all. */
    public static boolean isPresent() {
        return module() != null;
    }

    /** Whether Quark draws its attribute panel for tools, which is where our value goes. */
    public static boolean isActive() {
        return module() != null && flag("attributeTooltips", true);
    }

    /**
     * Whether our value really ends up inside Quark's panel. Quark has to be active and the mixin has
     * to have drawn at least once, which also covers the rare case of Quark moving its code around:
     * the plain tooltip line is then used instead.
     */
    public static boolean drawsValue() {
        return isActive() && QuarkRowRenderer.isActive();
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
}
