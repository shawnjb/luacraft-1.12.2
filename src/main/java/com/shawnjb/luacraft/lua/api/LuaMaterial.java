package com.shawnjb.luacraft.lua.api;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import net.minecraft.block.material.Material;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaMaterial extends LuaTable {
    private final Material material;

    public LuaMaterial(Material material) {
        this.material = material;

        set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(material.toString()); // fallback if name not available
            }
        });

        set("isLiquid", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(material.isLiquid());
            }
        });

        set("isSolid", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(material.isSolid());
            }
        });

        set("isReplaceable", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(material.isReplaceable());
            }
        });
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaMaterial");

        LuaDocRegistry.addMethod("LuaMaterial", new LuaDocRegistry.FunctionDoc(
                "getName",
                "Returns the material name (fallback via toString).",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Material name")),
                true
        ));

        LuaDocRegistry.addMethod("LuaMaterial", new LuaDocRegistry.FunctionDoc(
                "isLiquid",
                "Returns true if the material is a liquid.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true
        ));

        LuaDocRegistry.addMethod("LuaMaterial", new LuaDocRegistry.FunctionDoc(
                "isSolid",
                "Returns true if the material is solid.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true
        ));

        LuaDocRegistry.addMethod("LuaMaterial", new LuaDocRegistry.FunctionDoc(
                "isReplaceable",
                "Returns true if the material can be replaced (e.g. tall grass, fluids).",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "")),
                true
        ));
    }

    public Material getHandle() {
        return material;
    }
}
