package dev.delesk1jx.miningspeedinfo.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Keeps the Quark mixin out of the way when Quark is not installed.
 *
 * <p>Without this the game would fail to start, because a mixin whose target class is missing cannot
 * be skipped on its own.
 */
public final class MixinPlugin implements IMixinConfigPlugin {

    /** The class the mixin writes into, which only exists while Quark is installed. */
    private static final String QUARK_ATTRIBUTE_COMPONENT =
            "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("dev.delesk1jx.miningspeedinfo.mixin.Quark")) {
            return isQuarkPresent();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                          IMixinInfo mixinInfo) {
    }

    private static boolean isQuarkPresent() {
        try {
            return Class.forName(QUARK_ATTRIBUTE_COMPONENT, false, MixinPlugin.class.getClassLoader()) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
