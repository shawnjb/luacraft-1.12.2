local module = {}

---@param sender LuaPlayer?
function module.onScriptLoaded(sender)
    print('[ItemStack Test] Script Loaded')

    --- Cleanup previous events if reloaded
    local event = _G.TestItemStackEvent
    if type(event) == 'table' and type(event.disconnect) == 'function' then
        event:disconnect()
    end

    if sender then
        sender:sendTellrawFromTable({
            { text = "Type ",                         color = "yellow" },
            { text = "teststack",                     color = "aqua",  bold = true },
            { text = " to run the LuaItemStack test", color = "yellow" }
        })
    end

    --- @param player LuaPlayer
    --- @param message string
    _G.TestItemStackEvent = mc.bindToEvent('ServerChat', function(player, message)
        if message:lower() == 'teststack' then
            local stack = mc.createItemStack('minecraft:diamond_sword', 1)
            if not stack then
                player:sendTellrawFromTable({ text = '[!] Failed to create diamond sword', color = 'red' })
                return
            end

            stack:setDisplayName('§bEpic Test Sword')
            stack:addEnchantment('minecraft:sharpness', 5)
            stack:setLore({ 'Line 1 of lore', 'Line 2 of lore' })

            local nbt = stack:getNBTTag()
            if nbt then
                nbt.CustomNBTFlag = true
                stack:setNBTTag(nbt)
            end

            local clone = stack:clone()
            local success = player:addItem(clone)

            player:sendTellrawFromTable({
                { text = '[✓] Test stack given. Check your inventory.', color = success and 'green' or 'red' }
            })
        end
    end)
end

function module.onWorldUnload()
    local event = _G.TestItemStackEvent
    if type(event) == 'table' and type(event.disconnect) == 'function' then
        event:disconnect()
    end
end

function module.collectGarbage()
    print('[ItemStack Test] Collecting garbage')
    collectgarbage('collect')
end

return module
