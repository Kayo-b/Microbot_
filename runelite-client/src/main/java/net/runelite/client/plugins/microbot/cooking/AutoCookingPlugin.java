package net.runelite.client.plugins.microbot.cooking;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cooking.scripts.AutoCookingScript;
import net.runelite.client.plugins.microbot.cooking.scripts.BurnBakingScript;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Auto Cooking",
        description = "Microbot cooking plugin",
        tags = {"cooking", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoCookingPlugin extends Plugin implements SchedulablePlugin {
    public static double version = 1.1;
    @Inject
    AutoCookingScript autoCookingScript;
    @Inject
    BurnBakingScript burnBakingScript;
    @Inject
    private AutoCookingConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCookingOverlay overlay;

    @Provides
    AutoCookingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCookingConfig.class);
    }

    @Subscribe
    @Override
    public void onPluginScheduleEntrySoftStopEvent(PluginScheduleEntrySoftStopEvent event) {
        if (event.getPlugin() == this) {
            // Cleanup operations
            Microbot.getClientThread().invokeLater(() -> {
                Microbot.stopPlugin(this);
            });
        }
    }


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        switch (config.cookingActivity()) {
            case COOKING:
                autoCookingScript.run(config);
                break;
            case BURN_BAKING:
                burnBakingScript.run(config);
                break;
            default:
                Microbot.log("Invalid Cooking Activity");
        }
    }

    protected void shutDown() {
        autoCookingScript.shutdown();
        burnBakingScript.shutdown();
        overlayManager.remove(overlay);
    }
}
