--- @type LuaEvent
local chatCommandEvent = _G.chatCommandEvent
if type(chatCommandEvent) == 'table' and type(chatCommandEvent.disconnect) == 'function' then
    chatCommandEvent:disconnect()
end

chatCommandEvent = mc.bindToEvent("ServerChat", function(event)
    --- @type LuaPlayer
    local player = event.player
    --- @type string
    local message = event.message

    if not message:find("^:") then return end

    local args = {}
    for word in message:gmatch("%S+") do
        table.insert(args, word)
    end

    local rawCommand = args[1]:sub(2):lower()
    --- @type LuaWorld
    local world = player:getWorld()
    local senderName = player:getName():lower()

    local function findPlayerByName(name)
        name = name:lower()
        for _, p in ipairs(world:getPlayers()) do
            if p:getName():lower() == name then
                return p
            end
        end
        return nil
    end

    local function resolveTargets(who)
        local result = {}
        local seen = {}
        local allPlayers = world:getPlayers()
        who = (who and who:lower()) or "me"

        if who == "me" then
            return { player }
        elseif who == "all" then
            return allPlayers
        elseif who == "others" then
            for _, p in ipairs(allPlayers) do
                if p:getName():lower() ~= senderName then
                    table.insert(result, p)
                end
            end
            return result
        else
            for name in who:gmatch("[^,]+") do
                name = name:match("^%s*(.-)%s*$"):lower()
                if not seen[name] then
                    local p = findPlayerByName(name)
                    if p then
                        table.insert(result, p)
                        seen[name] = true
                    else
                        player:sendTellrawFromTable({ { text = "couldn't find player: " .. name } })
                    end
                end
            end
            return result
        end
    end

    local function sendSuccess(p, msg)
        p:sendTellrawFromTable({ { text = "§a" .. msg } })
    end

    local function sendError(p, msg)
        p:sendTellrawFromTable({ { text = "§c" .. msg } })
    end

    local function sendToTarget(p, msg)
        p:sendTellrawFromTable({ { text = msg } })
    end

    local abusiveCommands = {
        kill = function(p, args)
            local targets = resolveTargets(args[2])
            for _, t in ipairs(targets) do
                t:kill()
                sendToTarget(t, "§cYou have been slain by a command.")
            end
            sendSuccess(p, "Killed " .. #targets .. " player(s).")
        end,

        teleport = function(p, args)
            local sources = resolveTargets(args[2])
            local destination = findPlayerByName(args[3] or "")
            if not destination then
                sendError(p, "Couldn't find target player: " .. (args[3] or "nil"))
                return
            end
            local pos = destination:getPosition()
            for _, t in ipairs(sources) do
                t:setPosition(pos)
                sendToTarget(t, "§aYou were teleported to " .. destination:getName())
            end
            sendSuccess(p, "Teleported " .. #sources .. " player(s) to " .. destination:getName())
        end,

        explode = function(p, args)
            local targets = resolveTargets(args[2] or "me")
            for _, t in ipairs(targets) do
                p:getWorld():createExplosion(t:getPosition())
                sendToTarget(t, "§eBoom!")
            end
            sendSuccess(p, "Exploded " .. #targets .. " player(s).")
        end,

        smite = function(p, args)
            local targets = resolveTargets(args[2])
            for _, t in ipairs(targets) do
                p:getWorld():strikeLightning(t:getPosition())
                sendToTarget(t, "§cYou were smitten!")
            end
            sendSuccess(p, "Smitten " .. #targets .. " player(s).")
        end,

        fire = function(p, args)
            local targets = resolveTargets(args[2] or "me")
            local ticks = tonumber(args[3]) or 100
            for _, t in ipairs(targets) do
                t:setFireTicks(ticks)
                sendToTarget(t, "§cYou were set on fire!")
            end
            sendSuccess(p, "Set " .. #targets .. " player(s) on fire for " .. ticks .. " ticks.")
        end,

        unfire = function(p, args)
            local targets = resolveTargets(args[2] or "me")
            for _, t in ipairs(targets) do
                t:setFireTicks(0)
                sendToTarget(t, "§aYou are no longer on fire.")
            end
            sendSuccess(p, "Extinguished " .. #targets .. " player(s).")
        end,

        heal = function(p, args)
            local targets = resolveTargets(args[2] or "me")
            local healed = 0
            for _, t in ipairs(targets) do
                local max = t:getMaxHealth()
                if type(max) == "number" then
                    t:setHealth(max)
                    sendToTarget(t, "§aYou have been fully healed.")
                    healed = healed + 1
                end
            end
            sendSuccess(p, "Healed " .. healed .. " player(s).")
        end,
    }

    abusiveCommands.nofire = abusiveCommands.unfire

    local nonAbusiveCommands = {
        help = function(p)
            sendError(p, "Command help is limited in legacy chat.")
            sendError(p, "Read the source file or ask an op/dev for help.")
        end,

        ping = function(p)
            sendSuccess(p, "Pong")
        end,

        coords = function(p)
            local pos = p:getPosition()
            sendSuccess(p, string.format("Your position is: x=%.2f y=%.2f z=%.2f", pos.x, pos.y, pos.z))
        end,

        dimension = function(p)
            sendSuccess(p, "You are in: " .. p:getDimension())
        end,

        who = function(p)
            local names = {}
            for _, pl in ipairs(world:getPlayers()) do
                table.insert(names, pl:getName())
            end
            sendSuccess(p, "Online players: " .. table.concat(names, ", "))
        end,

        health = function(p)
            sendSuccess(p, "Your health: " .. p:getHealth() .. " / " .. p:getMaxHealth())
        end,

        status = function(p)
            local fireTicks = p:getFireTicks() or 0
            sendSuccess(p, "Health: " .. p:getHealth() .. " / " .. p:getMaxHealth())
            sendSuccess(p, "On fire: " .. (fireTicks > 0 and "yes (" .. fireTicks .. " ticks)" or "no"))
        end,

        item = function(p)
            local item = p:getItemInHand()
            if item and item.getType then
                sendSuccess(p, "You're holding: " .. item:getType())
            else
                sendError(p, "Your hand is empty.")
            end
        end,

        inv = function(p, args)
            local material = args[2]
            if not material then
                sendError(p, "Usage: :inv <item>")
                return
            end
            local count = 0
            for i = 0, 35 do
                local item = p:getInventoryItem(i)
                if item and item.getType then
                    if item:getType():lower() == material:lower() then
                        count = count + item:getAmount()
                    end
                end
            end
            sendSuccess(p, "You have " .. count .. " of " .. material)
        end,

        compass = function(p)
            local dir = p:getLookDirection()
            if type(dir) == 'table' and type(dir.z) == 'number' and type(dir.x) == 'number' then
                local angle = math.atan2(dir.z, dir.x) * (180 / math.pi)
                if angle < 0 then angle = angle + 360 end
                local facing = "unknown"
                if angle >= 45 and angle < 135 then
                    facing = "south"
                elseif angle >= 135 and angle < 225 then
                    facing = "west"
                elseif angle >= 225 and angle < 315 then
                    facing = "north"
                else
                    facing = "east"
                end
                sendSuccess(p, "You are facing " .. facing)
            end
        end,
    }

    local aliases = {
        tp = "teleport",
    }

    local command = aliases[rawCommand] or rawCommand

    if nonAbusiveCommands[command] then
        return nonAbusiveCommands[command](player, args)
    end

    if abusiveCommands[command] then
        if not player:isOp() then
            sendError(player, "You do not have permission to use the '" .. command .. "' command.")
            return
        end
        return abusiveCommands[command](player, args)
    end

    sendError(player, "Unknown command: " .. command)
end)

_G.chatCommandEvent = chatCommandEvent
