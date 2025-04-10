package com.shawnjb.luacraft;

import com.shawnjb.luacraft.docs.LuaDocBootstrap;
import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.io.File;

@Mod(modid = LuaCraft.MODID, name = LuaCraft.NAME, version = LuaCraft.VERSION)
public class LuaCraft {
    public static final String MODID = "luacraft";
    public static final String NAME = "LuaCraft";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[" + NAME + "] Pre-Initialization");

        File configDir = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDir.exists()) configDir.mkdirs();

        LuaManager.init(configDir);
        System.out.println("[" + NAME + "] LuaManager initialized");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[" + NAME + "] Initialization");

        LuaDocBootstrap.registerAll();
        System.out.println("[" + NAME + "] Lua documentation registered");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("[" + NAME + "] Post-Initialization");

        // Any additional post-setup logic can go here.
    }
}
