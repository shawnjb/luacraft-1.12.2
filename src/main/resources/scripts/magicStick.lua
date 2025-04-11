local module = {}

_G.MagicStickEvent = _G.MagicStickEvent or nil
_G.MagicStickStack = _G.MagicStickStack or nil

local function onInteract(player, item, block, face)
    local held = item
    local stick = _G.MagicStickStack

    -- If the held item or saved stack is invalid, do nothing
    if not held or not stick then return end

    if held:equals(stick) then
        local targetBlock = player:getTargetBlock(50)
        if targetBlock then
            local pos = targetBlock:getPosition()
            player:getWorld():createExplosion(pos)
        else
            player:sendTellrawFromTable({
                { text = "no target block in sight", color = "gray" }
            })
        end
    end

    -- If player no longer has the original stick, stop the listener
    local stillExists = false
    for i = 0, 35 do
        local invItem = player:getInventoryItem(i)
        if invItem and invItem:equals(stick) then
            stillExists = true
            break
        end
    end

    if not stillExists then
        player:sendTellrawFromTable({
            { text = "your Boom Stick is gone. listener stopped.", color = "red" }
        })
        if _G.MagicStickEvent and type(_G.MagicStickEvent.disconnect) == "function" then
            _G.MagicStickEvent:disconnect()
        end
        _G.MagicStickEvent = nil
        _G.MagicStickStack = nil
    end
end

---@param sender LuaPlayer?
function module.onScriptLoaded(sender)
    if sender then
        local stack = mc.createItemStack("minecraft:stick", 1)
        stack:setDisplayName("Boom Stick")
        sender:giveItemStack(stack)

        _G.MagicStickStack = stack

        sender:sendTellrawFromTable({
            { text = "you have received the ", color = "green" },
            { text = "Boom Stick", color = "red", bold = true },
            { text = "! right-click a block to detonate.", color = "gold" }
        })
    end

    if _G.MagicStickEvent and type(_G.MagicStickEvent.disconnect) == "function" then
        _G.MagicStickEvent:disconnect()
    end

    _G.MagicStickEvent = mc.bindToEvent("PlayerInteract", onInteract)
end

function module.onWorldUnload()
    if _G.MagicStickEvent and type(_G.MagicStickEvent.disconnect) == "function" then
        _G.MagicStickEvent:disconnect()
    end
end

function module.collectGarbage()
    module.onWorldUnload()
    _G.MagicStickEvent = nil
    _G.MagicStickStack = nil
end

return module
