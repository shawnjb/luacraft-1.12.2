package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.LuaLogger;
import com.shawnjb.luacraft.lua.core.LuaMc;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LuaManager {
    private static Globals globals;
    private static File scriptsFolder;
    private static final List<LuaModule> loadedScripts = new ArrayList<>();

    public static void init(File configDir) {
        resetGlobals();
        scriptsFolder = new File(configDir, "scripts");

        LuaLogger.LOGGER.info("[LuaCraft] Scripts Folder: " + scriptsFolder.getAbsolutePath());

        if (!scriptsFolder.exists()) {
            boolean created = scriptsFolder.mkdirs();
            if (created) {
                LuaLogger.LOGGER.info("[LuaCraft] Created script directory: " + scriptsFolder.getAbsolutePath());
                copyDefaultScripts();
            } else {
                LuaLogger.LOGGER.error("[LuaCraft] Failed to create script directory: " + scriptsFolder.getAbsolutePath());
            }
        } else if (scriptsFolder.isDirectory() && scriptsFolder.listFiles().length == 0) {
            copyDefaultScripts();
        }
    }

    private static void copyDefaultScripts() {
        String[] defaultScripts = {"example.lua"};

        for (String filename : defaultScripts) {
            try (InputStream in = LuaManager.class.getResourceAsStream("/scripts/" + filename)) {
                if (in == null) {
                    LuaLogger.LOGGER.warn("[LuaCraft] Default script not found in resources: " + filename);
                    continue;
                }

                File outFile = new File(scriptsFolder, filename);
                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    LuaLogger.LOGGER.info("[LuaCraft] Copied default script: " + filename);
                }
            } catch (IOException e) {
                LuaLogger.LOGGER.error("[LuaCraft] Failed to copy default script: " + filename, e);
            }
        }
    }

    public static void resetGlobals() {
        globals = JsePlatform.standardGlobals();
        globals.set("mc", new LuaMc(null));
        globals.set("sender", LuaValue.NIL);
    }

    public static File getScriptsFolder() {
        return scriptsFolder;
    }

    public static void loadScriptForPlayer(File file, EntityPlayerMP player) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            Globals context = globals;
            LuaMc mc = new LuaMc(new LuaPlayer(player));
            context.set("mc", mc);
            context.set("sender", new LuaPlayer(player));

            LuaValue chunk = context.load(builder.toString(), file.getName());
            LuaValue result = chunk.call();
            LuaModule module = new LuaModule(result, player);
            loadedScripts.add(module);

            if (module.hasOnScriptLoaded()) {
                module.onScriptLoaded(new LuaPlayer(player));
            } else {
                LuaLogger.LOGGER.warn("[LuaCraft] Script " + module.getName() + " does not have an onScriptLoaded method.");
            }

        } catch (IOException e) {
            LuaLogger.LOGGER.error("[LuaCraft] Failed to load script '" + file.getName() + "': " + e.getMessage());
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Error executing script file: " + file.getName(), e);
        }
    }

    public static void loadScript(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            Globals context = globals;
            context.set("mc", new LuaMc(null));
            context.set("sender", LuaValue.NIL);

            LuaValue chunk = context.load(builder.toString(), file.getName());
            LuaValue result = chunk.call();
            LuaModule module = new LuaModule(result, null);
            loadedScripts.add(module);

            if (module.hasOnScriptLoaded()) {
                module.onScriptLoaded();
            } else {
                LuaLogger.LOGGER.warn("[LuaCraft] Script " + module.getName() + " does not have an onScriptLoaded method.");
            }

        } catch (IOException e) {
            LuaLogger.LOGGER.error("[LuaCraft] Failed to load script '" + file.getName() + "': " + e.getMessage());
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Error executing script file: " + file.getName(), e);
        }
    }

    public static void unloadWorldScripts(World world) {
        LuaLogger.LOGGER.info("[LuaCraft] Unloading Lua scripts for world: " + world.getWorldInfo().getWorldName());

        for (LuaModule script : loadedScripts) {
            if (script.hasOnWorldUnload()) {
                try {
                    script.onWorldUnload();
                } catch (Exception e) {
                    LuaLogger.LOGGER.error("[LuaCraft] Error executing onWorldUnload for script " + script.getName(), e);
                }
            }

            if (script.hasGarbageCollect()) {
                try {
                    script.garbageCollect();
                } catch (Exception e) {
                    LuaLogger.LOGGER.error("[LuaCraft] Error executing garbageCollect for script " + script.getName(), e);
                }
            }
        }
    }

    public static void unloadAllScripts() {
        LuaLogger.LOGGER.info("[LuaCraft] Unloading all Lua scripts (server stopping)");
        LuaLogger.LOGGER.info("[LuaCraft] Loaded script count: " + loadedScripts.size());

        for (LuaModule script : loadedScripts) {
            if (script.hasOnWorldUnload()) {
                try {
                    script.onWorldUnload();
                } catch (Exception e) {
                    LuaLogger.LOGGER.error("[LuaCraft] Error executing onWorldUnload for script " + script.getName(), e);
                }
            }

            if (script.hasGarbageCollect()) {
                try {
                    script.garbageCollect();
                } catch (Exception e) {
                    LuaLogger.LOGGER.error("[LuaCraft] Error executing garbageCollect for script " + script.getName(), e);
                }
            }
        }

        loadedScripts.clear();
        resetGlobals();
    }

    public static Globals getGlobals() {
        return globals;
    }
}
