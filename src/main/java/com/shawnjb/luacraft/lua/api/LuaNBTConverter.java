package com.shawnjb.luacraft.lua.api;

import net.minecraft.nbt.*;
import org.luaj.vm2.*;

public class LuaNBTConverter {

    /**
     * Converts a Lua table into a NBTTagCompound.
     */
    public static NBTTagCompound fromLua(LuaTable table) {
        NBTTagCompound compound = new NBTTagCompound();

        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs next = table.next(key);
            key = next.arg1();
            if (key.isnil()) break;

            String tagName = key.tojstring();
            LuaValue value = next.arg(2);

            if (value.isboolean()) {
                compound.setBoolean(tagName, value.toboolean());
            } else if (value.isint()) {
                compound.setInteger(tagName, value.toint());
            } else if (value.islong()) {
                compound.setLong(tagName, value.tolong());
            } else if (value.isnumber()) {
                compound.setDouble(tagName, value.todouble());
            } else if (value.isstring()) {
                compound.setString(tagName, value.tojstring());
            } else if (value.istable()) {
                LuaTable child = value.checktable();

                if (isArrayTable(child)) {
                    NBTTagList list = new NBTTagList();
                    LuaValue index = LuaValue.NIL;
                    while (true) {
                        Varargs entry = child.next(index);
                        index = entry.arg1();
                        if (index.isnil()) break;

                        LuaValue item = entry.arg(2);
                        list.appendTag(primitiveToNBT(item));
                    }
                    compound.setTag(tagName, list);
                } else {
                    compound.setTag(tagName, fromLua(child));
                }
            }
        }

        return compound;
    }

    /**
     * Converts an NBTTagCompound into a Lua table.
     */
    public static LuaTable toLua(NBTTagCompound tag) {
        LuaTable table = new LuaTable();
        for (String key : tag.getKeySet()) {
            NBTBase base = tag.getTag(key);

            switch (base.getId()) {
                case 1: // byte
                    table.set(key, LuaValue.valueOf(((NBTTagByte) base).getByte() != 0));
                    break;
                case 2: // short
                    table.set(key, LuaValue.valueOf(((NBTTagShort) base).getShort()));
                    break;
                case 3: // int
                    table.set(key, LuaValue.valueOf(((NBTTagInt) base).getInt()));
                    break;
                case 4: // long
                    table.set(key, LuaValue.valueOf(((NBTTagLong) base).getLong()));
                    break;
                case 5: // float
                    table.set(key, LuaValue.valueOf(((NBTTagFloat) base).getFloat()));
                    break;
                case 6: // double
                    table.set(key, LuaValue.valueOf(((NBTTagDouble) base).getDouble()));
                    break;
                case 8: // string
                    table.set(key, LuaValue.valueOf(((NBTTagString) base).getString()));
                    break;
                case 10: // compound
                    table.set(key, toLua((NBTTagCompound) base));
                    break;
                case 9: // list
                    LuaTable list = new LuaTable();
                    NBTTagList nbtList = (NBTTagList) base;
                    for (int i = 0; i < nbtList.tagCount(); i++) {
                        NBTBase listEntry = nbtList.get(i);
                        list.set(i + 1, primitiveNBTToLua(listEntry));
                    }
                    table.set(key, list);
                    break;
                default:
                    // Unsupported or complex types like byte[], int[], long[]
                    table.set(key, LuaValue.NIL);
            }
        }
        return table;
    }

    private static boolean isArrayTable(LuaTable table) {
        LuaValue k = LuaValue.NIL;
        int count = 0;
        while (true) {
            Varargs next = table.next(k);
            k = next.arg1();
            if (k.isnil()) break;
            if (!k.isnumber()) return false;
            count++;
        }
        return count > 0;
    }

    private static NBTBase primitiveToNBT(LuaValue value) {
        if (value.isboolean()) {
            return new NBTTagByte((byte) (value.toboolean() ? 1 : 0));
        } else if (value.isint()) {
            return new NBTTagInt(value.toint());
        } else if (value.islong()) {
            return new NBTTagLong(value.tolong());
        } else if (value.isnumber()) {
            return new NBTTagDouble(value.todouble());
        } else if (value.isstring()) {
            return new NBTTagString(value.tojstring());
        } else {
            return new NBTTagString(value.tojstring()); // fallback
        }
    }

    private static LuaValue primitiveNBTToLua(NBTBase base) {
        switch (base.getId()) {
            case 1: return LuaValue.valueOf(((NBTTagByte) base).getByte() != 0);
            case 2: return LuaValue.valueOf(((NBTTagShort) base).getShort());
            case 3: return LuaValue.valueOf(((NBTTagInt) base).getInt());
            case 4: return LuaValue.valueOf(((NBTTagLong) base).getLong());
            case 5: return LuaValue.valueOf(((NBTTagFloat) base).getFloat());
            case 6: return LuaValue.valueOf(((NBTTagDouble) base).getDouble());
            case 8: return LuaValue.valueOf(((NBTTagString) base).getString());
            case 10: return toLua((NBTTagCompound) base);
            default: return LuaValue.NIL;
        }
    }
}
