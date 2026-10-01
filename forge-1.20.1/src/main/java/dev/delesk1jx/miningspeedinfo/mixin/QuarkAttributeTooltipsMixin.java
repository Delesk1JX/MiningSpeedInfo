package dev.delesk1jx.miningspeedinfo.mixin;

import dev.delesk1jx.miningspeedinfo.client.QuarkRowRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Puts the values of this mod behind the values of Quark's attribute panel, on the same row.
 *
 * <p>The two methods Quark inherits from the tooltip component carry the names the game uses while it
 * runs, which on 1.20.1 are the SRG ones, so they are written down as they are found at runtime. Nothing
 * else here touches Minecraft, so no reference map is needed.
 *
 * <p>Both injections sit at the end of the method. If a Quark update moves them, {@code require = 0}
 * skips both and the mod falls back to its plain tooltip line.
 */
@Mixin(targets = "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent", remap = false)
public abstract class QuarkAttributeTooltipsMixin {

    /** Quark's {@code getWidth(Font)}, the width of the whole panel. */
    @Shadow
    public abstract int m_142069_(Font font);

    @Shadow
    public abstract ItemStack stack();

    @Inject(method = "m_142069_", at = @At("RETURN"), cancellable = true, require = 0)
    private void miningspeedinfo$reserveSpace(Font font, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + QuarkRowRenderer.width(this.stack(), font));
    }

    /** Quark's {@code renderImage(Font, int, int, GuiGraphics)}. */
    @Inject(method = "m_183452_", at = @At("RETURN"), require = 0)
    private void miningspeedinfo$drawValues(Font font, int tooltipX, int tooltipY, GuiGraphics graphics,
                                            CallbackInfo ci) {
        QuarkRowRenderer.renderBehindPanel(this.stack(), font, this.m_142069_(font), tooltipX, tooltipY, graphics);
    }
}
