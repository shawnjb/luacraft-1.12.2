package com.shawnjb.luacraft.lua.api;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

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

        set("getDamage", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.getItemDamage());
            }
        });

        set("setDamage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                stack.setItemDamage(arg.checkint());
                return LuaValue.NIL;
            }
        });

        set("getDisplayName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.getDisplayName());
            }
        });

        set("setUsername", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                if (stack.getItem() == Items.SKULL && stack.getItemDamage() == 3) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag == null)
                        tag = new NBTTagCompound();
                    NBTTagCompound skullOwner = new NBTTagCompound();
                    skullOwner.setString("Name", name.checkjstring());
                    tag.setTag("SkullOwner", skullOwner);
                    stack.setTagCompound(tag);
                    return LuaValue.TRUE;
                } else {
                    return LuaValue.error("setUsername can only be used on player heads (SKULL with damage 3)");
                }
            }
        });

        set("addEnchantment", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue enchantIdVal, LuaValue levelVal) {
                String enchId = enchantIdVal.checkjstring();
                int level = levelVal.checkint();
                Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchId);
                if (enchantment == null) {
                    return LuaValue.error("Unknown enchantment ID: " + enchId);
                }
                stack.addEnchantment(enchantment, level);
                return LuaValue.TRUE;
            }
        });

        set("modifyEnchantment", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue enchantIdVal, LuaValue newLevelVal) {
                String enchId = enchantIdVal.checkjstring();
                int newLevel = newLevelVal.checkint();
                Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchId);
                if (enchantment == null) {
                    return LuaValue.error("Unknown enchantment ID: " + enchId);
                }
                NBTTagList enchList = stack.getEnchantmentTagList();
                if (enchList == null) {
                    return LuaValue.error("No enchantments to modify.");
                }
                boolean found = false;
                for (int i = 0; i < enchList.tagCount(); i++) {
                    NBTTagCompound enchCompound = enchList.getCompoundTagAt(i);
                    short idShort = enchCompound.getShort("id");
                    Enchantment ench = Enchantment.getEnchantmentByID(idShort);
                    if (ench == enchantment) {
                        enchCompound.setShort("lvl", (short) newLevel);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return LuaValue.error("Enchantment " + enchId + " not found on this item.");
                }
                return LuaValue.TRUE;
            }
        });

        set("removeEnchantment", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue enchantIdVal) {
                String enchId = enchantIdVal.checkjstring();
                Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchId);
                if (enchantment == null) {
                    return LuaValue.error("Unknown enchantment ID: " + enchId);
                }
                NBTTagList enchList = stack.getEnchantmentTagList();
                if (enchList == null) {
                    return LuaValue.error("No enchantments to remove.");
                }
                NBTTagList newList = new NBTTagList();
                boolean removed = false;
                for (int i = 0; i < enchList.tagCount(); i++) {
                    NBTTagCompound enchCompound = enchList.getCompoundTagAt(i);
                    short idShort = enchCompound.getShort("id");
                    Enchantment ench = Enchantment.getEnchantmentByID(idShort);
                    if (ench == enchantment) {
                        removed = true;
                        continue;
                    }
                    newList.appendTag(enchCompound.copy());
                }
                if (removed) {
                    if (newList.tagCount() > 0) {
                        stack.setTagInfo("ench", newList);
                    } else if (stack.hasTagCompound()) {
                        stack.getTagCompound().removeTag("ench");
                    }
                    return LuaValue.TRUE;
                } else {
                    return LuaValue.error("Enchantment " + enchId + " not found on this item.");
                }
            }
        });

        set("setDisplayName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String displayName = arg.checkjstring();
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                NBTTagCompound display = tag.getCompoundTag("display");
                display.setString("Name", displayName);
                tag.setTag("display", display);
                stack.setTagCompound(tag);
                return LuaValue.NIL;
            }
        });

        set("setLore", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (!arg.istable()) {
                    return LuaValue.error("Expected a table of lore lines");
                }
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                NBTTagCompound display = tag.getCompoundTag("display");
                if (display == null) {
                    display = new NBTTagCompound();
                }
                NBTTagList loreList = new NBTTagList();
                LuaValue k = LuaValue.NIL;
                while (true) {
                    Varargs next = arg.next(k);
                    k = next.arg1();
                    LuaValue v = next.arg(2);
                    if (k.isnil())
                        break;
                    loreList.appendTag(new NBTTagString(v.checkjstring()));
                }
                display.setTag("Lore", loreList);
                tag.setTag("display", display);
                stack.setTagCompound(tag);
                return LuaValue.NIL;
            }
        });

        set("setBookContent", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (!stack.getItem().equals(Items.WRITTEN_BOOK)) {
                    return LuaValue.error("setBookContent can only be used on written books.");
                }
                if (!arg.istable()) {
                    return LuaValue.error("Expected a table with keys 'title', 'author', and 'pages'");
                }
                String title = arg.get("title").checkjstring();
                String author = arg.get("author").checkjstring();
                LuaValue pagesTable = arg.get("pages");
                if (!pagesTable.istable()) {
                    return LuaValue.error("Expected 'pages' to be a table of strings.");
                }
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                tag.setString("title", title);
                tag.setString("author", author);
                NBTTagList pages = new NBTTagList();
                LuaValue k = LuaValue.NIL;
                while (true) {
                    Varargs next = pagesTable.next(k);
                    k = next.arg1();
                    LuaValue v = next.arg(2);
                    if (k.isnil())
                        break;
                    pages.appendTag(new NBTTagString(v.checkjstring()));
                }
                tag.setTag("pages", pages);
                stack.setTagCompound(tag);
                return LuaValue.TRUE;
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

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "getDamage",
                "Gets the current damage value (durability) of the item.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The damage value")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setDamage",
                "Sets the damage value (durability) of the item.",
                Arrays.asList(new LuaDocRegistry.Param("damage", "number", "The damage value to set")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "getDisplayName",
                "Returns the display name of the item.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The item display name")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setUsername",
                "Sets the username displayed on a player head item.",
                Arrays.asList(new LuaDocRegistry.Param("name", "string", "The Minecraft username to show")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if successful")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "addEnchantment",
                "Adds an enchantment to the item stack using the given registry ID and level.",
                Arrays.asList(
                        new LuaDocRegistry.Param("enchantId", "string",
                                "The registry ID of the enchantment (e.g., 'minecraft:sharpness')"),
                        new LuaDocRegistry.Param("level", "number", "The level of the enchantment")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the enchantment was added")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "modifyEnchantment",
                "Modifies the level of an existing enchantment on the item stack.",
                Arrays.asList(
                        new LuaDocRegistry.Param("enchantId", "string", "The registry ID of the enchantment to modify"),
                        new LuaDocRegistry.Param("newLevel", "number", "The new level to set")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the enchantment was modified")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "removeEnchantment",
                "Removes the enchantment identified by the given registry ID from the item stack.",
                Arrays.asList(new LuaDocRegistry.Param("enchantId", "string",
                        "The registry ID of the enchantment to remove")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the enchantment was removed")),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setDisplayName",
                "Sets the display name of the item. The name is stored in the item's display tag.",
                Arrays.asList(new LuaDocRegistry.Param("name", "string", "The new display name for the item")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setLore",
                "Sets the lore for the item. Expects a table of strings, where each string is a lore line.",
                Arrays.asList(
                        new LuaDocRegistry.Param("lore", "table", "A table of strings representing the lore lines")),
                Arrays.asList(),
                true));

        LuaDocRegistry.addMethod("LuaItemStack", new LuaDocRegistry.FunctionDoc(
                "setBookContent",
                "Sets the content of a written book. Expects a table with 'title', 'author', and 'pages' keys. The 'pages' key should be a table of strings, each representing a page's text.",
                Arrays.asList(new LuaDocRegistry.Param("bookInfo", "table",
                        "Table with keys 'title' (string), 'author' (string), and 'pages' (table of strings)")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the book content was successfully set")),
                true));
    }
}