package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.math.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaEntity extends LuaTable {
    protected final Entity entity;

    public LuaEntity(Entity entity) {
        this.entity = entity;

        set("getId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getEntityId());
            }
        });

        set("getType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(EntityList.getEntityString(entity));
            }
        });

        set("getPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                BlockPos pos = entity.getPosition();
                LuaTable t = new LuaTable();
                t.set("x", LuaValue.valueOf(pos.getX()));
                t.set("y", LuaValue.valueOf(pos.getY()));
                t.set("z", LuaValue.valueOf(pos.getZ()));
                return t;
            }
        });

        set("isPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity instanceof net.minecraft.entity.player.EntityPlayer);
            }
        });

        set("kill", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                entity.setDead();
                return LuaValue.NIL;
            }
        });
    }

    public Entity getHandle() {
        return entity;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaEntity");

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getId",
                "Returns the entity's internal ID.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The entity ID")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getType",
                "Returns the entity's registry name/type.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Entity type name (e.g., 'Zombie')")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getPosition",
                "Returns the entity's current block position.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table", "Table with x, y, z fields")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "isPlayer",
                "Checks if the entity is a player.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the entity is a player")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "kill",
                "Instantly kills the entity.",
                Arrays.asList(),
                Arrays.asList(),
                true));
    }
}
