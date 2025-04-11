--- @type LuaPlayer
local player = sender or mc.getOnlinePlayers()[1]

local function tell(msg)
    if player and player.sendTellrawFromTable then
        player:sendTellrawFromTable({
            { text = "§7[§dLuaCraft§7] " },
            { text = msg }
        })
    else
        mc.broadcast("[LuaCraft] " .. msg)
    end
end

tell("Starting safe LuaCraft test script...")

-- Try a harmless chat message
tell("Testing /say command...")
mc.execute("/say Hello from LuaCraft!")

-- Set the time to day (safe, visual)
tell("Setting time to day...")
mc.execute("/time set day")

-- Gently teleport player to their current position plus a few blocks up
if player then
    local name = player:getName()
    local pos = player:getPosition()
    if pos then
        tell("Teleporting player " .. name .. " slightly upward...")
        local safeX = math.floor(pos.x)
        local safeY = math.floor(pos.y + 5)
        local safeZ = math.floor(pos.z)
        mc.execute("/tp " .. name .. " " .. safeX .. " " .. safeY .. " " .. safeZ)
    end
end

-- Give a single harmless item
tell("Giving player a stick...")
mc.execute("/give @p stick 1")

-- List players
tell("Listing players...")
mc.execute("/list")

tell("Safe test complete.")
