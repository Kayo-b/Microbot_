package net.runelite.client.plugins.microbot.sticktothescript.varrockanvil;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;


import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.StickToTheScript + "Varrock Anvil",
        description = "StickToTheScript\'s Varrock Anvil plugin",
        tags = {"StickToTheScript", "STTS", "varrock", "anvil", "smithing"},
        enabledByDefault = false
)
@Slf4j
public class VarrockAnvilPlugin extends Plugin implements SchedulablePlugin {
    @Inject
    private VarrockAnvilConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private VarrockAnvilOverlay overlay;

    @Inject
    VarrockAnvilScript script;

    @Override
    public void onPluginScheduleEntrySoftStopEvent(PluginScheduleEntrySoftStopEvent event) {
        if (event.getPlugin() == this) {
            // Cleanup operations
            Microbot.getClientThread().invokeLater(() -> {
                Microbot.stopPlugin(this);
            });
        }
    }

    @Provides
    VarrockAnvilConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(VarrockAnvilConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }

        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
    }
}
