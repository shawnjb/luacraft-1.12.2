local chatCommandEvent = _G.chatCommandEvent
if type(chatCommandEvent) == 'table' and type(chatCommandEvent.disconnect) == 'function' then
    chatCommandEvent:disconnect()
end
chatCommandEvent = mc.bindToEvent("ServerChat", function(event)
    local player = event.player
    local message = event.message

    if not message:find("^:") then 
        return
    end

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
                        player:sendTellraw('{"rawtext":[{"text":"couldn\'t find player: ' .. name .. '"}]}')
                    end
                end
            end
            return result
        end
    end

    local abusiveCommands = {
        kill = function(player, args)
            local targets = resolveTargets(args[2])
            for _, p in ipairs(targets) do
                p:kill()
                p:sendTellraw('{"rawtext":[{"text":"§cYou have been slain by a command."}]}')
            end            
            player:sendTellraw('{"rawtext":[{"text":"§aKilled ' .. #targets .. ' player(s)"}]}')
        end,

        teleport = function(player, args)
            local sources = resolveTargets(args[2])
            local targetPlayer = findPlayerByName(args[3] or "")
            if not targetPlayer then
                player:sendTellraw('{"rawtext":[{"text":"§cCouldn\'t find target player: ' .. (args[3] or "nil") .. '"}]}')
                return
            end
            local targetPos = targetPlayer:getPosition()
            for _, p in ipairs(sources) do
                p:teleport(targetPos)
                p:sendTellraw('{"rawtext":[{"text":"§aYou were teleported to ' .. targetPlayer:getName() .. '"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aTeleported ' .. #sources .. ' player(s) to ' .. targetPlayer:getName() .. '"}]}')
        end,

        explode = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            for _, p in ipairs(targets) do
                world:createExplosion(p:getPosition())
                p:sendTellraw('{"rawtext":[{"text":"§eBoom!"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aExploded ' .. #targets .. ' player(s)"}]}')
        end,

        smite = function(player, args)
            local targets = resolveTargets(args[2])
            for _, p in ipairs(targets) do
                world:strikeLightning(p:getPosition())
                p:sendTellraw('{"rawtext":[{"text":"§cYou were smitten!"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aSmitten ' .. #targets .. ' player(s)"}]}')
        end,

        give = function(player, args)
            local targetArg, itemArg, amountArg
            if args[4] then
                targetArg = args[2]
                itemArg = args[3]
                amountArg = args[4]
            elseif args[3] then
                targetArg = "me"
                itemArg = args[2]
                amountArg = args[3]
            else
                player:sendTellraw('{"rawtext":[{"text":"§cUsage: :give [target] <item> <amount>"}]}')
                return
            end
            local targets = resolveTargets(targetArg)
            local amount = tonumber(amountArg) or 1
            -- Use the new item registry ID (e.g., "minecraft:stone") directly.
            for _, p in ipairs(targets) do
                p:giveItem(itemArg, amount)
                p:sendTellraw('{"rawtext":[{"text":"§aYou received ' .. amount .. ' of ' .. itemArg .. '"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aGave ' .. amount .. 'x ' .. itemArg .. ' to ' .. #targets .. ' player(s)"}]}')
        end,        

        time = function(player, args)
            local timeArg = args[2] and args[2]:lower() or nil
            if not timeArg then
                player:sendTellraw('{"rawtext":[{"text":"§cUsage: :time [day|night|<number>]"}]}')
                return
            end
            if timeArg == "day" then
                world:setTime(0)
                player:sendTellraw('{"rawtext":[{"text":"§aTime set to day (0)"}]}')
            elseif timeArg == "night" then
                world:setTime(13000)
                player:sendTellraw('{"rawtext":[{"text":"§aTime set to night (13000)"}]}')
            else
                local customTime = tonumber(timeArg)
                if customTime and customTime >= 0 and customTime <= 24000 then
                    world:setTime(customTime)
                    player:sendTellraw('{"rawtext":[{"text":"§aTime set to ' .. customTime .. '"}]}')
                else
                    player:sendTellraw('{"rawtext":[{"text":"§cInvalid time value: ' .. tostring(timeArg) .. '"}]}')
                end
            end
        end,

        fire = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local ticks = tonumber(args[3]) or 100
            for _, p in ipairs(targets) do
                p:setFireTicks(ticks)
                p:sendTellraw('{"rawtext":[{"text":"§cYou were set on fire!"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aSet ' .. #targets .. ' player(s) on fire for ' .. ticks .. ' ticks."}]}')
        end,

        summon = function(player, args)
            local entityName = args[2]
            local count = tonumber(args[3]) or 1
            local targetArg = args[4] or "me"
            if not entityName then
                player:sendTellraw('{"rawtext":[{"text":"§cUsage: :summon <entity> <count> <target>"}]}')
                return
            end
            count = math.max(1, math.min(count, 100))
            local targets = resolveTargets(targetArg)
            local total = 0
            for _, p in ipairs(targets) do
                local pos = p:getPosition()
                for i = 1, count do
                    local result = mc.summonEntity(entityName, pos)
                    if result and type(result) == "table" and result.getType then
                        total = total + 1
                    end
                end
                p:sendTellraw('{"rawtext":[{"text":"§aSummoned ' .. count .. ' ' .. entityName .. '(s) at your location"}]}')
            end
            player:sendTellraw('{"rawtext":[{"text":"§aSummoned a total of ' .. total .. ' ' .. entityName .. '(s)"}]}')
        end,

        randomtp = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local radius = tonumber(args[3]) or 1000
            local origin = player:getPosition()
            local function getRandomOffset()
                return math.random(-radius, radius)
            end
            local function getSafeY(x, z)
                for y = 127, 1, -1 do
                    local block = world:getBlockAt(Vector3.new(x, y, z))
                    if block and block:isSolid() then
                        local head = world:getBlockAt(Vector3.new(x, y + 1, z))
                        local above = world:getBlockAt(Vector3.new(x, y + 2, z))
                        if head and not head:isSolid() and above and not above:isSolid() then
                            return y + 1
                        end
                    end
                end
                return nil
            end
            for _, p in ipairs(targets) do
                local dx = getRandomOffset()
                local dz = getRandomOffset()
                local x = origin.x + dx
                local z = origin.z + dz
                local y = getSafeY(x, z)
                if type(y) == 'number' then
                    p:teleport(Vector3.new(x, y, z))
                    p:sendTellraw('{"rawtext":[{"text":"§aYou have been randomly teleported."}]}')
                end
            end
            player:sendTellraw('{"rawtext":[{"text":"§aRandomly teleported ' .. #targets .. ' player(s) within radius ' .. radius .. '"}]}')
        end,

        heal = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local count = 0
            for _, p in ipairs(targets) do
                local max = p:getMaxHealth()
                if type(max) == 'number' then
                    p:setHealth(max)
                    p:sendTellraw('{"rawtext":[{"text":"§aYou have been fully healed."}]}')
                    count = count + 1
                end
            end
            player:sendTellraw('{"rawtext":[{"text":"§aHealed ' .. count .. ' player(s)."}]}')
        end,
    }

    local function extinguishPlayers(player, args)
        local targets = resolveTargets(args[2] or "me")
        for _, p in ipairs(targets) do
            p:setFireTicks(0)
            p:sendTellraw('{"rawtext":[{"text":"§aYou are no longer on fire."}]}')
        end
        player:sendTellraw('{"rawtext":[{"text":"§aExtinguished ' .. #targets .. ' player(s)"}]}')
    end

    abusiveCommands.unfire = extinguishPlayers
    abusiveCommands.nofire = extinguishPlayers

    local nonAbusiveCommands = {
        help = function(player)
            player:sendTellraw('{"rawtext":[{"text":"§cCommand help is limited in legacy chat."}] }')
            player:sendTellraw('{"rawtext":[{"text":"§cRead the source file or ask an op/dev for help."}] }')
        end,
        ping = function(player)
            player:sendTellraw('{"rawtext":[{"text":"§aPong"}]}')
        end,
        coords = function(player)
            local pos = player:getPosition()
            player:sendTellraw('{"rawtext":[{"text":"§aYour position is: x=' .. string.format("%.2f", pos.x) .. " y=" .. string.format("%.2f", pos.y) .. " z=" .. string.format("%.2f", pos.z) .. '"}]}')
        end,
        dimension = function(player)
            player:sendTellraw('{"rawtext":[{"text":"§aYou are in: ' .. player:getDimension() .. '"}]}')
        end,
        who = function(player)
            local players = world:getPlayers()
            local names = {}
            for _, p in ipairs(players) do
                table.insert(names, p:getName())
            end
            player:sendTellraw('{"rawtext":[{"text":"§aOnline players: ' .. table.concat(names, ", ") .. '"}]}')
        end,
        health = function(player)
            player:sendTellraw('{"rawtext":[{"text":"§aYour health: ' .. player:getHealth() .. " / " .. player:getMaxHealth() .. '"}]}')
        end,
        status = function(player)
            local health = player:getHealth()
            local fireTicks = player:getFireTicks() or 0
            player:sendTellraw('{"rawtext":[{"text":"§aHealth: ' .. health .. " / " .. player:getMaxHealth() .. '"}]}')
            player:sendTellraw('{"rawtext":[{"text":"§aOn fire: ' .. (fireTicks > 0 and "yes (" .. fireTicks .. " ticks)" or "no") .. '"}]}')
        end,
        item = function(player)
            local item = player:getItemInHand()
            if item and item.getType then
                player:sendTellraw('{"rawtext":[{"text":"§aYou\'re holding: ' .. item:getType() .. '"}]}')
            else
                player:sendTellraw('{"rawtext":[{"text":"§cYour hand is empty."}]}')
            end
        end,
        inv = function(player, args)
            local material = args[2]
            if not material then
                player:sendTellraw('{"rawtext":[{"text":"§cUsage: :inv <item>"}]}')
                return
            end
            local count = 0
            for i = 0, 35 do
                local item = player:getInventoryItem(i)
                if item and item.getType then
                    if item:getType():lower() == material:lower() then
                        count = count + item:getAmount()
                    end
                end
            end
            player:sendTellraw('{"rawtext":[{"text":"you have ' .. count .. ' of ' .. material .. '"}]}')
        end,
        compass = function(player)
            local dir = player:getLookDirection()
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
            player:sendTellraw('{"rawtext":[{"text":"you are facing ' .. facing .. '"}]}')
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
            player:sendTellraw('{"rawtext":[{"text":"You do not have permission to use the \'' .. command .. '\' command."}]}')
            return
        end
        return abusiveCommands[command](player, args)
    end

    player:sendTellraw('{"rawtext":[{"text":"Unknown command: ' .. command .. '"}]}')
end)
_G.chatCommandEvent = chatCommandEvent