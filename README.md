# LuaCraft

LuaCraft is a Minecraft Forge 1.12.2 mod that adds Lua scripting support using LuaJ.

## Features

- Run Lua scripts with `/loadscript` or `/runscript`
- Bind to Minecraft events with `mc.bindToEvent`
- Access players, world, blocks, and entities from Lua
- Includes a `Vector3` class for 3D math

## Scripts

Place `.lua` files in:

```
config/luacraft/scripts/
```

Scripts in `scripts/autorun/` run automatically when the game starts.

## Example

```lua
mc.broadcast("Hello from Lua!")
local pos = Vector3.new(100, 64, 100)
mc.execute("/summon zombie " .. pos.x .. " " .. pos.y .. " " .. pos.z)
```