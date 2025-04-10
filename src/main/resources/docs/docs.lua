---@meta
-- Auto-generated LuaCATS documentation
-- Generated by LuaDocGenerator for LuaCraft

---@class mc
mc = {}

---@class LuaEntity
local LuaEntity = {}

---@class LuaPlayer
local LuaPlayer = {}

---@class LuaWorld
local LuaWorld = {}

---@class LuaMaterial
local LuaMaterial = {}

---@class LuaItemStack
local LuaItemStack = {}

---@class LuaBlock
local LuaBlock = {}

---@class LuaEvent
local LuaEvent = {}

---Returns the number of Lua scripts currently loaded.
---@return number @The script count
function mc.getLoadedScriptCount() end

---The player who triggered the command or event (if available).
---@return LuaPlayer @The sender player object or nil
function mc.sender() end

---Creates a table with x, y, and z fields.
---@param x number
---@param y number
---@param z number
---@return table @A table with x, y, and z values
function mc.vector(x, y, z) end

---Broadcasts a message to all players.
---@param message string
function mc.broadcast(message) end

---Executes a server-side command.
---@param command string
function mc.execute(command) end

---Binds a Lua function to a named event.
---@param eventName string
---@param callback function
---@return LuaEvent @The event binding handle
function mc.bindToEvent(eventName, callback) end

---Returns a list of all currently online players.
---@return LuaPlayer[] @The list of players currently online
function mc.getOnlinePlayers() end

---Gets a player by name, or nil if they are not online.
---@param name string
---@return LuaPlayer @The player object, or nil if not found
function mc.getPlayer(name) end

---Returns the current version of LuaCraft.
---@return string @The version string
function mc.getVersion() end

---Returns the LuaJ engine version.
---@return string @The LuaJ version
function mc.getLuaJVersion() end

---Summons an entity at a specific position.
---@param entityId string
---@param pos table
---@return boolean @True if the entity was spawned
function mc.summonEntity(entityId, pos) end

---Creates a LuaItemStack from a registry ID and count.
---@param itemId string
---@param count number
---@return LuaItemStack @The created item stack or nil if invalid
function mc.createItemStack(itemId, count) end

---Gets the current motion vector of the entity.
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaEntity:getVelocity() end

---Sets the entity's motion vector and marks it as changed for syncing.
---@param velocity table
function LuaEntity:setVelocity(velocity) end

---Returns the entity's internal ID.
---@return number @The entity ID
function LuaEntity:getId() end

---Returns the entity's registry name/type.
---@return string @Entity type name
function LuaEntity:getType() end

---Checks if the entity is a player.
---@return boolean @True if player
function LuaEntity:isPlayer() end

---Instantly kills the entity.
function LuaEntity:kill() end

---Teleports the entity to a specific position. Accepts a table with x, y, and z fields.
---@param pos table
function LuaEntity:setPosition(pos) end

---Sets the entity on fire for a specific number of ticks.
---@param ticks number
function LuaEntity:setFireTicks(ticks) end

---Extinguishes fire on the entity.
function LuaEntity:clearFire() end

---Returns the current position of the entity. May include decimal precision for players, but block-aligned for mobs and other entities.
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaEntity:getPosition() end

---Gets the current health of the entity. Returns nil if the entity does not have a health attribute.
---@return number|nil @The current health value or nil
function LuaEntity:getHealth() end

---Sets the entity's health to a specified value, if applicable.
---@param health number
function LuaEntity:setHealth(health) end

---Retrieves the maximum health of the entity, if available.
---@return number|nil @The maximum health value or nil
function LuaEntity:getMaxHealth() end

---Heals the entity by the specified amount, if applicable.
---@param amount number
function LuaEntity:heal(amount) end

---Damages the entity by the specified amount, if applicable.
---@param amount number
function LuaEntity:damage(amount) end

---Returns the world in which the entity resides. For player entities, this is the world the player is in.
---@return LuaWorld @The world object where the entity is located
function LuaEntity:getWorld() end

---Returns the direction the entity is currently looking as a normalized vector.
---@return table @A normalized table with 'x', 'y', and 'z' fields, or nil if not applicable
function LuaEntity:getLookDirection() end

---Returns the current number of ticks the entity will remain on fire. (Accessed via reflection)
---@return number @The fire ticks, or an error if inaccessible
function LuaEntity:getFireTicks() end

---Returns the name of the player.
---@return string @The player's name
function LuaPlayer:getName() end

---Checks if the player is an operator.
---@return boolean @True if op
function LuaPlayer:isOp() end

---Gets the name of the item held by the player.
---@param hand string|nil
---@return string|nil @Held item name or nil if empty
function LuaPlayer:getHeldItem(hand) end

---Adds an item to the player's inventory.
---@param item string|LuaItemStack
---@return boolean @True if added successfully
function LuaPlayer:addItem(item) end

---Sends a raw JSON-formatted chat message to the player. Accepts either a tellraw array (e.g. '[{"text":"hello"}]') or a fallback string with formatting codes like '§aHello world'. Parses the message and shows it with formatting.
---@param json string
function LuaPlayer:sendTellraw(json) end

---Sends a formatted chat message using a Lua table representing a tellraw-style JSON array. Supports Minecraft 1.12.2 structure including hoverEvent, clickEvent, and formatting like color and bold. Each table entry must be either a plain string (sent directly) or a table with keys like 'text', 'color', 'bold', etc.
---@param parts table
function LuaPlayer:sendTellrawFromTable(parts) end

---Gives an item to the player's inventory using a registry ID. Accepts an optional second argument for the total count to give. If the total count is above 64, the items are split into multiple stacks (clamped between 1 and 256). Returns true if all items were successfully added.
---@param item string
---@param count number|nil
---@return boolean @True if the items were successfully added
function LuaPlayer:giveItem(item, count) end

---Adds a custom-created LuaItemStack to the player's inventory.
---@param itemStack LuaItemStack
---@return boolean @True if the item stack was added successfully
function LuaPlayer:giveItemStack(itemStack) end

---Returns the name of the dimension the player is currently in.
---@return string @The dimension name (e.g., 'overworld')
function LuaPlayer:getDimension() end

---Returns the item currently held in the player's main hand as a LuaItemStack, or nil if the hand is empty.
---@return LuaItemStack|nil @The held item stack or nil
function LuaPlayer:getItemInHand() end

---Returns the item in the player's inventory at the given slot index as a LuaItemStack, or nil if empty.
---@param index number
---@return LuaItemStack|nil @The item in the slot or nil
function LuaPlayer:getInventoryItem(index) end

---Returns the block the player is currently looking at, up to a certain distance.
---@param distance number
---@return LuaBlock? @The block being looked at or nil if none
function LuaPlayer:getTargetBlock(distance) end

---Gets the position of the player's eyes. Useful for raycasting or line-of-sight calculations.
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaPlayer:getPositionEyes() end

---Gets the current motion vector of the entity. (Inherited from LuaEntity)
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaPlayer:getVelocity() end

---Sets the entity's motion vector and marks it as changed for syncing. (Inherited from LuaEntity)
---@param velocity table
function LuaPlayer:setVelocity(velocity) end

---Returns the entity's internal ID. (Inherited from LuaEntity)
---@return number @The entity ID
function LuaPlayer:getId() end

---Returns the entity's registry name/type. (Inherited from LuaEntity)
---@return string @Entity type name
function LuaPlayer:getType() end

---Checks if the entity is a player. (Inherited from LuaEntity)
---@return boolean @True if player
function LuaPlayer:isPlayer() end

---Instantly kills the entity. (Inherited from LuaEntity)
function LuaPlayer:kill() end

---Teleports the entity to a specific position. Accepts a table with x, y, and z fields. (Inherited from LuaEntity)
---@param pos table
function LuaPlayer:setPosition(pos) end

---Sets the entity on fire for a specific number of ticks. (Inherited from LuaEntity)
---@param ticks number
function LuaPlayer:setFireTicks(ticks) end

---Extinguishes fire on the entity. (Inherited from LuaEntity)
function LuaPlayer:clearFire() end

---Returns the current position of the entity. May include decimal precision for players, but block-aligned for mobs and other entities. (Inherited from LuaEntity)
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaPlayer:getPosition() end

---Gets the current health of the entity. Returns nil if the entity does not have a health attribute. (Inherited from LuaEntity)
---@return number|nil @The current health value or nil
function LuaPlayer:getHealth() end

---Sets the entity's health to a specified value, if applicable. (Inherited from LuaEntity)
---@param health number
function LuaPlayer:setHealth(health) end

---Retrieves the maximum health of the entity, if available. (Inherited from LuaEntity)
---@return number|nil @The maximum health value or nil
function LuaPlayer:getMaxHealth() end

---Heals the entity by the specified amount, if applicable. (Inherited from LuaEntity)
---@param amount number
function LuaPlayer:heal(amount) end

---Damages the entity by the specified amount, if applicable. (Inherited from LuaEntity)
---@param amount number
function LuaPlayer:damage(amount) end

---Returns the world in which the entity resides. For player entities, this is the world the player is in. (Inherited from LuaEntity)
---@return LuaWorld @The world object where the entity is located
function LuaPlayer:getWorld() end

---Returns the direction the entity is currently looking as a normalized vector. (Inherited from LuaEntity)
---@return table @A normalized table with 'x', 'y', and 'z' fields, or nil if not applicable
function LuaPlayer:getLookDirection() end

---Returns the current number of ticks the entity will remain on fire. (Accessed via reflection) (Inherited from LuaEntity)
---@return number @The fire ticks, or an error if inaccessible
function LuaPlayer:getFireTicks() end

---Gets the current world time.
---@return number @The world time in ticks
function LuaWorld:getTime() end

---Sets the world time relative to the current day. The provided tick value is applied to the current day without affecting the number of days passed.
---@param ticks number
function LuaWorld:setTime(ticks) end

---Sets the world time absolutely.
---@param ticks number
function LuaWorld:setTimeAbsolute(ticks) end

---Sets the world time relative to the current day using a clock format. Expects a string in HH:MM:SS format.
---@param time string
function LuaWorld:setClockTime(time) end

---Sets the world time absolutely using a clock format. Expects a string in HH:MM:SS format.
---@param time string
function LuaWorld:setClockTimeAbsolute(time) end

---Sets the number of days passed while preserving the current time-of-day.
---@param days number
function LuaWorld:setDaysPassed(days) end

---Gets the name of the current dimension.
---@return string @The dimension name (e.g. 'overworld')
function LuaWorld:getDimension() end

---Checks if it is currently raining.
---@return boolean @True if raining
function LuaWorld:isRaining() end

---Sets the rain state of the world.
---@param raining boolean
function LuaWorld:setRaining(raining) end

---Creates an explosion at the given position. Accepts a table with x, y, and z fields.
---@param pos table
function LuaWorld:createExplosion(pos) end

---Returns a LuaBlock at the given position.
---@param pos table
---@return LuaBlock @The block at that position
function LuaWorld:getBlockAt(pos) end

---Sets a block at a position using a block ID string.
---@param info table
---@return boolean @True if successful
function LuaWorld:setBlockAt(info) end

---Sets the block at the given LuaBlock position.
---@param info table
---@return boolean @True if successful
function LuaWorld:setBlock(info) end

---Gets the world's default spawn location as a table with x, y, z.
---@return table @The spawn point as a table with x, y, z
function LuaWorld:getSpawnPoint() end

---Returns a list of all player entities in the world wrapped as LuaPlayer objects.
---@return LuaPlayer[] @A list of all players in the world
function LuaWorld:getPlayers() end

---Strikes lightning at the given position. Accepts a table with x, y, and z fields.
---@param pos table
function LuaWorld:strikeLightning(pos) end

---Kills all Ender Dragons in the world by setting their health to 0. Works in all worlds; if none are found, returns false.
---@return boolean @True if any Ender Dragons were defeated, false otherwise
function LuaWorld:defeatEnderDragon() end

---Returns the material name (fallback via toString).
---@return string @Material name
function LuaMaterial:getName() end

---Returns true if the material is a liquid.
---@return boolean
function LuaMaterial:isLiquid() end

---Returns true if the material is solid.
---@return boolean
function LuaMaterial:isSolid() end

---Returns true if the material can be replaced (e.g. tall grass, fluids).
---@return boolean
function LuaMaterial:isReplaceable() end

---Returns the registry ID of the item.
---@return string @e.g., 'minecraft:stone'
function LuaItemStack:getItemId() end

---Returns the number of items in the stack.
---@return number @The stack count
function LuaItemStack:getCount() end

---Sets the number of items in the stack.
---@param count number
function LuaItemStack:setCount(count) end

---Returns true if the item stack is empty.
---@return boolean
function LuaItemStack:isEmpty() end

---Gets the current damage value (durability) of the item.
---@return number @The damage value
function LuaItemStack:getDamage() end

---Sets the damage value (durability) of the item.
---@param damage number
function LuaItemStack:setDamage(damage) end

---Returns the display name of the item.
---@return string @The item display name
function LuaItemStack:getDisplayName() end

---Sets the username displayed on a player head item.
---@param name string
---@return boolean @True if successful
function LuaItemStack:setUsername(name) end

---Adds an enchantment to the item stack using the given registry ID and level.
---@param enchantId string
---@param level number
---@return boolean @True if the enchantment was added
function LuaItemStack:addEnchantment(enchantId, level) end

---Modifies the level of an existing enchantment on the item stack.
---@param enchantId string
---@param newLevel number
---@return boolean @True if the enchantment was modified
function LuaItemStack:modifyEnchantment(enchantId, newLevel) end

---Removes the enchantment identified by the given registry ID from the item stack.
---@param enchantId string
---@return boolean @True if the enchantment was removed
function LuaItemStack:removeEnchantment(enchantId) end

---Sets the display name of the item. The name is stored in the item's display tag.
---@param name string
function LuaItemStack:setDisplayName(name) end

---Sets the lore for the item. Expects a table of strings, where each string is a lore line.
---@param lore table
function LuaItemStack:setLore(lore) end

---Sets the content of a written book. Expects a table with 'title', 'author', and 'pages' keys. The 'pages' key should be a table of strings, each representing a page's text.
---@param bookInfo table
---@return boolean @True if the book content was successfully set
function LuaItemStack:setBookContent(bookInfo) end

---Returns the quantity of items in the stack (same as getCount).
---@return number @The stack amount
function LuaItemStack:getAmount() end

---Returns the block material type of the item, if applicable.
---@return LuaMaterial|nil @The item's material, or nil if not applicable
function LuaItemStack:getMaterial() end

---Returns the registry ID of the item as a string (e.g., 'minecraft:stone'). This mirrors the behavior of getType() from LuaCraftBeta.
---@return string @The registry ID of the item
function LuaItemStack:getType() end

---Returns the legacy numeric ID of the block.
---@return number @The legacy block ID (e.g., 1 for stone)
function LuaBlock:getId() end

---Returns the block relative to this one by offset. Accepts a table with x, y, and z fields.
---@param offset table
---@return LuaBlock @The block at the offset position
function LuaBlock:getRelative(offset) end

---Returns the block's metadata value (damage/data). If the block has no metadata, returns 0. Some blocks may not support this and will default.
---@return number @The metadata value, or 0 as fallback
function LuaBlock:getData() end

---Returns the position of this block as a table with x, y, and z.
---@return table @A table with numeric fields 'x', 'y', and 'z'
function LuaBlock:getPosition() end

---Gets the registry name of the block type.
---@return string @Block ID (e.g., 'minecraft:stone')
function LuaBlock:getType() end

---Gets the material of the block.
---@return LuaMaterial @The block's material
function LuaBlock:getMaterial() end

---Returns true if the block is air.
---@return boolean
function LuaBlock:isAir() end

---Returns true if the block's material is solid.
---@return boolean
function LuaBlock:isSolid() end

---Stops the event listener from receiving further events.
function LuaEvent:disconnect() end

---@type LuaPlayer
sender = nil

