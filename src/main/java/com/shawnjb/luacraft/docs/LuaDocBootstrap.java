package com.shawnjb.luacraft.docs;

import com.shawnjb.luacraft.lua.core.LuaMc;
import com.shawnjb.luacraft.lua.LuaPlayer;
import com.shawnjb.luacraft.lua.LuaWorld;
import com.shawnjb.luacraft.lua.api.LuaMaterial;
import com.shawnjb.luacraft.lua.api.LuaItemStack;
import com.shawnjb.luacraft.lua.LuaEntity;
import com.shawnjb.luacraft.lua.LuaEvent;
import com.shawnjb.luacraft.lua.LuaBlock;

public class LuaDocBootstrap {
    public static void registerAll() {
        LuaMc.registerDocs();
        LuaEntity.registerDocs();
        LuaPlayer.registerDocs();
        LuaWorld.registerDocs();
        LuaMaterial.registerDocs();
        LuaItemStack.registerDocs();
        LuaBlock.registerDocs();
        LuaEvent.registerDocs();
    }
}
