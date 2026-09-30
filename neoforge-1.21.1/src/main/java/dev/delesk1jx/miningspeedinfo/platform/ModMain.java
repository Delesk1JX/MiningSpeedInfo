package dev.delesk1jx.miningspeedinfo.platform;

import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Minecraft 1.21.1 / NeoForge. Only the loading glue is version specific, the mod itself lives in the
 * shared source set.
 *
 * <p>The mod is declared client side, so a dedicated server normally never reaches this class. The
 * check below keeps a development server happy as well, because it loads every mod of the project.
 */
@Mod(MiningSpeedInfo.MOD_ID)
public final class ModMain {

    public ModMain(IEventBus modBus, ModContainer container) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        MiningSpeedInfo.bootstrap(FMLPaths.CONFIGDIR.get(), new VanillaMiningSpeedProvider());
        ClientEvents.register(modBus, NeoForge.EVENT_BUS);
    }
}
