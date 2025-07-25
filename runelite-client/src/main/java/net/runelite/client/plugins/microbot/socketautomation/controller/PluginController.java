package net.runelite.client.plugins.microbot.socketautomation.controller;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

public interface PluginController {
    String getPluginName();
    ApiResponse processCommand(String action, JsonObject data);
    default String[] getSupportedActions() {
        return new String[]{"start", "stop", "pause", "resume", "status"};
    }
}
