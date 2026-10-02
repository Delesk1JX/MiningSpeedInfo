package dev.delesk1jx.miningspeedinfo.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;

/**
 * Announces the mixin config at runtime.
 *
 * <p>The metadata of a Forge mod can list a mixin config, but the loaders do not read it from there, so
 * the config has to be handed to Mixin directly. Doing it here also means the config is registered
 * before anything can load the class it writes into, which is the only class that matters.
 *
 * <p>Mixin keeps one entry per config, so this does nothing when something else, for example a
 * development run, already registered it.
 */
public final class MixinRegistration {

    private MixinRegistration() {
    }

    public static void register(String configName) {
        MixinEnvironment.getDefaultEnvironment().addConfiguration(configName);
    }
}
