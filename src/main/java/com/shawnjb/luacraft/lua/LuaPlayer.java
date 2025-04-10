package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.api.LuaItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import java.util.Arrays;

public class LuaPlayer extends LuaEntity {
    private final EntityPlayer player;

    public LuaPlayer(EntityPlayer player) {
        super(player);
        this.player = player;

        set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getName());
            }
        });

        set("isOp", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.canUseCommand(4, ""));
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
                ItemStack stack;

                if (arg.isuserdata(LuaItemStack.class)) {
                    stack = ((LuaItemStack) arg.checkuserdata(LuaItemStack.class)).getHandle();
                } else if (arg.isstring()) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arg.tojstring()));
                    if (item == null)
                        return LuaValue.FALSE;
                    stack = new ItemStack(item);
                } else {
                    return LuaValue.error("Expected item name string or LuaItemStack");
                }

                boolean success = player.inventory.addItemStackToInventory(stack);
                return LuaValue.valueOf(success);
            }
        });

        set("sendTellraw", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String json = arg.checkjstring();
                try {
                    ITextComponent comp = ITextComponent.Serializer.fromJsonLenient(json);
                    if (comp != null) {
                        player.sendMessage(comp);
                    } else {
                        throw new IllegalArgumentException("Parsed component was null");
                    }
                } catch (Exception e) {
                    player.sendMessage(new TextComponentString(json));
                }
                return LuaValue.NIL;
            }
        });

        set("giveItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String id = arg.checkjstring();
                LuaValue luaStack = com.shawnjb.luacraft.lua.api.LuaItemStack.of(id, 1);
                if (luaStack == null) {
                    return LuaValue.FALSE;
                }
                LuaItemStack stack = (LuaItemStack) luaStack.checkuserdata(LuaItemStack.class);
                boolean success = player.inventory.addItemStackToInventory(stack.getHandle());
                return LuaValue.valueOf(success);
            }
        });

        set("giveItemStack", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (!arg.isuserdata(LuaItemStack.class)) {
                    return LuaValue.error("Expected LuaItemStack userdata");
                }
                LuaItemStack luaStack = (LuaItemStack) arg.checkuserdata(LuaItemStack.class);
                boolean success = player.inventory.addItemStackToInventory(luaStack.getHandle());
                return LuaValue.valueOf(success);
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
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The player's name")),
                true));
        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "isOp",
                "Checks if the player is an operator.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if op")),
                true));
        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getHeldItem",
                "Gets the name of the item held by the player.",
                Arrays.asList(new LuaDocRegistry.Param("hand", "string|nil", "The hand to check ('main' or 'off')")),
                Arrays.asList(new LuaDocRegistry.Return("string|nil", "Held item name or nil if empty")),
                true));
        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "addItem",
                "Adds an item to the player's inventory.",
                Arrays.asList(new LuaDocRegistry.Param("item", "string|LuaItemStack", "The item ID or LuaItemStack")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if added successfully")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "sendTellraw",
                "Sends a raw JSON-formatted chat message to the player. " +
                        "Accepts JSON strings such as " +
                        "'{\"rawtext\":[{\"text\":\"§aExample \"},{\"text\":\"§e§lText\"}]}' " +
                        "or " +
                        "'{\"rawtext\":[{\"text\":\"§aExample §e§lText\"}]}' " +
                        "and falls back to plain text if the JSON is invalid.",
                Arrays.asList(new LuaDocRegistry.Param("json", "string",
                        "A raw JSON-formatted string representing the chat message")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "giveItem",
                "Gives an item to the player's inventory using a registry ID or a LuaItemStack. " +
                        "If a string is provided, it is treated as a registry ID and a single item is created. " +
                        "Returns true if the item was successfully added.",
                Arrays.asList(new LuaDocRegistry.Param("item", "string|LuaItemStack",
                        "The registry ID of the item (e.g. 'minecraft:stone') or a LuaItemStack")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the item was successfully added")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "giveItemStack",
                "Adds a custom-created LuaItemStack to the player's inventory.",
                Arrays.asList(new LuaDocRegistry.Param("itemStack", "LuaItemStack", "The item stack to add")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the item stack was added successfully")),
                true));

        LuaDocRegistry.inheritMethods("LuaEntity", "LuaPlayer");
    }
}
