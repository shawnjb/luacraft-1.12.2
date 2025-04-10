package com.shawnjb.luacraft.docs;

import com.shawnjb.luacraft.lua.core.LuaMc;
import com.shawnjb.luacraft.lua.LuaPlayer;
import com.shawnjb.luacraft.lua.LuaWorld;
import com.shawnjb.luacraft.lua.api.LuaVector3;
import com.shawnjb.luacraft.lua.api.LuaMaterial;
import com.shawnjb.luacraft.lua.api.LuaItemStack;
import com.shawnjb.luacraft.lua.LuaEntity;
import com.shawnjb.luacraft.lua.LuaEvent;

import java.util.Arrays;

import com.shawnjb.luacraft.lua.LuaBlock;

public class LuaDocBootstrap {
    public static void registerAll() {
        LuaMc.registerDocs();
        LuaEntity.registerDocs();
        LuaPlayer.registerDocs();
        LuaWorld.registerDocs();
        LuaVector3.registerDocs();
        LuaMaterial.registerDocs();
        LuaItemStack.registerDocs();
        LuaBlock.registerDocs();
        LuaEvent.registerDocs();

        LuaDocRegistry.addFunction("core", new LuaDocRegistry.FunctionDoc(
                "wait",
                "Pauses script execution for the given number of seconds.",
                Arrays.asList(new LuaDocRegistry.Param("seconds", "number", "The number of seconds to wait")),
                Arrays.asList(),
                false));

        LuaDocRegistry.addFunction("core", new LuaDocRegistry.FunctionDoc(
                "waitTicks",
                "Pauses script execution for the given number of ticks (1 tick = 50 ms).",
                Arrays.asList(new LuaDocRegistry.Param("ticks", "number", "The number of ticks to wait")),
                Arrays.asList(),
                false));
    }
}
