package com.shawnjb.luacraft.lua;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import com.shawnjb.luacraft.LuaLogger;
import net.minecraft.world.World;

@Mod.EventBusSubscriber(modid = "luacraft")
public class WorldEventListener {

    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        LuaLogger.LOGGER.info("[LuaCraft] World loaded: " + world.getWorldInfo().getWorldName());
        LuaManager.resetLuaState();
    }

    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        LuaLogger.LOGGER.info("[LuaCraft] World unloaded: " + world.getWorldInfo().getWorldName());
        LuaManager.stopAllRunningThreads();
    }
}
