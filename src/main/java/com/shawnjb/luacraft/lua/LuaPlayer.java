package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaVector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Collections;

public class LuaPlayer extends LuaTable {
    private final EntityPlayer player;

    public LuaPlayer(EntityPlayer player) {
        this.player = player;

        set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getName());
            }
        });

        set("getHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getHealth());
            }
        });

        set("setHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                player.setHealth((float) arg.checkdouble());
                return LuaValue.NIL;
            }
        });

        set("isOp", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.canUseCommand(4, ""));
            }
        });

        set("setFireTicks", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                player.setFire(arg.checkint());
                return LuaValue.NIL;
            }
        });

        set("getMaxHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getMaxHealth());
            }
        });

        set("heal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                player.heal((float) arg.checkdouble());
                return LuaValue.NIL;
            }
        });

        set("damage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                player.attackEntityFrom(null, (float) arg.checkdouble());
                return LuaValue.NIL;
            }
        });

        set("kill", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                player.setHealth(0);
                return LuaValue.NIL;
            }
        });

        set("getHeldItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String handStr = arg.isnil() ? "main" : arg.checkjstring().toLowerCase();
                EnumHand hand = handStr.equals("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                ItemStack held = player.getHeldItem(hand);
                if (held == null || held.isEmpty())
                    return LuaValue.NIL;
                return LuaValue.valueOf(held.getItem().getTranslationKey());
            }
        });

        set("addItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String name = arg.checkjstring();
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
                if (item == null)
                    return LuaValue.FALSE;

                ItemStack stack = new ItemStack(item);
                boolean success = player.inventory.addItemStackToInventory(stack);
                return LuaValue.valueOf(success);
            }
        });

        set("getPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LuaVector3(player.posX, player.posY, player.posZ);
            }
        });
    }

    public EntityPlayer getHandle() {
        return player;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaPlayer");

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getName",
                "Returns the name of the player.",
                Collections.emptyList(),
                Collections.singletonList(new LuaDocRegistry.Return("string", "The player's name")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getHealth",
                "Gets the player's current health.",
                Collections.emptyList(),
                Collections.singletonList(new LuaDocRegistry.Return("number", "Current health value")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "setHealth",
                "Sets the player's health.",
                Collections.singletonList(new LuaDocRegistry.Param("health", "number", "The new health value")),
                Collections.emptyList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "isOp",
                "Checks if the player is an operator.",
                Collections.emptyList(),
                Collections.singletonList(new LuaDocRegistry.Return("boolean", "True if the player is op")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "setFireTicks",
                "Sets the player on fire for a specific number of ticks.",
                Collections.singletonList(new LuaDocRegistry.Param("ticks", "number", "Number of ticks to burn")),
                Collections.emptyList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getMaxHealth",
                "Gets the player's maximum health.",
                Collections.emptyList(),
                Collections.singletonList(new LuaDocRegistry.Return("number", "The max health value")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "heal",
                "Heals the player by the given amount.",
                Collections.singletonList(new LuaDocRegistry.Param("amount", "number", "Amount to heal")),
                Collections.emptyList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "damage",
                "Damages the player by the given amount.",
                Collections.singletonList(new LuaDocRegistry.Param("amount", "number", "Amount to damage")),
                Collections.emptyList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "kill",
                "Kills the player instantly.",
                Collections.emptyList(),
                Collections.emptyList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getHeldItem",
                "Gets the name of the item held by the player.",
                Collections.singletonList(new LuaDocRegistry.Param("hand", "string|nil",
                        "The hand to check ('main' or 'off'). Defaults to 'main'.")),
                Collections.singletonList(
                        new LuaDocRegistry.Return("string|nil", "Name of the held item or nil if empty")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "addItem",
                "Adds an item to the player's inventory by name.",
                Collections
                        .singletonList(new LuaDocRegistry.Param("itemName", "string", "The registry name of the item")),
                Collections.singletonList(new LuaDocRegistry.Return("boolean", "True if added successfully")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getPosition",
                "Gets the player's current position as a Vector3.",
                Collections.emptyList(),
                Collections.singletonList(new LuaDocRegistry.Return("Vector3", "The player's position as a vector")),
                true));
    }
}