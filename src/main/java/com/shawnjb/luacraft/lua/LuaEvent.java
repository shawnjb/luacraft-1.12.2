package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Arrays;

public class LuaEvent extends LuaTable {
    @SuppressWarnings("unused")
    private final Runnable unregister;

    public LuaEvent(Runnable unregister) {
        this.unregister = unregister;

        set("disconnect", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                unregister.run();
                return LuaValue.NIL;
            }
        });
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaEvent");

        LuaDocRegistry.addMethod("LuaEvent", new LuaDocRegistry.FunctionDoc(
                "disconnect",
                "Stops the event listener from receiving further events.",
                Arrays.asList(),
                Arrays.asList(),
                true));
    }
}
