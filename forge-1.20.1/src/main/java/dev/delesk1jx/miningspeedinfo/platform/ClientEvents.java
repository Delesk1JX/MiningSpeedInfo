package dev.delesk1jx.miningspeedinfo.platform;

import com.mojang.blaze3d.platform.InputConstants;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import dev.delesk1jx.miningspeedinfo.client.MiningSpeedTooltipComponent;
import dev.delesk1jx.miningspeedinfo.client.QuarkIntegration;
import dev.delesk1jx.miningspeedinfo.client.TooltipHandler;
import dev.delesk1jx.miningspeedinfo.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
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
         * The row that is added to Quark's tooltip is a real tooltip component, and Forge refuses to
         * draw one that has no factory, so the component has to be announced here. Without it the game
         * throws "Unknown TooltipComponent" as soon as a tool is hovered while Quark is installed.
         */
        @SubscribeEvent
        public static void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(MiningSpeedTooltipComponent.class, component -> component);
        }
    }

    @Mod.EventBusSubscriber(modid = MiningSpeedInfo.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeBus {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                while (openConfigKey != null && openConfigKey.consumeClick()) {
                    Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));
                }
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            TooltipHandler.onItemTooltip(event.getToolTip(), event.getItemStack());
        }

        /**
         * Runs last so Quark has already added its own attribute panel to the list, which is what
         * lets the value be placed directly behind it.
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
            QuarkIntegration.onGatherTooltipComponents(event.getItemStack(), event.getTooltipElements());
        }
    }
}
