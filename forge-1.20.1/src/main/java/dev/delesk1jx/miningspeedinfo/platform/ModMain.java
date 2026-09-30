package dev.delesk1jx.miningspeedinfo.platform;

import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Minecraft 1.20.1 / Forge. Only the loading glue is version specific, the mod itself lives in the
 * shared source set.
 *
 * <p>The mod is declared client side, so a dedicated server normally never reaches this class. The
 * check below keeps a development server happy as well, because it loads every mod of the project.
 */
@Mod(MiningSpeedInfo.MOD_ID)
public final class ModMain {

    public ModMain() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        MiningSpeedInfo.bootstrap(FMLPaths.CONFIGDIR.get(), new VanillaMiningSpeedProvider());
    }
}
