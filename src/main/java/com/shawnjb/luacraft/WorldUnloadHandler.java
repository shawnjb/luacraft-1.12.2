package com.shawnjb.luacraft;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class WorldUnloadHandler {
    public WorldUnloadHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        LuaManager.unloadWorldScripts(event.getWorld());
    }
}
