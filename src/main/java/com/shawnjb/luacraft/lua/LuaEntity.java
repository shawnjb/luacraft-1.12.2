package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaVector3;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
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

        set("isPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity instanceof EntityPlayer);
            }
        });

        set("kill", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                entity.setDead();
                return LuaValue.NIL;
            }
        });

        set("getPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof EntityPlayer) {
                    return new LuaVector3(((EntityPlayer) entity).posX, ((EntityPlayer) entity).posY,
                            ((EntityPlayer) entity).posZ);
                } else {
                    BlockPos pos = entity.getPosition();
                    LuaTable t = new LuaTable();
                    t.set("x", LuaValue.valueOf(pos.getX()));
                    t.set("y", LuaValue.valueOf(pos.getY()));
                    t.set("z", LuaValue.valueOf(pos.getZ()));
                    return t;
                }
            }
        });

        set("setPosition", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaVector3 pos = LuaVector3.fromLuaTable(arg.checktable());
                entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
                return LuaValue.NIL;
            }
        });

        set("setFireTicks", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                entity.setFire(arg.checkint());
                return LuaValue.NIL;
            }
        });

        set("clearFire", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                entity.extinguish();
                return LuaValue.NIL;
            }
        });

        set("getHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof EntityLivingBase) {
                    return LuaValue.valueOf(((EntityLivingBase) entity).getHealth());
                }
                return LuaValue.NIL;
            }
        });

        set("setHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).setHealth((float) arg.checkdouble());
                }
                return LuaValue.NIL;
            }
        });

        set("getMaxHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof EntityLivingBase) {
                    return LuaValue.valueOf(((EntityLivingBase) entity).getMaxHealth());
                }
                return LuaValue.NIL;
            }
        });

        set("heal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).heal((float) arg.checkdouble());
                }
                return LuaValue.NIL;
            }
        });

        set("damage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).attackEntityFrom(null, (float) arg.checkdouble());
                }
                return LuaValue.NIL;
            }
        });

        set("kill", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).setHealth(0);
                }
                return LuaValue.NIL;
            }
        });

        set("getWorld", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LuaWorld(entity.getEntityWorld());
            }
        });
    }

    public Entity getHandle() {
        return entity;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaEntity");

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getId", "Returns the entity's internal ID.",
                Arrays.asList(), Arrays.asList(new LuaDocRegistry.Return("number", "The entity ID")), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getType", "Returns the entity's registry name/type.",
                Arrays.asList(), Arrays.asList(new LuaDocRegistry.Return("string", "Entity type name")), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "isPlayer", "Checks if the entity is a player.",
                Arrays.asList(), Arrays.asList(new LuaDocRegistry.Return("boolean", "True if player")), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "kill", "Instantly kills the entity.", Arrays.asList(), Arrays.asList(), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "setPosition", "Sets the position of the entity.",
                Arrays.asList(new LuaDocRegistry.Param("pos", "Vector3", "The new position")), Arrays.asList(), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "setFireTicks", "Sets the entity on fire for a specific number of ticks.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "Number of ticks to burn")), Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "clearFire", "Extinguishes fire on the entity.", Arrays.asList(), Arrays.asList(), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getPosition",
                "Returns the current position of the entity. For players, returns a Vector3; for other entities, returns a table with x, y, and z fields.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3|table", "The entity's position")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getHealth",
                "Gets the current health of the entity. Returns nil if the entity does not have a health attribute.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number|nil", "The current health value or nil")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "setHealth",
                "Sets the entity's health to a specified value, if applicable.",
                Arrays.asList(new LuaDocRegistry.Param("health", "number", "The new health value")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getMaxHealth",
                "Retrieves the maximum health of the entity, if available.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number|nil", "The maximum health value or nil")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "heal",
                "Heals the entity by the specified amount, if applicable.",
                Arrays.asList(new LuaDocRegistry.Param("amount", "number", "The amount to heal")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "damage",
                "Damages the entity by the specified amount, if applicable.",
                Arrays.asList(new LuaDocRegistry.Param("amount", "number", "The damage amount")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getWorld",
                "Returns the world in which the entity resides. For player entities, this is the world the player is in.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("LuaWorld", "The world object where the entity is located")),
                true));
    }
}
