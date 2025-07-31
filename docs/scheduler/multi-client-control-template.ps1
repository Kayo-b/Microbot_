# Socket Automation Multi-Client Control Template
# Copy this file and customize the variables below for your setup

param(
    [string]$Action = "discover",
    [string]$TargetPlugin = "scheduler",
    [int[]]$Ports = @(45678, 45679, 45680, 45681, 45682),
    [string]$ScheduleFile = "",
    [string]$ScheduleJson = "",
    [int[]]$TargetPorts = @(),
    # Auto Mining parameters
    [string]$MiningOre = "IRON",
    [int]$MiningDistance = 25,
    [bool]$MiningUseBank = $true,
    [string]$MiningItemsToBank = "ore",
    [int]$MiningMaxPlayers = 2,
    # Auto Fishing parameters
    [string]$FishType = "LOBSTER",
    [bool]$UseBank = $true,
    [bool]$UseDepositBox = $false,
    [bool]$UseEchoHarpoon = $false,
    # Auto Cooking parameters
    [string]$CookingActivity = "COOKING",
    [string]$CookingItem = "Raw lobster",
    [string]$CookingLocation = "Rogues' Den",
    [bool]$UseNearestLocation = $false,
    [bool]$DropBurntItems = $true,
    # Auto Smelting parameters
    [string]$BarType = "IRON",
    # Auto Woodcutting parameters
    [string]$TreeType = "TREE",
    [int]$DistanceToStray = 20,
    [bool]$HopWhenPlayerDetected = $false,
    [bool]$FiremakeOnly = $false,
    [string]$ResetOptions = "DROP",
    [string]$ItemsToBank = "logs",
    [string]$WalkBack = "LAST_LOCATION",
    # AIO Fighter parameters
    [string]$AttackableNpcs = "",
    [bool]$UseFood = $true,
    [bool]$UsePrayer = $false,
    [bool]$UseCannon = $false,
    [string]$LootItems = "",
    [int]$CenterX = 0,
    [int]$CenterY = 0,
    [int]$SafeSpotX = 0,
    [int]$SafeSpotY = 0,
    [string]$NpcName = "",
    [switch]$Help
)

# ============================================================================
# CONFIGURATION SECTION - CUSTOMIZE THESE VARIABLES FOR YOUR SETUP
# ============================================================================

# Paths Configuration
$SCHEDULES_DIR = "$env:USERPROFILE\.microbot\schedules"    # Default: %USERPROFILE%\.microbot\schedules
$DEFAULT_SCHEDULE_FILE = "my-default-schedule.json"        # Default schedule file name

# Network Configuration  
$DEFAULT_PORTS = @(45678, 45679, 45680, 45681, 45682)     # Default RuneLite socket ports
$CONNECTION_TIMEOUT = 5000                                  # Socket timeout in milliseconds
$SOCKET_READ_TIMEOUT = 5000                                # Read timeout in milliseconds

# Display Configuration
$SHOW_JSON_COMMANDS = $false                               # Set to $true to show raw JSON commands
$USE_COLORED_OUTPUT = $true                                # Set to $false to disable colored output
$VERBOSE_OUTPUT = $false                                   # Set to $true for detailed output

# Default Plugin Settings
$DEFAULT_MINING_ORE = "IRON"
$DEFAULT_FISH_TYPE = "LOBSTER"  
$DEFAULT_COOKING_ITEM = "Raw lobster"
$DEFAULT_COOKING_LOCATION = "Rogues' Den"
$DEFAULT_BAR_TYPE = "IRON"
$DEFAULT_TREE_TYPE = "TREE"

# ============================================================================
# HELP SECTION
# ============================================================================

if ($Help) {
    Write-Host "=== Socket Automation Multi-Client Control Template ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "SETUP INSTRUCTIONS:" -ForegroundColor Cyan
    Write-Host "1. Copy this template to your desired location"
    Write-Host "2. Customize the CONFIGURATION SECTION above"
    Write-Host "3. Ensure RuneLite is running with Socket Automation plugin"
    Write-Host "4. Run .\your-script.ps1 -Action discover to test connection"
    Write-Host ""
    Write-Host "USAGE:" -ForegroundColor Cyan
    Write-Host "  .\your-script.ps1 [OPTIONS]"
    Write-Host ""
    Write-Host "ACTIONS:" -ForegroundColor Cyan
    Write-Host "  discover              - Find active RuneLite clients"
    Write-Host ""
    Write-Host "  SCHEDULER ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_scheduler       - Start scheduler on all/target clients"
    Write-Host "  stop_scheduler        - Stop scheduler on all/target clients"
    Write-Host "  pause_scheduler       - Pause scheduler on all/target clients"
    Write-Host "  resume_scheduler      - Resume scheduler on all/target clients"
    Write-Host "  load_schedule_file    - Load schedule from file on all/target clients"
    Write-Host "  load_schedule_json    - Load schedule from JSON on all/target clients"
    Write-Host "  loadrun              - Load schedule file and start scheduler (combines load + start)"
    Write-Host "  get_status           - Get scheduler status from all/target clients"
    Write-Host "  list_schedules       - List available schedule files"
    Write-Host "  clear_schedules      - Clear all schedules on all/target clients"
    Write-Host ""
    Write-Host "  AUTO MINING ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_mining          - Start mining on all/target clients"
    Write-Host "  stop_mining           - Stop mining on all/target clients"
    Write-Host "  get_mining_status     - Get mining status from all/target clients"
    Write-Host "  get_mining_config     - Get mining configuration from all/target clients"
    Write-Host "  set_mining_config     - Set mining configuration on all/target clients"
    Write-Host ""
    Write-Host "  AUTO FISHING ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_fishing         - Start fishing on all/target clients"
    Write-Host "  stop_fishing          - Stop fishing on all/target clients"
    Write-Host "  get_fishing_status    - Get fishing status from all/target clients"
    Write-Host "  get_fishing_config    - Get fishing configuration from all/target clients"
    Write-Host "  set_fishing_config    - Set fishing configuration on all/target clients"
    Write-Host ""
    Write-Host "  AUTO COOKING ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_cooking         - Start cooking on all/target clients"
    Write-Host "  stop_cooking          - Stop cooking on all/target clients"
    Write-Host "  get_cooking_status    - Get cooking status from all/target clients"
    Write-Host "  get_cooking_config    - Get cooking configuration from all/target clients"
    Write-Host "  set_cooking_config    - Set cooking configuration on all/target clients"
    Write-Host ""
    Write-Host "  AUTO SMELTING ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_smelting        - Start smelting on all/target clients"
    Write-Host "  stop_smelting         - Stop smelting on all/target clients"
    Write-Host "  get_smelting_status   - Get smelting status from all/target clients"
    Write-Host "  get_smelting_config   - Get smelting configuration from all/target clients"
    Write-Host "  set_smelting_config   - Set smelting configuration on all/target clients"
    Write-Host ""
    Write-Host "  AUTO WOODCUTTING ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_woodcutting     - Start woodcutting on all/target clients"
    Write-Host "  stop_woodcutting      - Stop woodcutting on all/target clients"
    Write-Host "  get_woodcutting_status - Get woodcutting status from all/target clients"
    Write-Host "  get_woodcutting_config - Get woodcutting configuration from all/target clients"
    Write-Host "  set_woodcutting_config - Set woodcutting configuration on all/target clients"
    Write-Host ""
    Write-Host "  AIO FIGHTER ACTIONS:" -ForegroundColor Yellow
    Write-Host "  start_fighting        - Start fighting on all/target clients"
    Write-Host "  stop_fighting         - Stop fighting on all/target clients"
    Write-Host "  get_fighting_status   - Get fighting status from all/target clients"
    Write-Host "  get_fighting_config   - Get fighting configuration from all/target clients"
    Write-Host "  set_fighting_config   - Set fighting configuration on all/target clients"
    Write-Host "  set_center_tile       - Set center combat tile for all/target clients"
    Write-Host "  set_safe_spot         - Set safe spot tile for all/target clients"
    Write-Host "  add_npc               - Add NPC to attack list on all/target clients"
    Write-Host "  remove_npc            - Remove NPC from attack list on all/target clients"
    Write-Host ""
    Write-Host "OPTIONS:" -ForegroundColor Cyan
    Write-Host "  -Ports               - Ports to check (default: configured in script)"
    Write-Host "  -TargetPorts         - Specific ports to target (if empty, targets all active)"
    Write-Host "  -ScheduleFile        - Schedule file to load (for load_schedule_file action)"
    Write-Host "  -ScheduleJson        - JSON schedule data (for load_schedule_json action)"
    Write-Host "  -TargetPlugin        - Plugin to target (default: scheduler)"
    Write-Host ""
    Write-Host "MINING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -MiningOre           - Ore type for mining (IRON, COAL, TIN, etc.)"
    Write-Host "  -MiningDistance      - Distance to stray from start position"
    Write-Host "  -MiningUseBank       - Enable banking (true/false)"
    Write-Host "  -MiningItemsToBank   - Items to bank (comma-separated)"
    Write-Host "  -MiningMaxPlayers    - Max players before world hopping"
    Write-Host ""
    Write-Host "FISHING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -FishType            - Fish type (LOBSTER, SHARK, TUNA, etc.)"
    Write-Host "  -UseBank             - Use banking for fishing (true/false)"
    Write-Host "  -UseDepositBox       - Use deposit box for fishing (true/false)"
    Write-Host "  -UseEchoHarpoon      - Use echo harpoon for fishing (true/false)"
    Write-Host ""
    Write-Host "COOKING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -CookingActivity     - Cooking activity type (COOKING, BURN_BAKING)"
    Write-Host "  -CookingItem         - Item to cook (Raw lobster, Raw shark, etc.)"
    Write-Host "  -CookingLocation     - Cooking location (Rogues' Den, etc.)"
    Write-Host "  -UseNearestLocation  - Use nearest cooking location (true/false)"
    Write-Host "  -DropBurntItems      - Drop burnt items (true/false)"
    Write-Host ""
    Write-Host "SMELTING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -BarType             - Bar type for smelting (IRON, STEEL, MITHRIL, etc.)"
    Write-Host ""
    Write-Host "WOODCUTTING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -TreeType            - Tree type to cut (TREE, OAK, WILLOW, TEAK, MAPLE, MAHOGANY, YEW, MAGIC, REDWOOD)"
    Write-Host "  -DistanceToStray     - Distance to stray (1-10)"
    Write-Host "  -HopWhenPlayerDetected - Hop worlds when player detected (true/false)"
    Write-Host "  -FiremakeOnly        - Only firemaking mode (true/false)"
    Write-Host "  -ResetOptions        - Reset options (DROP, BANK)"
    Write-Host "  -ItemsToBank         - Items to bank (comma-separated)"
    Write-Host "  -WalkBack            - Walk back after banking (true/false)"
    Write-Host ""
    Write-Host "FIGHTING PARAMETERS:" -ForegroundColor Cyan
    Write-Host "  -AttackableNpcs      - NPCs to attack (comma-separated)"
    Write-Host "  -UseFood             - Use food in combat (true/false)"
    Write-Host "  -UsePrayer           - Use prayer in combat (true/false)"
    Write-Host "  -UseCannon           - Use cannon in combat (true/false)"
    Write-Host "  -LootItems           - Items to loot (comma-separated)"
    Write-Host "  -CenterX/Y           - Center tile coordinates for combat"
    Write-Host "  -SafeSpotX/Y         - Safe spot coordinates for combat"
    Write-Host "  -NpcName             - NPC name to add/remove from attack list"
    Write-Host ""
    Write-Host "CUSTOMIZATION:" -ForegroundColor Cyan
    Write-Host "  Edit the CONFIGURATION SECTION at the top of this script to:"
    Write-Host "  - Set default paths and directories"
    Write-Host "  - Configure network timeouts"
    Write-Host "  - Customize default plugin settings"
    Write-Host "  - Enable/disable verbose output"
    Write-Host ""
    Write-Host "EXAMPLES:" -ForegroundColor Cyan
    Write-Host "  # Discover active clients"
    Write-Host "  .\your-script.ps1 -Action discover"
    Write-Host ""
    Write-Host "  # Start mining iron with banking"
    Write-Host "  .\your-script.ps1 -Action set_mining_config -MiningOre IRON -MiningUseBank `$true"
    Write-Host "  .\your-script.ps1 -Action start_mining"
    Write-Host ""
    Write-Host "  # Set up combat area and start fighting"
    Write-Host "  .\your-script.ps1 -Action set_center_tile -CenterX 3200 -CenterY 3200"
    Write-Host "  .\your-script.ps1 -Action set_fighting_config -AttackableNpcs 'Goblin,Orc'"
    Write-Host "  .\your-script.ps1 -Action start_fighting"
    Write-Host ""
    exit 0
}

# ============================================================================
# CORE FUNCTIONS - NO CUSTOMIZATION NEEDED BELOW THIS LINE
# ============================================================================

Write-Host "=== Socket Automation Multi-Client Control ===" -ForegroundColor Green

function Send-SocketCommand {
    param(
        [string]$Command,
        [int]$Port,
        [string]$Description = "",
        [switch]$Silent
    )
    
    try {
        if ($Description -and -not $Silent -and $VERBOSE_OUTPUT) {
            Write-Host "--- $Description (Port $Port) ---" -ForegroundColor Cyan
        }
        
        if (-not $Silent -and $SHOW_JSON_COMMANDS) {
            Write-Host "Sending to $Port : $Command" -ForegroundColor Yellow
        }
        
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $tcpClient.ReceiveTimeout = $SOCKET_READ_TIMEOUT
        $tcpClient.SendTimeout = $CONNECTION_TIMEOUT
        $tcpClient.Connect("localhost", $Port)
        
        $stream = $tcpClient.GetStream()
        $writer = New-Object System.IO.StreamWriter($stream)
        $reader = New-Object System.IO.StreamReader($stream)
        
        $writer.WriteLine($Command)
        $writer.Flush()
        
        Start-Sleep -Milliseconds 100
        
        $response = $reader.ReadLine()
        if (-not $Silent -and $VERBOSE_OUTPUT) {
            Write-Host "Response: $response" -ForegroundColor Green
        }
        
        $writer.Close()
        $reader.Close()
        $stream.Close()
        $tcpClient.Close()
        
        return $response
    }
    catch {
        if (-not $Silent) {
            Write-Host "Error on port $Port : $($_.Exception.Message)" -ForegroundColor Red
        }
        return $null
    }
}

function Find-ActiveClients {
    param([int[]]$PortRange)
    
    Write-Host "=== Discovering Active RuneLite Clients ===" -ForegroundColor Magenta
    $activeClients = @()
    
    foreach ($port in $PortRange) {
        $response = Send-SocketCommand -Command '{"action": "get_socket_info"}' -Port $port -Silent
        if ($response) {
            try {
                $data = $response | ConvertFrom-Json
                if ($data.success) {
                    $instanceResponse = Send-SocketCommand -Command '{"action": "get_current_instance"}' -Port $port -Silent
                    $instanceData = $instanceResponse | ConvertFrom-Json
                    
                    $client = @{
                        Port = $port
                        InstanceId = $instanceData.data.instanceId
                        ActiveConnections = $data.data.activeConnections
                        MaxConnections = $data.data.maxConnections
                        IsResponding = $true
                    }
                    $activeClients += $client
                    Write-Host "Found client on port $port - Instance: $($client.InstanceId)" -ForegroundColor Green
                }
            } catch {
                Write-Host "Port $port responded but with invalid data" -ForegroundColor Yellow
            }
        }
    }
    
    if ($activeClients.Count -eq 0) {
        Write-Host "No active RuneLite clients found!" -ForegroundColor Red
        Write-Host "Make sure RuneLite is running with Socket Automation plugin enabled" -ForegroundColor Yellow
        Write-Host "Default ports checked: $($PortRange -join ', ')" -ForegroundColor Yellow
    } else {
        Write-Host "Found $($activeClients.Count) active clients" -ForegroundColor Green
    }
    
    return $activeClients
}

function Invoke-PluginAction {
    param(
        [string]$ActionName,
        [array]$Clients,
        [string]$PluginName = "scheduler",
        [string]$ScheduleFile = $DEFAULT_SCHEDULE_FILE,
        [string]$ScheduleJson = "",
        # Mining parameters
        [string]$Ore = $DEFAULT_MINING_ORE,
        [int]$Distance = 25,
        [bool]$UseBank = $true,
        [string]$ItemsToBank = "ore",
        [int]$MaxPlayers = 2,
        # Fishing parameters
        [string]$FishType = $DEFAULT_FISH_TYPE,
        [bool]$UseDepositBox = $false,
        [bool]$UseEchoHarpoon = $false,
        # Cooking parameters
        [string]$CookingActivity = "COOKING",
        [string]$CookingItem = $DEFAULT_COOKING_ITEM,
        [string]$CookingLocation = $DEFAULT_COOKING_LOCATION,
        [bool]$UseNearestLocation = $false,
        [bool]$DropBurntItems = $true,
        # Smelting parameters
        [string]$BarType = $DEFAULT_BAR_TYPE,
        # Woodcutting parameters
        [string]$TreeType = $DEFAULT_TREE_TYPE,
        [int]$DistanceToStray = 20,
        [bool]$HopWhenPlayerDetected = $false,
        [bool]$FiremakeOnly = $false,
        [string]$ResetOptions = "DROP",
        [string]$ItemsToBank_WC = "logs",
        [string]$WalkBack = "LAST_LOCATION",
        # Fighting parameters
        [string]$AttackableNpcs = "",
        [bool]$UseFood = $true,
        [bool]$UsePrayer = $false,
        [bool]$UseCannon = $false,
        [string]$LootItems = "",
        [int]$CenterX = 0,
        [int]$CenterY = 0,
        [int]$SafeSpotX = 0,
        [int]$SafeSpotY = 0,
        [string]$NpcName = ""
    )
    
    if ($Clients.Count -eq 0) {
        Write-Host "No clients available for action: $ActionName" -ForegroundColor Red
        return
    }
    
    Write-Host "=== Executing Action: $ActionName on Plugin: $PluginName ===" -ForegroundColor Magenta
    
    foreach ($client in $Clients) {
        $commandObj = @{
            plugin = $PluginName
        }
        
        # Map action names to actual plugin actions
        switch ($ActionName) {
            # Scheduler actions
            { $_ -in @("start_scheduler", "stop_scheduler", "pause_scheduler", "resume_scheduler", "get_status", "list_schedules", "clear_schedules") } {
                $commandObj.action = $ActionName.Replace("_scheduler", "").Replace("get_status", "get_status").Replace("list_schedules", "list_schedules").Replace("clear_schedules", "clear_schedules")
            }
            
            # Mining actions
            "start_mining" { $commandObj.action = "start_mining" }
            "stop_mining" { $commandObj.action = "stop_mining" }
            "get_mining_status" { $commandObj.action = "get_status" }
            "get_mining_config" { $commandObj.action = "get_config" }
            "set_mining_config" { 
                $commandObj.action = "set_config"
                $commandObj.ore = $Ore
                $commandObj.distance = $Distance
                $commandObj.use_bank = $UseBank
                $commandObj.items_to_bank = $ItemsToBank
                $commandObj.max_players = $MaxPlayers
            }
            
            # Fishing actions
            "start_fishing" { $commandObj.action = "start_fishing" }
            "stop_fishing" { $commandObj.action = "stop_fishing" }
            "get_fishing_status" { $commandObj.action = "get_status" }
            "get_fishing_config" { $commandObj.action = "get_config" }
            "set_fishing_config" { 
                $commandObj.action = "set_config"
                $commandObj.fish = $FishType
                $commandObj.use_bank = $UseBank
                $commandObj.use_deposit_box = $UseDepositBox
                $commandObj.use_echo_harpoon = $UseEchoHarpoon
            }
            
            # Cooking actions
            "start_cooking" { $commandObj.action = "start_cooking" }
            "stop_cooking" { $commandObj.action = "stop_cooking" }
            "get_cooking_status" { $commandObj.action = "get_status" }
            "get_cooking_config" { $commandObj.action = "get_config" }
            "set_cooking_config" { 
                $commandObj.action = "set_config"
                $commandObj.cooking_activity = $CookingActivity
                $commandObj.cooking_item = $CookingItem
                $commandObj.cooking_location = $CookingLocation
                $commandObj.use_nearest_location = $UseNearestLocation
                $commandObj.drop_burnt_items = $DropBurntItems
            }
            
            # Smelting actions
            "start_smelting" { $commandObj.action = "start_smelting" }
            "stop_smelting" { $commandObj.action = "stop_smelting" }
            "get_smelting_status" { $commandObj.action = "get_status" }
            "get_smelting_config" { $commandObj.action = "get_config" }
            "set_smelting_config" { 
                $commandObj.action = "set_config"
                $commandObj.selected_bar_type = $BarType
            }
            
            # Woodcutting actions
            "start_woodcutting" { $commandObj.action = "start_woodcutting" }
            "stop_woodcutting" { $commandObj.action = "stop_woodcutting" }
            "get_woodcutting_status" { $commandObj.action = "get_status" }
            "get_woodcutting_config" { $commandObj.action = "get_config" }
            "set_woodcutting_config" { 
                $commandObj.action = "set_config"
                $commandObj.tree_type = $TreeType
                $commandObj.distance_to_stray = $DistanceToStray
                $commandObj.hop_when_player_detected = $HopWhenPlayerDetected
                $commandObj.firemake_only = $FiremakeOnly
                $commandObj.reset_options = $ResetOptions
                $commandObj.items_to_bank = $ItemsToBank_WC
                $commandObj.walk_back = $WalkBack
            }
            
            # Fighting actions
            "start_fighting" { $commandObj.action = "start_fighting" }
            "stop_fighting" { $commandObj.action = "stop_fighting" }
            "get_fighting_status" { $commandObj.action = "get_status" }
            "get_fighting_config" { $commandObj.action = "get_config" }
            "set_fighting_config" { 
                $commandObj.action = "set_config"
                $commandObj.attackable_npcs = $AttackableNpcs
                $commandObj.use_food = $UseFood
                $commandObj.use_prayer = $UsePrayer
                $commandObj.use_cannon = $UseCannon
                $commandObj.loot_items = $LootItems
            }
            "set_center_tile" { 
                $commandObj.action = "set_center_tile"
                $commandObj.x = $CenterX
                $commandObj.y = $CenterY
                $commandObj.plane = 0
            }
            "set_safe_spot" { 
                $commandObj.action = "set_safe_spot"
                $commandObj.x = $SafeSpotX
                $commandObj.y = $SafeSpotY
                $commandObj.plane = 0
            }
            "add_npc" { 
                $commandObj.action = "add_npc"
                $commandObj.npc_name = $NpcName
            }
            "remove_npc" { 
                $commandObj.action = "remove_npc"
                $commandObj.npc_name = $NpcName
            }
            
            default {
                $commandObj.action = $ActionName
            }
        }
        
        # Add additional parameters based on action
        switch ($ActionName) {
            "load_schedule_file" {
                if (-not $ScheduleFile) {
                    Write-Host "Error: ScheduleFile parameter required for load_schedule_file action" -ForegroundColor Red
                    return
                }
                $commandObj.filename = $ScheduleFile
            }
            "load_schedule_json" {
                if (-not $ScheduleJson) {
                    Write-Host "Error: ScheduleJson parameter required for load_schedule_json action" -ForegroundColor Red
                    return
                }
                try {
                    $parsedJson = $ScheduleJson | ConvertFrom-Json
                    $commandObj.schedule = $parsedJson
                } catch {
                    Write-Host "Error: Invalid JSON in ScheduleJson parameter" -ForegroundColor Red
                    return
                }
            }
        }
        
        $command = $commandObj | ConvertTo-Json -Depth 10 -Compress
        $response = Send-SocketCommand -Command $command -Port $client.Port -Description "$ActionName on $($client.InstanceId)"
        
        if ($response) {
            try {
                $data = $response | ConvertFrom-Json
                $statusColor = if ($data.success) {'Green'} else {'Red'}
                $status = if ($data.success) {'SUCCESS'} else {'FAILED'}
                Write-Host "[$status] Client $($client.InstanceId) (port $($client.Port)): $($data.message)" -ForegroundColor $statusColor
                
                # Show additional data if available and verbose output is enabled
                if ($data.data -and $VERBOSE_OUTPUT) {
                    if ($ActionName -in @("get_status", "get_mining_status")) {
                        if ($data.data.status) {
                            Write-Host "  Status: $($data.data.status)" -ForegroundColor Gray
                        }
                        if ($data.data.activeSchedules) {
                            Write-Host "  Active Schedules: $($data.data.activeSchedules)" -ForegroundColor Gray
                        }
                    }
                    elseif ($ActionName -eq "list_schedules") {
                        Write-Host "  Available Files: $($data.data.files -join ', ')" -ForegroundColor Gray
                    }
                    elseif ($ActionName -like "*get*config*") {
                        Write-Host "  Configuration: $($data.data | ConvertTo-Json -Compress)" -ForegroundColor Gray
                    }
                }
            } catch {
                Write-Host "[ERROR] Client $($client.InstanceId) (port $($client.Port)): Invalid response format" -ForegroundColor Red
            }
        } else {
            Write-Host "[ERROR] Client $($client.InstanceId) (port $($client.Port)): No response" -ForegroundColor Red
        }
    }
}

function Show-ClientSummary {
    param([array]$Clients)
    
    Write-Host "=== Client Summary ===" -ForegroundColor Magenta
    Write-Host "Total Clients: $($Clients.Count)" -ForegroundColor White
    
    foreach ($client in $Clients) {
        Write-Host "Client $($client.InstanceId):" -ForegroundColor Cyan
        Write-Host "  Port: $($client.Port)" -ForegroundColor Gray
        Write-Host "  Connections: $($client.ActiveConnections)/$($client.MaxConnections)" -ForegroundColor Gray
        $statusColor = if ($client.IsResponding) {'Green'} else {'Red'}
        $statusText = if ($client.IsResponding) {'Responding'} else {'Not Responding'}
        Write-Host "  Status: $statusText" -ForegroundColor $statusColor
    }
}

# ============================================================================
# MAIN EXECUTION
# ============================================================================

# Use configured ports if none specified
if ($Ports.Count -eq 0) {
    $Ports = $DEFAULT_PORTS
}

Write-Host "Action: $Action" -ForegroundColor Yellow
Write-Host "Target Plugin: $TargetPlugin" -ForegroundColor Yellow

# Discover active clients
$allClients = Find-ActiveClients -PortRange $Ports

if ($allClients.Count -eq 0) {
    Write-Host "No active clients found. Exiting." -ForegroundColor Red
    Write-Host "Check configuration section at top of script for port settings." -ForegroundColor Yellow
    exit 1
}

# Filter target clients if specific ports are specified
$targetClients = if ($TargetPorts.Count -gt 0) {
    $allClients | Where-Object { $_.Port -in $TargetPorts }
} else {
    $allClients
}

if ($targetClients.Count -eq 0 -and $TargetPorts.Count -gt 0) {
    Write-Host "No active clients found on target ports: $($TargetPorts -join ', ')" -ForegroundColor Red
    Write-Host "Available ports: $($allClients.Port -join ', ')" -ForegroundColor Yellow
    exit 1
}

Write-Host "Targeting $($targetClients.Count) client(s) on ports: $($targetClients.Port -join ', ')" -ForegroundColor Green

# Execute the requested action
switch ($Action.ToLower()) {
    "discover" {
        Show-ClientSummary -Clients $allClients
    }
    { $_ -in @("start_scheduler", "stop_scheduler", "pause_scheduler", "resume_scheduler", "get_status", "list_schedules", "clear_schedules") } {
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin
    }
    { $_ -in @("start_mining", "stop_mining", "get_mining_status", "get_mining_config", "set_mining_config") } {
        if ($TargetPlugin -ne "auto_mining") {
            Write-Host "Setting TargetPlugin to 'auto_mining' for mining action" -ForegroundColor Yellow
            $TargetPlugin = "auto_mining"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -Ore $MiningOre -Distance $MiningDistance -UseBank $MiningUseBank -ItemsToBank $MiningItemsToBank -MaxPlayers $MiningMaxPlayers
    }
    { $_ -in @("start_fishing", "stop_fishing", "get_fishing_status", "get_fishing_config", "set_fishing_config") } {
        if ($TargetPlugin -ne "auto_fish") {
            Write-Host "Setting TargetPlugin to 'auto_fish' for fishing action" -ForegroundColor Yellow
            $TargetPlugin = "auto_fish"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -FishType $FishType -UseBank $UseBank -UseDepositBox $UseDepositBox -UseEchoHarpoon $UseEchoHarpoon
    }
    { $_ -in @("start_cooking", "stop_cooking", "get_cooking_status", "get_cooking_config", "set_cooking_config") } {
        if ($TargetPlugin -ne "auto_cooking") {
            Write-Host "Setting TargetPlugin to 'auto_cooking' for cooking action" -ForegroundColor Yellow
            $TargetPlugin = "auto_cooking"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -CookingActivity $CookingActivity -CookingItem $CookingItem -CookingLocation $CookingLocation -UseNearestLocation $UseNearestLocation -DropBurntItems $DropBurntItems
    }
    { $_ -in @("start_smelting", "stop_smelting", "get_smelting_status", "get_smelting_config", "set_smelting_config") } {
        if ($TargetPlugin -ne "auto_smelting") {
            Write-Host "Setting TargetPlugin to 'auto_smelting' for smelting action" -ForegroundColor Yellow
            $TargetPlugin = "auto_smelting"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -BarType $BarType
    }
    { $_ -in @("start_woodcutting", "stop_woodcutting", "get_woodcutting_status", "get_woodcutting_config", "set_woodcutting_config") } {
        if ($TargetPlugin -ne "auto_woodcutting") {
            Write-Host "Setting TargetPlugin to 'auto_woodcutting' for woodcutting action" -ForegroundColor Yellow
            $TargetPlugin = "auto_woodcutting"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -TreeType $TreeType -DistanceToStray $DistanceToStray -HopWhenPlayerDetected $HopWhenPlayerDetected -FiremakeOnly $FiremakeOnly -ResetOptions $ResetOptions -ItemsToBank_WC $ItemsToBank -WalkBack $WalkBack
    }
    { $_ -in @("start_fighting", "stop_fighting", "get_fighting_status", "get_fighting_config", "set_fighting_config", "set_center_tile", "set_safe_spot", "add_npc", "remove_npc") } {
        if ($TargetPlugin -ne "aio_fighter") {
            Write-Host "Setting TargetPlugin to 'aio_fighter' for fighting action" -ForegroundColor Yellow
            $TargetPlugin = "aio_fighter"
        }
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -AttackableNpcs $AttackableNpcs -UseFood $UseFood -UsePrayer $UsePrayer -UseCannon $UseCannon -LootItems $LootItems -CenterX $CenterX -CenterY $CenterY -SafeSpotX $SafeSpotX -SafeSpotY $SafeSpotY -NpcName $NpcName
    }
    "load_schedule_file" {
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -ScheduleFile $ScheduleFile
    }
    "load_schedule_json" {
        Invoke-PluginAction -ActionName $Action -Clients $targetClients -PluginName $TargetPlugin -ScheduleJson $ScheduleJson
    }
    "loadrun" {
        Write-Host "=== Load and Run Sequence ===" -ForegroundColor Magenta
        Write-Host "Step 1: Loading schedule file..." -ForegroundColor Yellow
        Invoke-PluginAction -ActionName "load_schedule_file" -Clients $targetClients -PluginName $TargetPlugin -ScheduleFile $ScheduleFile
        
        Start-Sleep -Seconds 2
        
        Write-Host "Step 2: Starting scheduler..." -ForegroundColor Yellow
        Invoke-PluginAction -ActionName "start_scheduler" -Clients $targetClients -PluginName $TargetPlugin
    }
    default {
        Write-Host "Unknown action: $Action" -ForegroundColor Red
        Write-Host "Use -Help to see available actions" -ForegroundColor Yellow
        exit 1
    }
}

Write-Host "=== Operation Complete ===" -ForegroundColor Green
