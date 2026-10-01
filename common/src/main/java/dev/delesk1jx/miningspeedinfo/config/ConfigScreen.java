package dev.delesk1jx.miningspeedinfo.config;

import dev.delesk1jx.miningspeedinfo.MiningSpeedInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * The settings screen. It is built from plain vanilla widgets on purpose: the mod stays free of any
 * library, and the buttons look the same on both supported game versions.
 */
public final class ConfigScreen extends Screen {

    private static final int MAX_PANEL_WIDTH = 260;
    private static final int PADDING = 6;
    private static final int MAX_ROW_HEIGHT = 24;
    private static final int MIN_ROW_HEIGHT = 16;
    private static final int PANEL_PADDING = 4;

    /** One row per option plus the "Done" button. */
    private static final int ROWS = 11;

    /** The numbers {@link MiningSpeedConfig#decimals} can be set to, {@code -1} meaning "auto". */
    private static final int[] DECIMALS = {-1, 0, 1, 2};

    private final Screen parent;

    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int rowHeight;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("miningspeedinfo.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        MiningSpeedConfig config = MiningSpeedInfo.config;
        int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        int rows = ROWS;
        this.rowHeight = Math.max(MIN_ROW_HEIGHT, Math.min(MAX_ROW_HEIGHT, (screenHeight - 20) / rows));
        this.panelHeight = this.rowHeight * rows + PANEL_PADDING * 2;
        this.panelWidth = Math.min(MAX_PANEL_WIDTH, screenWidth - 20);
        this.panelLeft = (screenWidth - this.panelWidth) / 2;
        this.panelTop = Math.max(MAX_ROW_HEIGHT, (screenHeight - this.panelHeight) / 2);

        int buttonLeft = this.panelLeft + PADDING;
        int buttonWidth = this.panelWidth - PADDING * 2;
        int row = 0;
        int top = this.panelTop + PANEL_PADDING;

        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "enabled", config.enabled, () -> {
            config.enabled = !config.enabled;
        });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "require_shift", config.requireShift, () -> {
            config.requireShift = !config.requireShift;
        });
        addCycle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "color",
                Component.translatable("chat.color." + colorName(config)),
                () -> {
                    List<ChatFormatting> colors = MiningSpeedConfig.COLORS;
                    int index = colors.indexOf(config.colorFormatting());
                    config.color = colors.get((index + 1) % colors.size()).getName();
                });
        addCycle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "decimals",
                decimalsLabel(config.decimals),
                () -> {
                    int index = 0;
                    for (int i = 0; i < DECIMALS.length; i++) {
                        if (DECIMALS[i] == config.decimals) {
                            index = i;
                        }
                    }
                    config.decimals = DECIMALS[(index + 1) % DECIMALS.length];
                });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "include_efficiency",
                config.includeEfficiency, () -> {
                    config.includeEfficiency = !config.includeEfficiency;
                    config.validate();
                });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "efficiency_breakdown",
                config.showEfficiencyBreakdown && config.includeEfficiency, () -> {
                    config.showEfficiencyBreakdown = !config.showEfficiencyBreakdown;
                });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "harvest_level",
                config.showHarvestLevel, () -> {
                    config.showHarvestLevel = !config.showHarvestLevel;
                });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "non_mining_tools",
                config.showForNonMiningTools, () -> {
                    config.showForNonMiningTools = !config.showForNonMiningTools;
                });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "quark_tooltip", config.quarkTooltip, () -> {
            config.quarkTooltip = !config.quarkTooltip;
        });
        addToggle(buttonLeft, top + row++ * this.rowHeight, buttonWidth, "quark_comparison",
                config.quarkComparison, () -> {
                    config.quarkComparison = !config.quarkComparison;
                });

        addButton(buttonLeft, top + row * this.rowHeight, buttonWidth, Component.translatable("gui.done"), button -> onClose());
    }

    private void addToggle(int x, int y, int width, String key, boolean value, Runnable onPress) {
        addButton(x, y, width, Component.translatable("miningspeedinfo.option." + key, onOff(value)), button -> {
            onPress.run();
            config().validate();
            rebuildWidgets();
        });
    }

    private void addCycle(int x, int y, int width, String key, Component value, Runnable onPress) {
        addButton(x, y, width, Component.translatable("miningspeedinfo.option." + key, value), button -> {
            onPress.run();
            config().validate();
            rebuildWidgets();
        });
    }

    private void addButton(int x, int y, int width, Component message, Button.OnPress onPress) {
        this.addRenderableWidget(Button.builder(message, onPress)
                .bounds(x, y, width, this.rowHeight)
                .build());
    }

    private MiningSpeedConfig config() {
        return MiningSpeedInfo.config;
    }

    private static Component onOff(boolean value) {
        return Component.translatable(value ? "options.on" : "options.off");
    }

    private static Component decimalsLabel(int decimals) {
        if (decimals < 0) {
            return Component.translatable("miningspeedinfo.value.auto");
        }
        return Component.literal(String.valueOf(decimals));
    }

    private static String colorName(MiningSpeedConfig config) {
        ChatFormatting formatting = config.colorFormatting();
        return formatting == null ? "dark_green" : formatting.getName();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Vanilla changed the background helper between 1.20.1 and 1.21, so the dimming is done here
        // to keep one screen working on both game versions.
        int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
        graphics.fill(0, 0, screenWidth, screenHeight, 0xC0101010);
        graphics.fill(this.panelLeft, this.panelTop,
                this.panelLeft + this.panelWidth, this.panelTop + this.panelHeight, 0xE0182028);
        graphics.fill(this.panelLeft, this.panelTop,
                this.panelLeft + this.panelWidth, this.panelTop + 1, 0x70FFFFFF);
        graphics.drawCenteredString(this.font, this.title,
                this.panelLeft + this.panelWidth / 2, this.panelTop - 12, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        MiningSpeedInfo.saveConfig();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
