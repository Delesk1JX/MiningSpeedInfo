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
 * <p>Both injections sit at the end of the method and only call into this mod. NeoForge runs with the
 * same names the game is compiled with, so no reference map is needed here. If a Quark update moves the
 * methods, {@code require = 0} skips both injections and the mod falls back to its plain tooltip line.
 */
@Mixin(targets = "org.violetmoon.quark.content.client.tooltip.AttributeTooltips$AttributeComponent", remap = false)
public abstract class QuarkAttributeTooltipsMixin {

    @Shadow
    public abstract int getWidth(Font font);

    @Shadow
    public abstract ItemStack stack();

    @Inject(method = "getWidth", at = @At("RETURN"), cancellable = true, require = 0)
    private void miningspeedinfo$reserveSpace(Font font, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + QuarkRowRenderer.width(this.stack(), font));
    }

    @Inject(method = "renderImage", at = @At("RETURN"), require = 0)
    private void miningspeedinfo$drawValues(Font font, int tooltipX, int tooltipY, GuiGraphics graphics,
                                            CallbackInfo ci) {
        QuarkRowRenderer.renderBehindPanel(this.stack(), font, this.getWidth(font), tooltipX, tooltipY, graphics);
    }
}
