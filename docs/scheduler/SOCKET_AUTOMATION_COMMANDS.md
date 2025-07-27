# Socket Automation Multi-Client Control Documentation

This document describes all available commands for the Socket Automation Multi-Client Control system that manages RuneLite plugins across multiple clients.

## Table of Contents
- [Overview](#overview)
- [Setup](#setup)
- [Discovery Commands](#discovery-commands)
- [Scheduler Plugin Commands](#scheduler-plugin-commands)
- [Auto Mining Plugin Commands](#auto-mining-plugin-commands)
- [Auto Fishing Plugin Commands](#auto-fishing-plugin-commands)
- [Auto Cooking Plugin Commands](#auto-cooking-plugin-commands)
- [Auto Smelting Plugin Commands](#auto-smelting-plugin-commands)
- [Auto Woodcutting Plugin Commands](#auto-woodcutting-plugin-commands)
- [AIO Fighter Plugin Commands](#aio-fighter-plugin-commands)
- [Global Parameters](#global-parameters)
- [Usage Examples](#usage-examples)

## Overview

The Socket Automation system allows you to control multiple RuneLite clients simultaneously through socket commands. It supports 6 different plugins with comprehensive configuration and control capabilities.

### Supported Plugins:
1. **Scheduler** - Task scheduling and automation
2. **Auto Mining** - Automated mining operations
3. **Auto Fishing** - Automated fishing operations  
4. **Auto Cooking** - Automated cooking operations
5. **Auto Smelting** - Automated smelting operations
6. **Auto Woodcutting** - Automated woodcutting operations
7. **AIO Fighter** - All-in-one combat automation

## Setup

1. Ensure RuneLite is running with the Socket Automation plugin enabled
2. Default ports: 45678, 45679, 45680, 45681, 45682
3. Run the PowerShell script with appropriate parameters

## Discovery Commands

### Discover Active Clients
```powershell
.\multi-client-control.ps1 -Action discover
```
**Description:** Finds all active RuneLite clients and displays their connection information.

## Scheduler Plugin Commands

### Basic Operations
```powershell
# Start scheduler on all clients
.\multi-client-control.ps1 -Action start_scheduler

# Stop scheduler on all clients
.\multi-client-control.ps1 -Action stop_scheduler

# Pause scheduler on all clients
.\multi-client-control.ps1 -Action pause_scheduler

# Resume scheduler on all clients
.\multi-client-control.ps1 -Action resume_scheduler

# Get scheduler status
.\multi-client-control.ps1 -Action get_status

# Clear all schedules
.\multi-client-control.ps1 -Action clear_schedules

# List available schedule files
.\multi-client-control.ps1 -Action list_schedules
```

### Schedule Management
```powershell
# Load schedule from file
.\multi-client-control.ps1 -Action load_schedule_file -ScheduleFile "my-schedule.json"

# Load schedule from JSON string
.\multi-client-control.ps1 -Action load_schedule_json -ScheduleJson '{"tasks":[]}'

# Load and immediately start (combo command)
.\multi-client-control.ps1 -Action loadrun -ScheduleFile "my-schedule.json"
```

## Auto Mining Plugin Commands

### Basic Operations
```powershell
# Start mining
.\multi-client-control.ps1 -Action start_mining

# Stop mining
.\multi-client-control.ps1 -Action stop_mining

# Get mining status
.\multi-client-control.ps1 -Action get_mining_status

# Get mining configuration
.\multi-client-control.ps1 -Action get_mining_config
```

### Configuration
```powershell
# Configure mining settings
.\multi-client-control.ps1 -Action set_mining_config -MiningOre "IRON" -MiningUseBank $true -MiningItemsToBank "ore,gem" -MiningDistance 25 -MiningMaxPlayers 2
```

### Parameters:
- **MiningOre**: Ore type (IRON, COAL, TIN, COPPER, SILVER, GOLD, MITHRIL, ADAMANTITE, RUNITE)
- **MiningDistance**: Distance to stray from start position (1-50)
- **MiningUseBank**: Enable banking (true/false)
- **MiningItemsToBank**: Items to bank (comma-separated)
- **MiningMaxPlayers**: Max players before world hopping (1-10)

## Auto Fishing Plugin Commands

### Basic Operations
```powershell
# Start fishing
.\multi-client-control.ps1 -Action start_fishing

# Stop fishing
.\multi-client-control.ps1 -Action stop_fishing

# Get fishing status
.\multi-client-control.ps1 -Action get_fishing_status

# Get fishing configuration
.\multi-client-control.ps1 -Action get_fishing_config
```

### Configuration
```powershell
# Configure fishing settings
.\multi-client-control.ps1 -Action set_fishing_config -FishType "LOBSTER" -UseBank $true -UseDepositBox $false -UseEchoHarpoon $false
```

### Parameters:
- **FishType**: Fish type (LOBSTER, SHARK, TUNA, SALMON, TROUT, SARDINE, HERRING, ANCHOVIES)
- **UseBank**: Use banking (true/false)
- **UseDepositBox**: Use deposit box instead of bank (true/false)
- **UseEchoHarpoon**: Use echo harpoon for barbarian fishing (true/false)

## Auto Cooking Plugin Commands

### Basic Operations
```powershell
# Start cooking
.\multi-client-control.ps1 -Action start_cooking

# Stop cooking
.\multi-client-control.ps1 -Action stop_cooking

# Get cooking status
.\multi-client-control.ps1 -Action get_cooking_status

# Get cooking configuration
.\multi-client-control.ps1 -Action get_cooking_config
```

### Configuration
```powershell
# Configure cooking settings
.\multi-client-control.ps1 -Action set_cooking_config -CookingActivity "COOKING" -CookingItem "Raw lobster" -CookingLocation "Rogues' Den" -UseNearestLocation $false -DropBurntItems $true
```

### Parameters:
- **CookingActivity**: Activity type (COOKING, BURN_BAKING)
- **CookingItem**: Item to cook (Raw lobster, Raw shark, Raw tuna, etc.)
- **CookingLocation**: Cooking location (Rogues' Den, Lumbridge, etc.)
- **UseNearestLocation**: Use nearest cooking location (true/false)
- **DropBurntItems**: Drop burnt items (true/false)

## Auto Smelting Plugin Commands

### Basic Operations
```powershell
# Start smelting
.\multi-client-control.ps1 -Action start_smelting

# Stop smelting
.\multi-client-control.ps1 -Action stop_smelting

# Get smelting status
.\multi-client-control.ps1 -Action get_smelting_status

# Get smelting configuration
.\multi-client-control.ps1 -Action get_smelting_config
```

### Configuration
```powershell
# Configure smelting settings
.\multi-client-control.ps1 -Action set_smelting_config -BarType "STEEL"
```

### Parameters:
- **BarType**: Bar type to smelt (BRONZE, IRON, STEEL, MITHRIL, ADAMANTITE, RUNITE, GOLD, SILVER)

## Auto Woodcutting Plugin Commands

### Basic Operations
```powershell
# Start woodcutting
.\multi-client-control.ps1 -Action start_woodcutting

# Stop woodcutting
.\multi-client-control.ps1 -Action stop_woodcutting

# Get woodcutting status
.\multi-client-control.ps1 -Action get_woodcutting_status

# Get woodcutting configuration
.\multi-client-control.ps1 -Action get_woodcutting_config
```

### Configuration
```powershell
# Configure woodcutting settings
.\multi-client-control.ps1 -Action set_woodcutting_config -TreeType "YEW" -DistanceToStray 5 -HopWhenPlayerDetected $true -FiremakeOnly $false -ResetOptions "BANK" -ItemsToBank "Yew logs" -WalkBack $true
```

### Parameters:
- **TreeType**: Tree type (TREE, OAK, WILLOW, TEAK, MAPLE, MAHOGANY, YEW, MAGIC, REDWOOD)
- **DistanceToStray**: Distance to stray (1-10)
- **HopWhenPlayerDetected**: Hop worlds when player detected (true/false)
- **FiremakeOnly**: Only firemaking mode (true/false)
- **ResetOptions**: Reset options (DROP, BANK)
- **ItemsToBank**: Items to bank (comma-separated)
- **WalkBack**: Walk back after banking (true/false)

## AIO Fighter Plugin Commands

### Basic Operations
```powershell
# Start fighting
.\multi-client-control.ps1 -Action start_fighting

# Stop fighting
.\multi-client-control.ps1 -Action stop_fighting

# Get fighting status
.\multi-client-control.ps1 -Action get_fighting_status

# Get fighting configuration
.\multi-client-control.ps1 -Action get_fighting_config
```

### Configuration
```powershell
# Configure fighting settings
.\multi-client-control.ps1 -Action set_fighting_config -AttackableNpcs "Goblin,Orc" -UseFood $true -UsePrayer $false -UseCannon $false -LootItems "Bones,Coins"
```

### Combat Area Management
```powershell
# Set center tile for combat area
.\multi-client-control.ps1 -Action set_center_tile -CenterX 3200 -CenterY 3200

# Set safe spot tile
.\multi-client-control.ps1 -Action set_safe_spot -SafeSpotX 3195 -SafeSpotY 3195
```

### NPC Management
```powershell
# Add single NPC to attack list
.\multi-client-control.ps1 -Action add_npc -NpcName "Goblin"

# Remove single NPC from attack list
.\multi-client-control.ps1 -Action remove_npc -NpcName "Orc"
```

### Parameters:
- **AttackableNpcs**: NPCs to attack (comma-separated list, overwrites existing)
- **UseFood**: Use food in combat (true/false)
- **UsePrayer**: Use prayer in combat (true/false)
- **UseCannon**: Use cannon in combat (true/false)
- **LootItems**: Items to loot (comma-separated)
- **CenterX/Y**: Center tile coordinates for combat area
- **SafeSpotX/Y**: Safe spot coordinates
- **NpcName**: Single NPC name for add/remove operations

## Global Parameters

### Client Targeting
- **-Ports**: Ports to check (default: 45678,45679,45680,45681,45682)
- **-TargetPorts**: Specific ports to target (if empty, targets all active)
- **-TargetPlugin**: Plugin to target (automatically set based on action)

### Schedule Parameters
- **-ScheduleFile**: Schedule file to load
- **-ScheduleJson**: JSON schedule data

## Usage Examples

### Single Client Operations
```powershell
# Target specific client by port
.\multi-client-control.ps1 -Action start_mining -TargetPorts @(45678)
```

### Multi-Plugin Farm Setup
```powershell
# Set up mining on first two clients
.\multi-client-control.ps1 -Action set_mining_config -TargetPorts @(45678,45679) -MiningOre "IRON" -MiningUseBank $true

# Set up fishing on next two clients
.\multi-client-control.ps1 -Action set_fishing_config -TargetPorts @(45680,45681) -FishType "LOBSTER" -UseBank $true

# Set up combat on last client
.\multi-client-control.ps1 -Action set_fighting_config -TargetPorts @(45682) -AttackableNpcs "Cow,Chicken" -UseFood $true
```

### Advanced Combat Setup
```powershell
# Configure complete combat setup
.\multi-client-control.ps1 -Action set_center_tile -CenterX 3253 -CenterY 3266
.\multi-client-control.ps1 -Action set_safe_spot -SafeSpotX 3250 -SafeSpotY 3260
.\multi-client-control.ps1 -Action set_fighting_config -AttackableNpcs "Hill Giant" -UseFood $true -UsePrayer $true -LootItems "Big bones,Limpwurt root"
.\multi-client-control.ps1 -Action start_fighting
```

### Complete Woodcutting Setup
```powershell
# Configure and start yew tree cutting with banking
.\multi-client-control.ps1 -Action set_woodcutting_config -TreeType "YEW" -ResetOptions "BANK" -ItemsToBank "Yew logs,Bird nest" -WalkBack $true -HopWhenPlayerDetected $true
.\multi-client-control.ps1 -Action start_woodcutting
```

### Resource Gathering Chain
```powershell
# Mine iron ore
.\multi-client-control.ps1 -Action set_mining_config -MiningOre "IRON" -MiningUseBank $true
.\multi-client-control.ps1 -Action start_mining

# Smelt iron bars (after mining completes)
.\multi-client-control.ps1 -Action stop_mining
.\multi-client-control.ps1 -Action set_smelting_config -BarType "IRON"
.\multi-client-control.ps1 -Action start_smelting
```

### Emergency Stop All
```powershell
# Stop all activities across all plugins
.\multi-client-control.ps1 -Action stop_mining
.\multi-client-control.ps1 -Action stop_fishing
.\multi-client-control.ps1 -Action stop_cooking
.\multi-client-control.ps1 -Action stop_smelting
.\multi-client-control.ps1 -Action stop_woodcutting
.\multi-client-control.ps1 -Action stop_fighting
.\multi-client-control.ps1 -Action stop_scheduler
```

## Tips and Best Practices

1. **Always discover clients first** to see what's available
2. **Use specific ports** when you want to target particular clients
3. **Check status regularly** to monitor plugin states
4. **Set configurations before starting** activities
5. **Use quotes for parameters** with spaces or special characters
6. **Test with single client** before applying to multiple clients
7. **Save complex configurations** in scripts for reuse

## Troubleshooting

- **No clients found**: Ensure RuneLite is running with Socket Automation plugin enabled
- **Connection refused**: Check if ports are correct and plugins are loaded
- **Commands not working**: Verify plugin is loaded and compatible
- **Configuration not applying**: Check parameter names and values are correct

## Error Handling

The system provides detailed error messages for:
- Plugin not found or not loaded
- Invalid parameter values
- Connection timeouts
- Command processing failures

Always check the console output for detailed error information and response status.
