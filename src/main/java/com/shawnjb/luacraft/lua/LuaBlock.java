package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaBlock extends LuaTable {
    private final World world;
    private final BlockPos pos;

    public LuaBlock(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;

        set("getId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Block block = world.getBlockState(pos).getBlock();
                int id = Block.getIdFromBlock(block);
                return LuaValue.valueOf(id);
            }
        });

        set("getPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaUtils.makeXYZ(pos.getX(), pos.getY(), pos.getZ());
            }
        });

        set("getRelative", new org.luaj.vm2.lib.OneArgFunction() {
            @Override
            public LuaValue call(LuaValue direction) {
                double[] offset = LuaUtils.unpackXYZ(direction);
                BlockPos relative = pos.add(offset[0], offset[1], offset[2]);
                return new LuaBlock(world, relative);
            }
        });

        set("getData", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                try {
                    return LuaValue.valueOf(block.getMetaFromState(state));
                } catch (IllegalArgumentException ignored) {
                    return LuaValue.valueOf(0);
                }
            }
        });

        set("getType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Block block = world.getBlockState(pos).getBlock();
                return LuaValue.valueOf(block.getRegistryName().toString());
            }
        });

        set("getMaterial", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                IBlockState state = world.getBlockState(pos);
                return new LuaMaterial(state.getMaterial());
            }
        });

        set("isAir", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.isAirBlock(pos));
            }
        });

        set("isSolid", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(world.getBlockState(pos).getMaterial().isSolid());
            }
        });
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaBlock");

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getId",
                "Returns the legacy numeric ID of the block.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The legacy block ID (e.g., 1 for stone)")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getRelative",
                "Returns the block relative to this one by offset. Accepts a table with x, y, and z fields.",
                Arrays.asList(
                        new LuaDocRegistry.Param("offset", "table", "A table with numeric fields 'x', 'y', and 'z'")),
                Arrays.asList(new LuaDocRegistry.Return("LuaBlock", "The block at the offset position")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getData",
                "Returns the block's metadata value (damage/data). If the block has no metadata, returns 0. Some blocks may not support this and will default.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The metadata value, or 0 as fallback")),
                true));

        LuaDocRegistry.addFunction("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getPosition",
                "Returns the position of this block as a table with x, y, and z.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table", "A table with numeric fields 'x', 'y', and 'z'")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getType",
                "Gets the registry name of the block type.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Block ID (e.g., 'minecraft:stone')")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "getMaterial",
                "Gets the material of the block.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("LuaMaterial", "The block's material")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "isAir",
                "Returns true if the block is air.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true));

        LuaDocRegistry.addMethod("LuaBlock", new LuaDocRegistry.FunctionDoc(
                "isSolid",
                "Returns true if the block's material is solid.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true));
    }
}
