mc.broadcast("Starting safe LuaCraft test script...")

-- Try a harmless chat message
mc.broadcast("Testing /say command...")
mc.execute("/say Hello from LuaCraft!")

-- Set the time to day (safe, visual)
mc.broadcast("Setting time to day...")
mc.execute("/time set day")

-- Gently teleport player to their current position plus a few blocks up
local player = sender or mc.getOnlinePlayers()[1]
if player then
    local name = player:getName()
    local pos = player:getPosition()
    if pos then
        mc.broadcast("Teleporting player " .. name .. " slightly upward...")
        local safeX = math.floor(pos.x)
        local safeY = math.floor(pos.y + 5)
        local safeZ = math.floor(pos.z)
        mc.execute("/tp " .. name .. " " .. safeX .. " " .. safeY .. " " .. safeZ)
    end
end

-- Give a single harmless item
mc.broadcast("Giving player a stick...")
mc.execute("/give @p stick 1")

-- List players
mc.broadcast("Listing players...")
mc.execute("/list")

mc.broadcast("Safe test complete.")
