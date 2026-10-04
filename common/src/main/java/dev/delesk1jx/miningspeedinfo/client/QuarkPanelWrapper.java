package dev.delesk1jx.miningspeedinfo.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

/**
 * Wraps the tooltip component Quark draws for its attribute panel and puts the values of this mod right
 * behind the ones Quark shows itself.
 *
 * <p>Quark hands out its panel through the factory the loader asks for whenever a tooltip component has
 * to be drawn on the client. Registering a wrapper for Quark's class therefore keeps the values on the
 * same row, without the mod having to write into Quark's code, and without a mixin that would break
 * the moment Quark moves a method.
 */
public final class QuarkPanelWrapper implements TooltipComponent, ClientTooltipComponent {

    private final ClientTooltipComponent panel;
    private final ItemStack stack;

    QuarkPanelWrapper(ClientTooltipComponent panel, ItemStack stack) {
        this.panel = panel;
        this.stack = stack;
    }

    @Override
    public int getHeight() {
        return this.panel.getHeight();
    }

    /**
     * The panel plus the values behind it.
     *
     * <p>The gap counts here as well: Quark reports a width that already stops at the end of its last
     * number, while its rendering would have continued a further {@link QuarkRowRenderer#GAP} pixels
     * before the next value starts. Leaving that gap out is what made the last number poke out of the
     * tooltip.
     */
    @Override
    public int getWidth(Font font) {
        return this.panel.getWidth(font) + QuarkRowRenderer.GAP + QuarkRowRenderer.width(this.stack, font);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f pose, MultiBufferSource.BufferSource buffers) {
        this.panel.renderText(font, x, y, pose, buffers);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        this.panel.renderImage(font, x, y, graphics);
        // The width Quark itself reports, so the values land exactly where its last value ended.
        QuarkRowRenderer.renderBehindPanel(this.stack, font, this.panel.getWidth(font), x, y, graphics);
    }
}
