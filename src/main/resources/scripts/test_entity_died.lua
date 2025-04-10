-- listen to entity death events
local listener = mc.bindToEvent("LivingDeath", function(event)
    local entity = event:getEntity()          -- fictional method to get LuaEntity
    local source = event:getSourceEntity()    -- fictional method to get attacker

    if entity:isPlayer() then
        mc.broadcast("Player " .. entity:getName() .. " died!")
    else
        mc.broadcast("Mob " .. entity:getType() .. " was slain.")
    end

    if source ~= nil then
        mc.broadcast("Killed by: " .. source:getName())
    end
end)

-- disconnect after 30 seconds
mc.delay(30, function()
    listener:disconnect()
    mc.broadcast("Entity death listener removed.")
end)
