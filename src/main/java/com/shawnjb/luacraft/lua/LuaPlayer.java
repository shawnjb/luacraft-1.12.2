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
import org.luaj.vm2.lib.TwoArgFunction;
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

        set("giveItem", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue idArg, LuaValue countArg) {
                String id = idArg.checkjstring();
                int totalCount = 1;
                if (!countArg.isnil()) {
                    totalCount = countArg.checkint();
                }
                totalCount = Math.max(1, Math.min(totalCount, 256));
                final int maxStackSize = 64;
                boolean overallSuccess = true;
                int remaining = totalCount;

                while (remaining > 0) {
                    int stackCount = Math.min(remaining, maxStackSize);
                    LuaValue luaStack = com.shawnjb.luacraft.lua.api.LuaItemStack.of(id, stackCount);
                    if (luaStack == null) {
                        return LuaValue.FALSE;
                    }
                    LuaItemStack itemStack = (LuaItemStack) luaStack.checkuserdata(LuaItemStack.class);
                    boolean partSuccess = player.inventory.addItemStackToInventory(itemStack.getHandle());
                    overallSuccess = overallSuccess && partSuccess;
                    remaining -= stackCount;
                }
                return LuaValue.valueOf(overallSuccess);
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

        set("getDimension", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(player.getEntityWorld().provider.getDimensionType().getName());
            }
        });

        set("getItemInHand", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
                if (held == null || held.isEmpty())
                    return LuaValue.NIL;
                return new LuaItemStack(held);
            }
        });

        set("getInventoryItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue indexVal) {
                int index = indexVal.checkint();
                if (index < 0 || index >= player.inventory.getSizeInventory())
                    return LuaValue.NIL;
                ItemStack stack = player.inventory.getStackInSlot(index);
                if (stack == null || stack.isEmpty())
                    return LuaValue.NIL;
                return new LuaItemStack(stack);
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
                "Gives an item to the player's inventory using a registry ID. " +
                        "Accepts an optional second argument for the total count to give. " +
                        "If the total count is above 64, the items are split into multiple stacks (clamped between 1 and 256). "
                        +
                        "Returns true if all items were successfully added.",
                Arrays.asList(
                        new LuaDocRegistry.Param("item", "string",
                                "The registry ID of the item (e.g. 'minecraft:stone')"),
                        new LuaDocRegistry.Param("count", "number|nil",
                                "Optional total count to give (clamped between 1 and 256)")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the items were successfully added")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "giveItemStack",
                "Adds a custom-created LuaItemStack to the player's inventory.",
                Arrays.asList(new LuaDocRegistry.Param("itemStack", "LuaItemStack", "The item stack to add")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the item stack was added successfully")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getDimension",
                "Returns the name of the dimension the player is currently in.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The dimension name (e.g., 'overworld')")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getItemInHand",
                "Returns the item currently held in the player's main hand as a LuaItemStack, or nil if the hand is empty.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("LuaItemStack|nil", "The held item stack or nil")),
                true));

        LuaDocRegistry.addFunction("LuaPlayer", new LuaDocRegistry.FunctionDoc(
                "getInventoryItem",
                "Returns the item in the player's inventory at the given slot index as a LuaItemStack, or nil if empty.",
                Arrays.asList(new LuaDocRegistry.Param("index", "number", "The inventory slot index (0-based)")),
                Arrays.asList(new LuaDocRegistry.Return("LuaItemStack|nil", "The item in the slot or nil")),
                true));

        LuaDocRegistry.inheritMethods("LuaEntity", "LuaPlayer");
    }
}
