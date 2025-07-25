package net.runelite.client.plugins.microbot.socketautomation.controllers;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

public interface PluginController {
    ApiResponse processCommand(String action, JsonObject command);
    String getPluginName();
    String[] getSupportedActions();
}
