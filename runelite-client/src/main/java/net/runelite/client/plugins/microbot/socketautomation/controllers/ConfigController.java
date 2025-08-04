package net.runelite.client.plugins.microbot.socketautomation.controllers;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

import javax.inject.Inject;

@Slf4j
public class ConfigController implements PluginController {
    
    @Inject
    private ConfigManager configManager;
    
    public ConfigController(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "set_config":
                return setPluginConfig(command);
            case "get_config":
                return getPluginConfig(command);
            case "preset_configs":
                return presetMultipleConfigs(command);
            case "list_config_groups":
                return listConfigGroups();
            case "backup_config":
                return backupPluginConfig(command);
            case "restore_config":
                return restorePluginConfig(command);
            default:
                return new ApiResponse(false, "Unknown config action: " + action, 0);
        }
    }
    
    private ApiResponse setPluginConfig(JsonObject command) {
        try {
            if (!command.has("plugin_group")) {
                return new ApiResponse(false, "Missing 'plugin_group' field", 0);
            }
            
            if (!command.has("config_key")) {
                return new ApiResponse(false, "Missing 'config_key' field", 0);
            }
            
            if (!command.has("config_value")) {
                return new ApiResponse(false, "Missing 'config_value' field", 0);
            }
            
            String pluginGroup = command.get("plugin_group").getAsString();
            String configKey = command.get("config_key").getAsString();
            String configValue = command.get("config_value").getAsString();
            
            String profile = null;
            if (command.has("profile")) {
                profile = command.get("profile").getAsString();
            }
            
            if (profile != null) {
                configManager.setConfiguration(pluginGroup, profile, configKey, configValue);
            } else {
                configManager.setConfiguration(pluginGroup, configKey, configValue);
            }
            
            JsonObject responseData = new JsonObject();
            responseData.addProperty("plugin_group", pluginGroup);
            responseData.addProperty("config_key", configKey);
            responseData.addProperty("config_value", configValue);
            if (profile != null) {
                responseData.addProperty("profile", profile);
            }
            
            log.info("Set config for {}.{} = {}", pluginGroup, configKey, configValue);
            
            return new ApiResponse(true, "Config set successfully", 0, responseData);
            
        } catch (Exception e) {
            log.error("Error setting plugin config: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to set config: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getPluginConfig(JsonObject command) {
        try {
            if (!command.has("plugin_group")) {
                return new ApiResponse(false, "Missing 'plugin_group' field", 0);
            }
            
            if (!command.has("config_key")) {
                return new ApiResponse(false, "Missing 'config_key' field", 0);
            }
            
            String pluginGroup = command.get("plugin_group").getAsString();
            String configKey = command.get("config_key").getAsString();
            
            String profile = null;
            if (command.has("profile")) {
                profile = command.get("profile").getAsString();
            }
            
            String value;
            if (profile != null) {
                value = configManager.getConfiguration(pluginGroup, profile, configKey);
            } else {
                value = configManager.getConfiguration(pluginGroup, configKey);
            }
            
            JsonObject responseData = new JsonObject();
            responseData.addProperty("plugin_group", pluginGroup);
            responseData.addProperty("config_key", configKey);
            responseData.addProperty("config_value", value != null ? value : "null");
            if (profile != null) {
                responseData.addProperty("profile", profile);
            }
            
            return new ApiResponse(true, "Config retrieved successfully", 0, responseData);
            
        } catch (Exception e) {
            log.error("Error getting plugin config: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to get config: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse presetMultipleConfigs(JsonObject command) {
        try {
            if (!command.has("plugin_group")) {
                return new ApiResponse(false, "Missing 'plugin_group' field", 0);
            }
            
            if (!command.has("configs")) {
                return new ApiResponse(false, "Missing 'configs' field", 0);
            }
            
            String pluginGroup = command.get("plugin_group").getAsString();
            JsonObject configs = command.getAsJsonObject("configs");
            
            String profile = null;
            if (command.has("profile")) {
                profile = command.get("profile").getAsString();
            }
            
            int configsSet = 0;
            for (String key : configs.keySet()) {
                String value = configs.get(key).getAsString();
                
                if (profile != null) {
                    configManager.setConfiguration(pluginGroup, profile, key, value);
                } else {
                    configManager.setConfiguration(pluginGroup, key, value);
                }
                
                configsSet++;
                log.debug("Set config for {}.{} = {}", pluginGroup, key, value);
            }
            
            JsonObject responseData = new JsonObject();
            responseData.addProperty("plugin_group", pluginGroup);
            responseData.addProperty("configs_set", configsSet);
            if (profile != null) {
                responseData.addProperty("profile", profile);
            }
            
            log.info("Preset {} configs for plugin group: {}", configsSet, pluginGroup);
            
            return new ApiResponse(true, "Multiple configs preset successfully", 0, responseData);
            
        } catch (Exception e) {
            log.error("Error presetting configs: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to preset configs: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse listConfigGroups() {
        try {
            JsonObject responseData = new JsonObject();
            responseData.addProperty("message", "Config groups listing not implemented - use direct group names");
            
            return new ApiResponse(true, "Config groups info", 0, responseData);
            
        } catch (Exception e) {
            log.error("Error listing config groups: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to list config groups: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse backupPluginConfig(JsonObject command) {
        try {
            return new ApiResponse(false, "Backup functionality not yet implemented", 0);
        } catch (Exception e) {
            log.error("Error backing up config: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to backup config: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse restorePluginConfig(JsonObject command) {
        try {
            return new ApiResponse(false, "Restore functionality not yet implemented", 0);
        } catch (Exception e) {
            log.error("Error restoring config: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to restore config: " + e.getMessage(), 0);
        }
    }
    
    @Override
    public String getPluginName() {
        return "Config";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "set_config", 
            "get_config", 
            "preset_configs", 
            "list_config_groups",
            "backup_config",
            "restore_config"
        };
    }
}
