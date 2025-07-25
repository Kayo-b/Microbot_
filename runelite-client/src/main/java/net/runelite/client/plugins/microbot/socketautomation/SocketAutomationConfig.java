package net.runelite.client.plugins.microbot.socketautomation;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(SocketAutomationConfig.GROUP_NAME)
public interface SocketAutomationConfig extends Config {
    String GROUP_NAME = "socketautomation";
    
    @ConfigSection(
            name = "General Settings",
            description = "Basic socket automation configuration",
            position = 0
    )
    String generalSection = "general";
    
    @ConfigSection(
            name = "Port Configuration",
            description = "Port management settings",
            position = 1
    )
    String portSection = "ports";
    
    @ConfigSection(
            name = "Security",
            description = "Security and access control settings",
            position = 2
    )
    String securitySection = "security";
    
    @ConfigItem(
            keyName = "autoStartEnabled",
            name = "Auto-start on Plugin Load",
            description = "Automatically start socket listener when plugin is enabled",
            position = 0,
            section = generalSection
    )
    default boolean autoStartEnabled() {
        return true;
    }
    
    @ConfigItem(
            keyName = "primaryPort",
            name = "Primary Port",
            description = "Primary port for socket automation API (default: 45678)",
            position = 0,
            section = portSection
    )
    default int primaryPort() {
        return 45678;
    }
    
    @ConfigItem(
            keyName = "portRangeStart",
            name = "Port Range Start",
            description = "Starting port for automatic port allocation",
            position = 1,
            section = portSection
    )
    default int portRangeStart() {
        return 45679;
    }
    
    @ConfigItem(
            keyName = "portRangeEnd",
            name = "Port Range End",
            description = "Ending port for automatic port allocation",
            position = 2,
            section = portSection
    )
    default int portRangeEnd() {
        return 45699;
    }
    
    @ConfigItem(
            keyName = "maxConnections",
            name = "Max Concurrent Connections",
            description = "Maximum number of concurrent socket connections",
            position = 3,
            section = portSection
    )
    default int maxConnections() {
        return 10;
    }
    
    @ConfigItem(
            keyName = "allowLocalOnly",
            name = "Localhost Only",
            description = "Only allow connections from localhost (recommended)",
            position = 0,
            section = securitySection
    )
    default boolean allowLocalOnly() {
        return true;
    }
    
    @ConfigItem(
            keyName = "logConnections",
            name = "Log Connections",
            description = "Log socket connection attempts and commands",
            position = 1,
            section = securitySection
    )
    default boolean logConnections() {
        return true;
    }
}
