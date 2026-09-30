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

/**
 * Mining speed of the 1.21 tool component.
 *
 * <p>Every tool now carries a list of rules that say which blocks it is made for and how fast it digs
 * them. The fastest mining rule wins; rules for things that are not mined, such as the one that lets
 * swords hurt mobs faster, are ignored so that a sword is not reported as a digging tool.
 */
public final class VanillaMiningSpeedProvider implements MiningSpeedProvider {

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
