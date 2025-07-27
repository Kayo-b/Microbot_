package net.runelite.client.plugins.microbot.woodcutting.controller;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingConfig;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the AutoWoodcutting plugin socket automation
 * Provides remote control over woodcutting operations via socket commands
 */
public class AutoWoodcuttingController implements PluginController {
    private final AutoWoodcuttingPlugin plugin;

    public AutoWoodcuttingController(AutoWoodcuttingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginName() {
        return "auto_woodcutting";
    }

    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "start_woodcutting", "stop_woodcutting", "get_status", "set_config", "get_config"
        };
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "start_woodcutting":
                return startWoodcutting();
            case "stop_woodcutting":
                return stopWoodcutting();
            case "get_status":
                return getStatus();
            case "set_config":
                return setConfig(command);
            case "get_config":
                return getConfig();
            default:
                return new ApiResponse(false, "Unknown woodcutting action: " + action, 0);
        }
    }

    private ApiResponse startWoodcutting() {
        try {
            plugin.startWoodcutting();
            return new ApiResponse(true, "Woodcutting started successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to start woodcutting: " + e.getMessage(), 0);
        }
    }

    private ApiResponse stopWoodcutting() {
        try {
            plugin.stopWoodcutting();
            return new ApiResponse(true, "Woodcutting stopped successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to stop woodcutting: " + e.getMessage(), 0);
        }
    }

    private ApiResponse getStatus() {
        try {
            boolean isRunning = plugin.isRunning();
            String status = isRunning ? "running" : "stopped";
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("running", isRunning);
            statusData.put("can_light_fire", !plugin.isCannotLightFire());
            return new ApiResponse(true, "Woodcutting status: " + status, isRunning ? 1 : 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get status: " + e.getMessage(), 0);
        }
    }

    private ApiResponse setConfig(JsonObject command) {
        try {
            if (command != null) {
                if (command.has("tree_type")) {
                    plugin.updateTreeTypeConfig(command.get("tree_type").getAsString());
                }
                if (command.has("distance_to_stray")) {
                    plugin.updateDistanceToStrayConfig(command.get("distance_to_stray").getAsInt());
                }
                if (command.has("hop_when_player_detected")) {
                    plugin.updateHopWhenPlayerDetectedConfig(command.get("hop_when_player_detected").getAsBoolean());
                }
                if (command.has("firemake_only")) {
                    plugin.updateFiremakeOnlyConfig(command.get("firemake_only").getAsBoolean());
                }
                if (command.has("reset_options")) {
                    plugin.updateResetOptionsConfig(command.get("reset_options").getAsString());
                }
                if (command.has("items_to_bank")) {
                    plugin.updateItemsToBankConfig(command.get("items_to_bank").getAsString());
                }
                if (command.has("walk_back")) {
                    plugin.updateWalkBackConfig(command.get("walk_back").getAsString());
                }
                
                return new ApiResponse(true, "Configuration updated successfully", 1);
            } else {
                return new ApiResponse(false, "No configuration data provided", 0);
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to update configuration: " + e.getMessage(), 0);
        }
    }

    private ApiResponse getConfig() {
        try {
            AutoWoodcuttingConfig config = plugin.getConfig();
            Map<String, Object> configData = new HashMap<>();
            configData.put("tree_type", config.TREE().name());
            configData.put("distance_to_stray", config.distanceToStray());
            configData.put("hop_when_player_detected", config.hopWhenPlayerDetected());
            configData.put("firemake_only", config.firemakeOnly());
            configData.put("reset_options", config.resetOptions().name());
            configData.put("items_to_bank", config.itemsToBank());
            configData.put("walk_back", config.walkBack().name());
            
            return new ApiResponse(true, "Configuration retrieved successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get configuration: " + e.getMessage(), 0);
        }
    }
}
