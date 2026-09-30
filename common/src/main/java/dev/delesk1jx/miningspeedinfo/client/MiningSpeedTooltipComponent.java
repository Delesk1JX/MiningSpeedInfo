package dev.delesk1jx.miningspeedinfo.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.delesk1jx.miningspeedinfo.MiningSpeed;
import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * The mining speed rendered as its own row, right behind Quark's attribute panel.
 *
 * <p>All three images are ordinary resource pack textures, so they can be recoloured or replaced
 * without touching the mod.
 */
public final class MiningSpeedTooltipComponent implements TooltipComponent, ClientTooltipComponent {

    private static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/mining_speed.png");
    private static final ResourceLocation UPGRADE =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/upgrade.png");
    private static final ResourceLocation DOWNGRADE =
            ResourceLocation.fromNamespaceAndPath(MiningSpeedInfo.MOD_ID, "textures/gui/downgrade.png");

    /** Matches the size of the images Quark draws next to its own values. */
    private static final int IMAGE = 9;
    private static final int ARROW = 13;
    private static final int TEXT_OFFSET = 12;
    private static final int ROW_HEIGHT = 10;
    private static final int ANIMATION_LENGTH = 20;

    private final MiningSpeed speed;
    private final int comparison;
    private final ChatFormatting color;
    private final int decimals;
    private final boolean animated;

    public MiningSpeedTooltipComponent(MiningSpeed speed, int comparison, ChatFormatting color, int decimals,
                                       boolean animated) {
        this.speed = speed;
        this.comparison = comparison;
        this.color = color;
        this.decimals = decimals;
        this.animated = animated;
    }

    @Override
    public int getWidth(Font font) {
        return TEXT_OFFSET + font.width(label());
    }

    @Override
    public int getHeight() {
        return ROW_HEIGHT;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, 500.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.blit(ICON, x, y, 0.0F, 0.0F, IMAGE, IMAGE, IMAGE, IMAGE);

        if (this.comparison != 0) {
            int arrowY = y - 2;
            if (this.animated && tick() % ANIMATION_LENGTH < ANIMATION_LENGTH / 2) {
                arrowY++;
            }
            graphics.blit(this.comparison > 0 ? UPGRADE : DOWNGRADE,
                    x - 2, arrowY, 0.0F, 0.0F, ARROW, ARROW, ARROW, ARROW);
        }

        graphics.drawString(font, label(), x + TEXT_OFFSET, y + 1, -1);

        pose.popPose();
    }

    /** "Mining Speed: 6", with the value coloured the way Quark colours its own comparisons. */
    private MutableComponent label() {
        ChatFormatting valueColor = switch (this.comparison) {
            case 1 -> ChatFormatting.GREEN;
            case -1 -> ChatFormatting.RED;
            default -> this.color;
        };
        return TooltipHandler.miningSpeedName().append(": ").append(TooltipHandler.buildValue(this.speed, valueColor));
    }

    private static int tick() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.gui == null ? 0 : minecraft.gui.getGuiTicks();
    }
}
