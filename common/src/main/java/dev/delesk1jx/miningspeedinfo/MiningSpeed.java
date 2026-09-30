package dev.delesk1jx.miningspeedinfo;

/**
 * The mining speed of a tool.
 *
 * @param base  speed the tool has on its own, without any enchantments
 * @param bonus extra speed granted by the Efficiency enchantment
 */
public record MiningSpeed(float base, float bonus) {

    public static final MiningSpeed NONE = new MiningSpeed(0.0F, 0.0F);

    public float total() {
        return this.base + this.bonus;
    }

    public boolean hasBonus() {
        return this.bonus > 0.0F;
    }
}
