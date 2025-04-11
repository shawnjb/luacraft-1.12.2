package com.shawnjb.luacraft;

import com.shawnjb.luacraft.commands.ListScriptsCommand;
import com.shawnjb.luacraft.commands.LoadScriptCommand;
import com.shawnjb.luacraft.commands.RunScriptCommand;
import com.shawnjb.luacraft.docs.LuaDocBootstrap;
import com.shawnjb.luacraft.lua.LuaManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Mod(modid = LuaCraft.MODID, name = LuaCraft.NAME, version = LuaCraft.VERSION)
public class LuaCraft {
    public static final String MODID = "luacraft";
    public static final String NAME = "LuaCraft";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LuaLogger.setupFileLogging();
        LuaLogger.LOGGER.info("LuaCraft logging initialized.");

        File configDir = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDir.exists()) configDir.mkdirs();

        LuaManager.init(configDir);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LuaDocBootstrap.registerAll();
        new WorldUnloadHandler();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new RunScriptCommand());
        event.registerServerCommand(new LoadScriptCommand());
        event.registerServerCommand(new ListScriptsCommand());
    }

    @Mod.EventHandler
    public void onServerStopping(net.minecraftforge.fml.common.event.FMLServerStoppingEvent event) {
        LuaManager.unloadAllScripts();
    }    
}
