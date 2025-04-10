package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaMaterial;
import com.shawnjb.luacraft.lua.api.LuaVector3;
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

        set("getPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LuaVector3(pos.getX(), pos.getY(), pos.getZ());
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
                "getPosition",
                "Returns the position of the block as a Vector3.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Block position")),
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
