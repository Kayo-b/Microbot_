package net.runelite.client.plugins.microbot.smelting.controller;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.smelting.AutoSmeltingConfig;
import net.runelite.client.plugins.microbot.smelting.AutoSmeltingPlugin;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

/**
 * Controller for the AutoSmelting plugin socket automation
 * Provides remote control over smelting operations via socket commands
 */
@Slf4j
public class AutoSmeltingController implements PluginController {
    private final AutoSmeltingPlugin plugin;

    public AutoSmeltingController(AutoSmeltingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginName() {
        return "auto_smelting";
    }

    @Override
    public String[] getSupportedActions() {
        return new String[]{"start_smelting", "stop_smelting", "get_status", "get_config", "set_config"};
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        try {
            switch (action.toLowerCase()) {
                case "start_smelting":
                    plugin.startSmelting();
                    return new ApiResponse(true, "Smelting started successfully", 0);

                case "stop_smelting":
                    plugin.stopSmelting();
                    return new ApiResponse(true, "Smelting stopped successfully", 0);

                case "get_status":
                    boolean isRunning = plugin.isRunning();
                    JsonObject statusData = new JsonObject();
                    statusData.addProperty("running", isRunning);
                    statusData.addProperty("coalBagEmpty", plugin.isCoalBagEmpty());
                    return new ApiResponse(true, "Status retrieved", 0, statusData);

                case "set_config":
                    return handleConfigUpdate(command);

                case "get_config":
                    AutoSmeltingConfig config = plugin.getConfig();
                    JsonObject configData = new JsonObject();
                    configData.addProperty("selected_bar_type", config.SELECTED_BAR_TYPE().toString());
                    return new ApiResponse(true, "Configuration retrieved", 0, configData);

                default:
                    return new ApiResponse(false, "Unknown action: " + action, 0);
            }
        } catch (Exception e) {
            log.error("Error processing command for {}: {}", getPluginName(), e.getMessage());
            return new ApiResponse(false, "Command processing failed: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleConfigUpdate(JsonObject command) {
        try {
            if (command.has("selected_bar_type")) {
                plugin.updateSelectedBarTypeConfig(command.get("selected_bar_type").getAsString());
            }

            return new ApiResponse(true, "Configuration updated successfully", 0);
        } catch (Exception e) {
            log.error("Error updating configuration for {}: {}", getPluginName(), e.getMessage());
            return new ApiResponse(false, "Configuration update failed: " + e.getMessage(), 0);
        }
    }
}
