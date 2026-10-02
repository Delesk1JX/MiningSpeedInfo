package dev.delesk1jx.miningspeedinfo.platform;

import com.mojang.blaze3d.platform.InputConstants;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.client.QuarkIntegration;
import dev.delesk1jx.miningspeedinfo.client.QuarkRowRenderer;
import dev.delesk1jx.miningspeedinfo.client.TooltipHandler;
import dev.delesk1jx.miningspeedinfo.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Client side event handlers. The listeners below are registered on the client only, which is why the
 * mod never does anything on a dedicated server.
 */
public final class ClientEvents {

    private static KeyMapping openConfigKey;

    private ClientEvents() {
    }

    @Mod.EventBusSubscriber(modid = MiningSpeedInfo.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            openConfigKey = new KeyMapping(
                    "key." + MiningSpeedInfo.MOD_ID + ".open_config",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + MiningSpeedInfo.MOD_ID);
            event.register(openConfigKey);
        }

        /**
         * Puts the values behind the ones Quark draws, by handing the loader a wrapper for Quark's
         * attribute panel. This runs last on purpose, so it wins over the registration Quark does.
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            Class<?> panel = QuarkIntegration.panelClass();
            if (panel != null) {
                register(event, panel.asSubclass(TooltipComponent.class));
            }
        }

        private static <T extends TooltipComponent> void register(RegisterClientTooltipComponentFactoriesEvent event,
                                                                 Class<T> panel) {
            event.register(panel, QuarkIntegration::wrapPanel);
        }
    }

    @Mod.EventBusSubscriber(modid = MiningSpeedInfo.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeBus {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                QuarkRowRenderer.watchdog();
                while (openConfigKey != null && openConfigKey.consumeClick()) {
                    Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));
                }
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            TooltipHandler.onItemTooltip(event.getToolTip(), event.getItemStack());
        }
    }
}
