local module = {}

--- This will start when you use `/loadscript example.lua`, which is this script.
--- If you renamed the script and changed its contents, use the new name and make
--- sure you perform cleanup when appropriate.
--- @param sender LuaPlayer?
function module.onScriptLoaded(sender)
    print('SCRIPT LOADED')

    --- @type LuaEvent?
    local ChatEvent = _G.ExampleChatEvent

    if type(ChatEvent) == 'table' and type(ChatEvent.disconnect) == 'function' then
        ChatEvent:disconnect()
    end

    if type(sender) == 'table' and type(sender.sendTellrawFromTable) then
        sender:sendTellrawFromTable({
            {
                text = "Type \'ping\' to test example.lua",
                color = "yellow"
            }
        })
    end

    --- @param player LuaPlayer
    --- @param message string
    _G.ExampleChatEvent = mc.bindToEvent('ServerChat', function(player, message)
        if message:lower() == 'ping' then
            player:sendTellrawFromTable({
                {
                    text = "Pong!",
                    color = "green"
                }
            })
        end
    end)
end

--- Please make sure you collect the garbage at the end of your scripts, clean up everything, as memory leaks WILL
--- occur if the game is left open for too long with running scripts.
function module.onWorldUnload()
    print('DOING WORLD UNLOADING CLEAN-UP')
    local ChatEvent = _G.ExampleChatEvent
    if type(ChatEvent) == 'table' and type(ChatEvent.disconnect) == 'function' then
        ChatEvent:disconnect()
    end
end

--- This will run when the game closes. If you have something persistent going on outside of the world
--- then this method will be very useful for you.
function module.collectGarbage()
    print('GOODBYE! GARBAGE COLLECTING...')
    collectgarbage('collect')
end

return module
