package dev.delesk1jx.miningspeedinfo.platform;

import com.mojang.blaze3d.platform.InputConstants;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.client.TooltipHandler;
import dev.delesk1jx.miningspeedinfo.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Client side event handlers. This class is only ever loaded on a client, so it may use client classes
 * as freely as it likes.
 */
public final class ClientEvents {

    private static KeyMapping openConfigKey;

    private ClientEvents() {
    }

    /** Called from the mod constructor. */
    public static void register(IEventBus modBus, IEventBus gameBus) {
        modBus.addListener(ClientEvents::onRegisterKeyMappings);
        gameBus.addListener(ClientEvents::onClientTick);
        gameBus.addListener(ClientEvents::onItemTooltip);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        openConfigKey = new KeyMapping(
                "key." + MiningSpeedInfo.MOD_ID + ".open_config",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories." + MiningSpeedInfo.MOD_ID);
        event.register(openConfigKey);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        while (openConfigKey != null && openConfigKey.consumeClick()) {
            Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));
        }
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        TooltipHandler.onItemTooltip(event.getToolTip(), event.getItemStack());
    }
}
