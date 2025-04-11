package com.shawnjb.luacraft.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaUtils {
    public static double[] unpackXYZ(LuaValue table) {
        if (!table.istable()) return null;
    
        LuaValue x = table.get("x");
        LuaValue y = table.get("y");
        LuaValue z = table.get("z");
    
        if (!x.isnumber() || !y.isnumber() || !z.isnumber()) {
            return null;
        }
    
        return new double[]{ x.todouble(), y.todouble(), z.todouble() };
    }
    
    public static LuaTable makeXYZ(double x, double y, double z) {
        LuaTable tbl = new LuaTable();
        tbl.set(LuaValue.valueOf("x"), LuaValue.valueOf(x));
        tbl.set(LuaValue.valueOf("y"), LuaValue.valueOf(y));
        tbl.set(LuaValue.valueOf("z"), LuaValue.valueOf(z));
        return tbl;
    }    
}
