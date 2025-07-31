package net.runelite.client.plugins.microbot.cooking.controller;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.cooking.AutoCookingPlugin;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;

import java.util.HashMap;
import java.util.Map;

public class AutoCookingController implements PluginController {
    private final AutoCookingPlugin plugin;

    public AutoCookingController(AutoCookingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "start_cooking":
                return startCooking();
            case "stop_cooking":
                return stopCooking();
            case "get_status":
                return getStatus();
            case "set_config":
                return setConfig(command);
            case "get_config":
                return getConfig();
            default:
                return new ApiResponse(false, "Unknown cooking action: " + action, 0);
        }
    }
    
    @Override
    public String getPluginName() {
        return "auto_cooking";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "start_cooking", "stop_cooking", "get_status", "set_config", "get_config"
        };
    }
    
    private ApiResponse startCooking() {
        try {
            plugin.startCooking();
            return new ApiResponse(true, "Cooking started successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to start cooking: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse stopCooking() {
        try {
            plugin.stopCooking();
            return new ApiResponse(true, "Cooking stopped successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to stop cooking: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getStatus() {
        try {
            boolean isRunning = plugin.isRunning();
            String status = isRunning ? "running" : "stopped";
            return new ApiResponse(true, "Cooking status: " + status, isRunning ? 1 : 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get status: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse setConfig(JsonObject command) {
        try {
            if (command != null) {
                if (command.has("cooking_activity")) {
                    plugin.updateCookingActivityConfig(command.get("cooking_activity").getAsString());
                }
                if (command.has("cooking_item")) {
                    plugin.updateCookingItemConfig(command.get("cooking_item").getAsString());
                }
                if (command.has("cooking_location")) {
                    plugin.updateCookingLocationConfig(command.get("cooking_location").getAsString());
                }
                if (command.has("use_nearest_location")) {
                    plugin.updateNearestLocationConfig(command.get("use_nearest_location").getAsBoolean());
                }
                if (command.has("drop_burnt_items")) {
                    plugin.updateDropBurntItemsConfig(command.get("drop_burnt_items").getAsBoolean());
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
            AutoCookingConfig config = plugin.getConfig();
            Map<String, Object> configData = new HashMap<>();
            configData.put("cooking_activity", config.cookingActivity().name());
            configData.put("cooking_item", config.cookingItem().name());
            configData.put("cooking_location", config.cookingLocation().name());
            configData.put("use_nearest_location", config.useNearestCookingLocation());
            configData.put("drop_burnt_items", config.shouldDropBurntItems());
            
            return new ApiResponse(true, "Configuration retrieved successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get configuration: " + e.getMessage(), 0);
        }
    }
}
