package com.shawnjb.luacraft.docs;

import com.shawnjb.luacraft.lua.core.LuaMc;
import com.shawnjb.luacraft.lua.LuaPlayer;
import com.shawnjb.luacraft.lua.LuaWorld;
import com.shawnjb.luacraft.lua.api.LuaVector3;
import com.shawnjb.luacraft.lua.api.LuaMaterial;
import com.shawnjb.luacraft.lua.api.LuaItemStack;
import com.shawnjb.luacraft.lua.LuaEntity;
import com.shawnjb.luacraft.lua.LuaEvent;
import com.shawnjb.luacraft.lua.LuaBlock;

public class LuaDocBootstrap {
    public static void registerAll() {
        LuaMc.registerDocs();
        LuaPlayer.registerDocs();
        LuaWorld.registerDocs();
        LuaVector3.registerDocs();
        LuaMaterial.registerDocs();
        LuaItemStack.registerDocs();
        LuaEntity.registerDocs();
        LuaBlock.registerDocs();
        LuaEvent.registerDocs();
    }
}
