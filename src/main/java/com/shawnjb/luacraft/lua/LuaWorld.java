package com.shawnjb.luacraft.lua;

import java.util.Arrays;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaVector3;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LuaWorld extends LuaTable {
    private final World world;

    public LuaWorld(World world) {
        this.world = world;

        set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.getWorldInfo().getWorldName());
            }
        });

        set("getSpawnPoint", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                BlockPos spawn = world.getSpawnPoint();
                return new LuaVector3(spawn.getX(), spawn.getY(), spawn.getZ());
            }
        });

        set("getTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.getWorldTime());
            }
        });

        set("setTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                long relativeTicks = arg.checklong();
                long current = world.getWorldTime();
                long newAbsolute = current - (current % 24000) + relativeTicks;
                world.setWorldTime(newAbsolute);
                return LuaValue.NIL;
            }
        });

        set("setTimeAbsolute", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                world.setWorldTime(arg.checklong());
                return LuaValue.NIL;
            }
        });

        set("setClockTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String timeStr = arg.checkjstring();
                long ticks = parseClockTime(timeStr);
                long current = world.getWorldTime();
                long newAbsolute = current - (current % 24000) + ticks;
                world.setWorldTime(newAbsolute);
                return LuaValue.NIL;
            }
        });

        set("setClockTimeAbsolute", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String timeStr = arg.checkjstring();
                long ticks = parseClockTime(timeStr);
                world.setWorldTime(ticks);
                return LuaValue.NIL;
            }
        });

        set("setDaysPassed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                long days = arg.checklong();
                long current = world.getWorldTime();
                long dayTime = current % 24000;
                long newTime = days * 24000L + dayTime;
                world.setWorldTime(newTime);
                return LuaValue.NIL;
            }
        });

        set("getDimension", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.provider.getDimensionType().getName());
            }
        });

        set("isRaining", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.isRaining());
            }
        });

        set("setRaining", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                world.getWorldInfo().setRaining(arg.checkboolean());
                return LuaValue.NIL;
            }
        });

        set("createExplosion", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    LuaVector3 vec;
        
                    if (arg instanceof LuaVector3) {
                        vec = (LuaVector3) arg;
                    } else if (arg.istable()) {
                        vec = LuaVector3.fromLuaTable(arg.checktable());
                    } else {
                        return LuaValue.error("Expected Vector3 table or LuaVector3");
                    }
        
                    world.createExplosion(null, vec.x, vec.y, vec.z, 4.0F, true);
                } catch (Exception e) {
                    return LuaValue.error("Invalid Vector3: " + e.getMessage());
                }
        
                return LuaValue.NIL;
            }
        });
        
        set("getBlockAt", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaVector3 vec = LuaVector3.fromLuaTable(arg.checktable());
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                return new LuaBlock(world, pos);
            }
        });

        set("setBlockAt", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaTable tbl = arg.checktable();
                LuaValue posVal = tbl.get("pos");
                LuaValue idVal = tbl.get("id");

                if (!posVal.istable() || !idVal.isstring()) {
                    return LuaValue.error("Expected table with { pos = Vector3, id = 'minecraft:block_id' }");
                }

                LuaVector3 vec = LuaVector3.fromLuaTable(posVal.checktable());
                String id = idVal.tojstring();

                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                if (block == null) {
                    return LuaValue.error("Unknown block ID: " + id);
                }

                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                world.setBlockState(pos, block.getDefaultState(), 3);
                return LuaValue.TRUE;
            }
        });

        set("setBlock", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaTable tbl = arg.checktable();
                LuaBlock blockRef = (LuaBlock) tbl.get("block").checkuserdata(LuaBlock.class);
                String id = tbl.get("id").checkjstring();

                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                if (block == null) {
                    return LuaValue.error("Unknown block ID: " + id);
                }

                world.setBlockState(blockRef.getBlockPos(), block.getDefaultState(), 3);
                return LuaValue.TRUE;
            }
        });

        set("getPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable players = new LuaTable();
                int index = 1;
                for (Object obj : world.playerEntities) {
                    if (obj instanceof net.minecraft.entity.player.EntityPlayer) {
                        players.set(index++, new LuaPlayer((net.minecraft.entity.player.EntityPlayer) obj));
                    }
                }
                return players;
            }
        });

        set("strikeLightning", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaVector3 pos = LuaVector3.fromLuaTable(arg.checktable());
                EntityLightningBolt bolt = new EntityLightningBolt(world, pos.x, pos.y, pos.z, false);
                world.spawnEntity(bolt);
                return LuaValue.NIL;
            }
        });

        set("defeatEnderDragon", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                boolean found = false;
                for (Object obj : world.loadedEntityList) {
                    if (obj instanceof net.minecraft.entity.boss.EntityDragon) {
                        net.minecraft.entity.boss.EntityDragon dragon = (net.minecraft.entity.boss.EntityDragon) obj;
                        dragon.setHealth(0);
                        found = true;
                    }
                }
                return LuaValue.valueOf(found);
            }
        });
    }

    private long parseClockTime(String timeStr) {
        String[] parts = timeStr.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Time must be in HH:MM:SS format");
        }
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            long ticks = hours * 1000L;
            ticks += Math.round(minutes * (1000.0 / 60.0));
            ticks += Math.round(seconds * (1000.0 / 3600.0));
            return ticks % 24000;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric values in time string: " + timeStr);
        }
    }

    public World getHandle() {
        return world;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaWorld");

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "getTime",
                "Gets the current world time.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The world time in ticks")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setTime",
                "Sets the world time relative to the current day. The provided tick value is applied to the current day without affecting the number of days passed.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "Tick value (0–23999)")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setTimeAbsolute",
                "Sets the world time absolutely.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "Absolute tick value")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setClockTime",
                "Sets the world time relative to the current day using a clock format. Expects a string in HH:MM:SS format.",
                Arrays.asList(new LuaDocRegistry.Param("time", "string", "Time in HH:MM:SS format")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setClockTimeAbsolute",
                "Sets the world time absolutely using a clock format. Expects a string in HH:MM:SS format.",
                Arrays.asList(new LuaDocRegistry.Param("time", "string", "Time in HH:MM:SS format")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setDaysPassed",
                "Sets the number of days passed while preserving the current time-of-day.",
                Arrays.asList(new LuaDocRegistry.Param("days", "number", "Number of days passed")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "getDimension",
                "Gets the name of the current dimension.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The dimension name (e.g. 'overworld')")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "isRaining",
                "Checks if it is currently raining.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if raining")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setRaining",
                "Sets the rain state of the world.",
                Arrays.asList(new LuaDocRegistry.Param("raining", "boolean", "Whether it should rain")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "createExplosion",
                "Creates an explosion at the given position.",
                Arrays.asList(new LuaDocRegistry.Param("pos", "Vector3", "The position to explode at")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "getBlockAt",
                "Returns a LuaBlock at the given position.",
                Arrays.asList(new LuaDocRegistry.Param("pos", "Vector3", "The position to query")),
                Arrays.asList(new LuaDocRegistry.Return("LuaBlock", "The block at that position")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setBlockAt",
                "Sets a block at a position using a block ID string.",
                Arrays.asList(new LuaDocRegistry.Param("info", "table", "Table with 'pos' and 'id' fields")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if successful")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "setBlock",
                "Sets the block at the given LuaBlock position.",
                Arrays.asList(new LuaDocRegistry.Param("info", "table", "Table with 'block' (LuaBlock) and 'id'")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if successful")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "getSpawnPoint",
                "Gets the world's default spawn location.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The spawn point as a vector")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "getPlayers",
                "Returns a list of all player entities in the world wrapped as LuaPlayer objects.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("LuaPlayer[]", "A list of all players in the world")),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "strikeLightning",
                "Strikes lightning at the given position by summoning a lightning bolt entity. " +
                        "Expects a Vector3 representing the position at which to strike lightning.",
                Arrays.asList(new LuaDocRegistry.Param("pos", "Vector3", "The position at which to strike lightning")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaWorld", new LuaDocRegistry.FunctionDoc(
                "defeatEnderDragon",
                "Kills all Ender Dragons in the world by setting their health to 0. " +
                        "Works in all worlds; if none are found, returns false.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean",
                        "True if any Ender Dragons were defeated, false otherwise")),
                true));
    }
}
