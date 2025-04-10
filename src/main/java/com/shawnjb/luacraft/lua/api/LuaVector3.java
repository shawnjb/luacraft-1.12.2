package com.shawnjb.luacraft.lua.api;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaVector3 extends LuaTable {
    public double x, y, z;

    public LuaVector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        set("x", LuaValue.valueOf(x));
        set("y", LuaValue.valueOf(y));
        set("z", LuaValue.valueOf(z));

        set("add", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue other) {
                LuaVector3 vec = fromLuaTable(other.checktable());
                return new LuaVector3(x + vec.x, y + vec.y, z + vec.z);
            }
        });

        set("sub", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue other) {
                LuaVector3 vec = fromLuaTable(other.checktable());
                return new LuaVector3(x - vec.x, y - vec.y, z - vec.z);
            }
        });

        set("scale", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue factor) {
                double f = factor.checkdouble();
                return new LuaVector3(x * f, y * f, z * f);
            }
        });

        set("length", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Math.sqrt(x * x + y * y + z * z));
            }
        });

        set("normalize", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                double len = Math.sqrt(x * x + y * y + z * z);
                if (len == 0)
                    return new LuaVector3(0, 0, 0);
                return new LuaVector3(x / len, y / len, z / len);
            }
        });

        set("distanceTo", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue other) {
                LuaVector3 vec = fromLuaTable(other.checktable());
                double dx = vec.x - x;
                double dy = vec.y - y;
                double dz = vec.z - z;
                return LuaValue.valueOf(Math.sqrt(dx * dx + dy * dy + dz * dz));
            }
        });

        set("toBlockPos", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable tbl = new LuaTable();
                tbl.set("x", (int) Math.floor(x));
                tbl.set("y", (int) Math.floor(y));
                tbl.set("z", (int) Math.floor(z));
                return tbl;
            }
        });
    }

    public static class NewVectorFunction extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue lx, LuaValue ly, LuaValue lz) {
            return new LuaVector3(lx.checkdouble(), ly.checkdouble(), lz.checkdouble());
        }
    }

    public static void registerGlobal(LuaTable globals) {
        globals.set("Vector3", new LuaTable() {
            {
                set("new", new NewVectorFunction());
            }
        });
    }

    public static LuaVector3 fromLuaTable(LuaTable table) {
        double x = table.get("x").checkdouble();
        double y = table.get("y").checkdouble();
        double z = table.get("z").checkdouble();
        return new LuaVector3(x, y, z);
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("Vector3", "A 3D vector with x, y, z and math methods.");

        LuaDocRegistry.addFunction("Vector3", new LuaDocRegistry.FunctionDoc(
                "new",
                "Creates a new 3D vector.",
                Arrays.asList(
                        new LuaDocRegistry.Param("x", "number", "X coordinate"),
                        new LuaDocRegistry.Param("y", "number", "Y coordinate"),
                        new LuaDocRegistry.Param("z", "number", "Z coordinate")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The new vector instance")),
                false));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "add",
                "Returns the sum of this vector and another vector.",
                Arrays.asList(new LuaDocRegistry.Param("other", "Vector3", "The vector to add")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The result of the addition")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "sub",
                "Returns the difference between this vector and another vector.",
                Arrays.asList(new LuaDocRegistry.Param("other", "Vector3", "The vector to subtract")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The result of the subtraction")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "scale",
                "Scales this vector by a factor.",
                Arrays.asList(new LuaDocRegistry.Param("factor", "number", "The multiplier")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The scaled vector")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "length",
                "Returns the length (magnitude) of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The length of the vector")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "normalize",
                "Returns a unit vector (normalized version).",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The normalized vector")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "distanceTo",
                "Returns the Euclidean distance to another vector.",
                Arrays.asList(new LuaDocRegistry.Param("other", "Vector3", "The target vector")),
                Arrays.asList(new LuaDocRegistry.Return("number", "The distance between the vectors")),
                true));

        LuaDocRegistry.addMethod("Vector3", new LuaDocRegistry.FunctionDoc(
                "toBlockPos",
                "Returns an integer table for block position rounding down x/y/z.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("table", "A table with integer x, y, z")),
                true));
    }
}