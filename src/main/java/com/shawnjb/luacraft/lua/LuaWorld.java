package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import net.minecraft.world.World;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import com.shawnjb.luacraft.lua.api.LuaVector3;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class LuaWorld extends LuaTable {
    private final World world;

    public LuaWorld(World world) {
        this.world = world;

        set("getTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.getWorldTime());
            }
        });

        set("setTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                world.setWorldTime(arg.checklong());
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
                if (!arg.istable()) {
                    return LuaValue.error("Expected table {x=..., y=..., z=...}");
                }

                double x = arg.get("x").checkdouble();
                double y = arg.get("y").checkdouble();
                double z = arg.get("z").checkdouble();

                world.createExplosion(null, x, y, z, 4.0F, true);
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
                    return LuaValue.error("Expected table with { pos = {x=..,y=..,z=..}, id = 'minecraft:stone' }");
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
                "Sets the world time.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "The new world time in ticks")),
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
                Arrays.asList(new LuaDocRegistry.Param("pos", "table", "Table with x, y, z")),
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
    }
}
