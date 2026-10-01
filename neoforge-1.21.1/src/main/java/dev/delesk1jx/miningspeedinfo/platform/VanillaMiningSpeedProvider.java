package dev.delesk1jx.miningspeedinfo.platform;

import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * Mining speed of the 1.21 tool component.
 *
 * <p>Every tool now carries a list of rules that say which blocks it is made for and how fast it digs
 * them. The fastest mining rule wins; rules for things that are not mined, such as the one that lets
 * swords hurt mobs faster, are ignored so that a sword is not reported as a digging tool.
 */
public final class VanillaMiningSpeedProvider implements MiningSpeedProvider {

    /**
     * Vanilla keeps the level of a tool in the block tag of the rule that lists the blocks it cannot
     * break, for example {@code #minecraft:incorrect_for_diamond_tool}. This is what the numbers in
     * the game mean, gold is as weak as wood because it only exists to be fast.
     */
    private static final Map<String, Integer> HARVEST_LEVELS = Map.of(
            "wooden", 0,
            "stone", 1,
            "iron", 2,
            "diamond", 3,
            "gold", 0,
            "netherite", 4
    );

    private static final String TIERS_PREFIX = "incorrect_for_";
    private static final String TIERS_SUFFIX = "_tool";

    @Override
    public MiningSpeed getMiningSpeed(ItemStack stack, boolean includeNonMiningTools) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            return null;
        }

        float mining = 0.0F;
        float other = 0.0F;
        boolean digsBlocks = false;

        for (Tool.Rule rule : tool.rules()) {
            Float speed = rule.speed().orElse(null);
            HolderSet<Block> blocks = rule.blocks();
            if (speed == null || blocks == null) {
                continue;
            }
            if (isMiningTag(blocks)) {
                digsBlocks = true;
                mining = Math.max(mining, speed);
            } else {
                other = Math.max(other, speed);
            }
        }

        float base = digsBlocks ? mining : (includeNonMiningTools ? other : 0.0F);
        if (base <= 0.0F) {
            return null;
        }

        float bonus = 0.0F;
        if (base > 1.0F) {
            int level = efficiencyLevel(stack);
            if (level > 0) {
                bonus = level * level + 1;
            }
        }
        return new MiningSpeed(base, bonus);
    }

    @Override
    public int getHarvestLevel(ItemStack stack) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            return UNKNOWN_HARVEST_LEVEL;
        }
        for (Tool.Rule rule : tool.rules()) {
            HolderSet<Block> blocks = rule.blocks();
            if (blocks == null) {
                continue;
            }
            Integer level = levelOf(blocks);
            if (level != null) {
                return level;
            }
        }
        return UNKNOWN_HARVEST_LEVEL;
    }

    private static Integer levelOf(HolderSet<Block> blocks) {
        return blocks.unwrapKey()
                .map(key -> {
                    String path = key.location().getPath();
                    if (path.startsWith(TIERS_PREFIX) && path.endsWith(TIERS_SUFFIX)) {
                        String tier = path.substring(TIERS_PREFIX.length(), path.length() - TIERS_SUFFIX.length());
                        return HARVEST_LEVELS.get(tier);
                    }
                    return null;
                })
                .orElse(null);
    }

    private static int efficiencyLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getTagEnchantments();
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.is(Enchantments.EFFICIENCY)) {
                return enchantments.getLevel(holder);
            }
        }
        return 0;
    }

    /** Vanilla digs blocks through the "mineable/..." tags, everything else is a special case. */
    private static boolean isMiningTag(HolderSet<Block> blocks) {
        return blocks.unwrapKey()
                .map(key -> key.location().getPath().startsWith("mineable/"))
                .orElse(false);
    }
}
