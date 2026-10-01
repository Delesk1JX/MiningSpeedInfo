package dev.delesk1jx.miningspeedinfo.platform;

import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedProvider;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mining speed of the 1.20.1 tool classes.
 *
 * <p>Vanilla keeps the base speed in the tier of the tool and adds the Efficiency bonus separately
 * while a block is being broken, so both parts are calculated here the same way. Tools of other mods
 * that do not use a tier are asked directly.
 */
public final class VanillaMiningSpeedProvider implements MiningSpeedProvider {

    /**
     * Used for tools that are not built on a tier. Vanilla only accepts a block the tool can dig, and
     * stone is the one block that belongs to every mining tag.
     */
    private static final BlockState REFERENCE = Blocks.STONE.defaultBlockState();

    @Override
    public MiningSpeed getMiningSpeed(ItemStack stack, boolean includeNonMiningTools) {
        Item item = stack.getItem();
        float base;
        if (item instanceof DiggerItem) {
            // Every vanilla digging tool is a tiered item, and a sword is a tiered item too, which is
            // why the digging check has to come first.
            base = item instanceof TieredItem tiered ? tiered.getTier().getSpeed() : stack.getDestroySpeed(REFERENCE);
        } else if (includeNonMiningTools && (item instanceof ShearsItem || item instanceof SwordItem)) {
            base = 1.0F;
        } else {
            return null;
        }

        if (base <= 0.0F) {
            return null;
        }

        float bonus = 0.0F;
        if (base > 1.0F) {
            int efficiency = stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
            if (efficiency > 0) {
                bonus = efficiency * efficiency + 1;
            }
        }
        return new MiningSpeed(base, bonus);
    }

    @Override
    public int getHarvestLevel(ItemStack stack) {
        // 1.20.1 still keeps the number on the tier itself, so it can be read directly. That also
        // covers tools of other mods as long as they build on a tier.
        if (stack.getItem() instanceof TieredItem tiered) {
            return tiered.getTier().getLevel();
        }
        return UNKNOWN_HARVEST_LEVEL;
    }
}
