package com.shawnjb.luacraft.lua.api;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaItemStack extends LuaTable {
    private final ItemStack stack;

    public LuaItemStack(ItemStack stack) {
        this.stack = stack;

        set("getItemId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Item.REGISTRY.getNameForObject(stack.getItem()).toString());
            }
        });

        set("getCount", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.getCount());
            }
        });

        set("setCount", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                stack.setCount(arg.checkint());
                return LuaValue.NIL;
            }
        });

        set("isEmpty", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.isEmpty());
            }
        });
    }

    public ItemStack getHandle() {
        return stack;
    }

    public static LuaItemStack of(String id, int count) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null)
            return null;
        return new LuaItemStack(new ItemStack(item, count));
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaItemStack");

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "getItemId",
                "Returns the registry ID of the item.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "e.g., 'minecraft:stone'")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "getCount",
                "Returns the number of items in the stack.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The stack count")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setCount",
                "Sets the number of items in the stack.",
                Arrays.asList(new LuaDocRegistry.Param("count", "number", "The new item count")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "isEmpty",
                "Returns true if the item stack is empty.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true));
    }
}
