local module = {}

-- use _G for global persistence
_G.ChatCommandEvent = _G.ChatCommandEvent or nil

--- Helper to resolve target players
---@param world LuaWorld
---@param sender LuaPlayer
---@param arg string|nil
---@return LuaPlayer[]
local function resolveTargets(world, sender, arg)
    local results = {}
    local allPlayers = world:getPlayers()
    local senderName = sender:getName():lower()

    if not arg or arg == 'me' then
        return { sender }
    elseif arg == 'all' then
        return allPlayers
    elseif arg == 'others' then
        for _, p in ipairs(allPlayers) do
            if p:getName():lower() ~= senderName then
                table.insert(results, p)
            end
        end
        return results
    else
        local argLower = arg:lower()
        for _, p in ipairs(allPlayers) do
            if p:getName():lower() == argLower then
                return { p }
            end
        end
    end

    return {}
end

---@param player LuaPlayer
---@param message string
local function handleChat(player, message)
    if not message:find("^:") then return end

    local args = {}
    for word in message:gmatch("%S+") do
        table.insert(args, word)
    end

    local cmd = args[1]:sub(2):lower()
    local world = player:getWorld()
    local senderName = player:getName()

    local function reply(text, color)
        player:sendTellrawFromTable({ { text = text, color = color or "gray" } })
    end

    if cmd == 'ping' then
        reply('pong!', 'green')

    elseif cmd == 'kill' then
        local targets = resolveTargets(world, player, args[2])
        for _, target in ipairs(targets) do
            target:kill()
        end
        reply('executed kill on ' .. (#targets > 0 and #targets or 'no') .. ' target(s)', 'red')

    elseif cmd == 'fire' then
        local ticks = tonumber(args[3]) or 100
        local targets = resolveTargets(world, player, args[2])
        for _, target in ipairs(targets) do
            target:setFireTicks(ticks)
        end
        reply('set fire for ' .. ticks .. ' ticks on ' .. #targets .. ' target(s)', 'gold')

    elseif cmd == 'heal' then
        local amount = tonumber(args[3]) or 10
        local targets = resolveTargets(world, player, args[2])
        for _, target in ipairs(targets) do
            target:heal(amount)
        end
        reply('healed ' .. amount .. ' HP on ' .. #targets .. ' target(s)', 'green')

    elseif cmd == 'where' then
        local pos = player:getPosition()
        reply(string.format("📍 x=%.1f y=%.1f z=%.1f", pos.x, pos.y, pos.z), 'aqua')

    elseif cmd == 'explode' then
        local targets = resolveTargets(world, player, args[2])
        for _, target in ipairs(targets) do
            local pos = target:getPosition()
            world:createExplosion(pos)
        end
        reply('explosions triggered!', 'dark_red')

    else
        reply('Unknown command: ' .. cmd, 'gray')
    end
end

--- Called when the script is loaded
---@param sender LuaPlayer?
function module.onScriptLoaded(sender)
    print('[chat_commands.lua] loaded!')

    if _G.ChatCommandEvent and type(_G.ChatCommandEvent.disconnect) == 'function' then
        _G.ChatCommandEvent:disconnect()
    end

    _G.ChatCommandEvent = mc.bindToEvent('ServerChat', handleChat)

    if sender then
        sender:sendTellrawFromTable({
            { text = "Chat commands loaded. Try: ", color = "yellow" },
            { text = ":ping, :fire [target] [ticks], :heal [target] [amount]", color = "gold" },
        })
    end
end

--- Called when the world is unloaded
function module.onWorldUnload()
    print('[chat_commands.lua] world unload cleanup')
    if _G.ChatCommandEvent and type(_G.ChatCommandEvent.disconnect) == 'function' then
        _G.ChatCommandEvent:disconnect()
    end
end

--- Called when the game exits
function module.collectGarbage()
    print('[chat_commands.lua] full shutdown cleanup')
    if _G.ChatCommandEvent and type(_G.ChatCommandEvent.disconnect) == 'function' then
        _G.ChatCommandEvent:disconnect()
    end
    _G.ChatCommandEvent = nil
    collectgarbage('collect')
end

return module
