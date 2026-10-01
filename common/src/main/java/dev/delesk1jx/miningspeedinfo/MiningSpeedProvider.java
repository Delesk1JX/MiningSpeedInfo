package dev.delesk1jx.miningspeedinfo;

import net.minecraft.world.item.ItemStack;

/**
 * Version specific mining speed lookup. Minecraft moved the tool data from {@code DiggerItem} to the
 * {@code minecraft:tool} data component in 1.21, so the actual implementation cannot be shared.
 */
public interface MiningSpeedProvider {

    MiningSpeedProvider NONE = new MiningSpeedProvider() {
        @Override
        public MiningSpeed getMiningSpeed(ItemStack stack, boolean includeNonMiningTools) {
            return null;
        }

        @Override
        public int getHarvestLevel(ItemStack stack) {
            return UNKNOWN_HARVEST_LEVEL;
        }
    };

    /** Returned when a tool has no tier, for example because it comes from another mod. */
    int UNKNOWN_HARVEST_LEVEL = -1;

    /**
     * @param stack                the item to inspect
     * @param includeNonMiningTools also report items that are not digging tools, such as swords and shears
     * @return the mining speed, or {@code null} when the item is not a digging tool
     */
    MiningSpeed getMiningSpeed(ItemStack stack, boolean includeNonMiningTools);

    /**
     * How good a tool is: 0 mines stone, 1 iron ore, 2 diamonds and so on, which tells the player
     * right away whether a new pickaxe can break blocks an old one could not.
     *
     * @return the level, or {@link #UNKNOWN_HARVEST_LEVEL} when the tool has no known tier
     */
    int getHarvestLevel(ItemStack stack);
}
