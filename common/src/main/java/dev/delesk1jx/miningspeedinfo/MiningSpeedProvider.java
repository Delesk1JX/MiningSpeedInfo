package dev.delesk1jx.miningspeedinfo;

import net.minecraft.world.item.ItemStack;

/**
 * Version specific mining speed lookup. Minecraft moved the tool data from {@code DiggerItem} to the
 * {@code minecraft:tool} data component in 1.21, so the actual implementation cannot be shared.
 */
public interface MiningSpeedProvider {

    /** Used until the platform mod class installed the real implementation. */
    MiningSpeedProvider NONE = (stack, includeNonMiningTools) -> null;

    /**
     * @param stack                the item to inspect
     * @param includeNonMiningTools also report items that are not digging tools, such as swords and shears
     * @return the mining speed, or {@code null} when the item is not a digging tool
     */
    MiningSpeed getMiningSpeed(ItemStack stack, boolean includeNonMiningTools);
}
