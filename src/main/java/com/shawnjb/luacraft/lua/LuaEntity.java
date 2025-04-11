package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.LuaLogger;
import com.shawnjb.luacraft.docs.LuaDocRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaEntity extends LuaTable {
    protected final Entity entity;

    public LuaEntity(Entity entity) {
        this.entity = entity;

        set("getVelocity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaUtils.makeXYZ(entity.motionX, entity.motionY, entity.motionZ);
            }
        });

        set("setVelocity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                double[] coords = LuaUtils.unpackXYZ(arg);
                entity.motionX = coords[0];
                entity.motionY = coords[1];
                entity.motionZ = coords[2];
                entity.velocityChanged = true;
                return LuaValue.NIL;
            }
        });

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
                    Vec3d position = ((EntityPlayer) entity).getPositionVector();
                    LuaTable posTable = LuaUtils.makeXYZ(position.x, position.y, position.z);
                    LuaLogger.LOGGER.info("[LuaCraft] Returning position vector: x=" + position.x + ", y=" + position.y + ", z=" + position.z);
                    return posTable;
                } else if (entity != null) {
                    Vec3d position = entity.getPositionVector();
                    LuaTable posTable = LuaUtils.makeXYZ(position.x, position.y, position.z);
                    LuaLogger.LOGGER.info("[LuaCraft] Returning position vector: x=" + position.x + ", y=" + position.y + ", z=" + position.z);
                    return posTable;
                }
                return LuaValue.NIL; 
            }
        });
        

        set("setPosition", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                double[] coords = LuaUtils.unpackXYZ(arg);
                entity.setPositionAndUpdate(coords[0], coords[1], coords[2]);
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

        set("getLookDirection", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof EntityLivingBase) {
                    Vec3d look = ((EntityLivingBase) entity).getLook(1.0F);
                    return LuaUtils.makeXYZ(look.x, look.y, look.z);
                }
                return LuaValue.NIL;
            }
        });

        set("getFireTicks", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (entity instanceof net.minecraft.entity.EntityLivingBase) {
                    try {
                        java.lang.reflect.Field fireField = net.minecraft.entity.EntityLivingBase.class
                                .getDeclaredField("fire");
                        fireField.setAccessible(true);
                        int fireTicks = fireField.getInt(entity);
                        return LuaValue.valueOf(fireTicks);
                    } catch (Exception e) {
                        return LuaValue.error("Failed to retrieve fire ticks: " + e.getMessage());
                    }
                }
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
                "getVelocity",
                "Gets the current motion vector of the entity.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table", "A table with numeric fields 'x', 'y', and 'z'")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "setVelocity",
                "Sets the entity's motion vector and marks it as changed for syncing.",
                Arrays.asList(
                        new LuaDocRegistry.Param("velocity", "table", "Table with numeric fields 'x', 'y', and 'z'")),
                Arrays.asList(),
                true));

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
                "setPosition",
                "Teleports the entity to a specific position. Accepts a table with x, y, and z fields.",
                Arrays.asList(
                        new LuaDocRegistry.Param("pos", "table", "A table with numeric fields 'x', 'y', and 'z'")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "setFireTicks", "Sets the entity on fire for a specific number of ticks.",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "Number of ticks to burn")), Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "clearFire", "Extinguishes fire on the entity.", Arrays.asList(), Arrays.asList(), true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getPosition",
                "Returns the current position of the entity. May include decimal precision for players, but block-aligned for mobs and other entities.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table", "A table with numeric fields 'x', 'y', and 'z'")),
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

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getLookDirection",
                "Returns the direction the entity is currently looking as a normalized vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table",
                        "A normalized table with 'x', 'y', and 'z' fields, or nil if not applicable")),
                true));

        LuaDocRegistry.addFunction("LuaEntity", new LuaDocRegistry.FunctionDoc(
                "getFireTicks",
                "Returns the current number of ticks the entity will remain on fire. (Accessed via reflection)",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The fire ticks, or an error if inaccessible")),
                true));
    }
}
