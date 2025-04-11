package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.LuaLogger;
import com.shawnjb.luacraft.lua.core.LuaMc;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LuaManager {
    private static Globals globals;
    private static File scriptsFolder;
    private static File autorunFolder;

    // Mapping of world instances to Lua environments (active scripts for each world)
    private static final List<World> activeWorlds = new ArrayList<>();

    public static File getScriptsFolder() {
        return scriptsFolder;
    }

    public static void init(File configDir) {
        // Initialize Lua Globals here
        resetGlobals();

        scriptsFolder = new File(configDir, "luacraft/scripts");
        autorunFolder = new File(scriptsFolder, "autorun");

        LuaLogger.LOGGER.info("[LuaCraft] Scripts Folder: " + scriptsFolder.getAbsolutePath());
        LuaLogger.LOGGER.info("[LuaCraft] Autorun Folder: " + autorunFolder.getAbsolutePath());

        if (!scriptsFolder.exists()) {
            LuaLogger.LOGGER.info("[LuaCraft] Copying default scripts...");
            copyEmbeddedScriptFolder("scripts/", scriptsFolder);
        } else {
            LuaLogger.LOGGER.info("[LuaCraft] Script directory exists, skipping default copy.");
        }

        runAutorunScripts();
    }

    // Reset Lua Globals to a fresh state
    public static void resetGlobals() {
        globals = JsePlatform.standardGlobals();
        // Reinitialize necessary functions after resetting globals
        globals.set("print", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                StringBuilder output = new StringBuilder();
                for (int i = 1; i <= args.narg(); i++) {
                    if (i > 1) output.append("\t");
                    output.append(args.arg(i).tojstring());
                }
                LuaLogger.LOGGER.info(output.toString());
                return LuaValue.NIL;
            }
        });

        globals.set("wait", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                double seconds = arg.checkdouble();
                try {
                    Thread.sleep((long) (seconds * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return LuaValue.NIL;
            }
        });
    }

    private static void copyEmbeddedScriptFolder(String resourcePath, File destination) {
        try {
            URL root = LuaManager.class.getClassLoader().getResource(resourcePath);
            if (root == null) {
                LuaLogger.LOGGER.error("[LuaCraft] Cannot find resource path: " + resourcePath);
                return;
            }

            if (root.getProtocol().equals("jar")) {
                String jarPath = root.getPath().substring(5, root.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(resourcePath) && !entry.isDirectory()) {
                            String relativePath = entry.getName().substring(resourcePath.length());
                            InputStream in = jar.getInputStream(entry);
                            writeStreamToFile(in, new File(destination, relativePath));
                        }
                    }
                }
            } else {
                Path resourceDir = Paths.get(root.toURI());
                Files.walk(resourceDir).forEach(path -> {
                    if (Files.isRegularFile(path)) {
                        try {
                            Path relative = resourceDir.relativize(path);
                            File outFile = new File(destination, relative.toString());
                            Files.createDirectories(outFile.getParentFile().toPath());
                            Files.copy(path, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            LuaLogger.LOGGER.error("[LuaCraft] Failed to copy " + path + ": " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Failed to copy embedded scripts: " + e.getMessage());
        }
    }

    private static void writeStreamToFile(InputStream in, File outFile) throws IOException {
        outFile.getParentFile().mkdirs();
        try (OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    private static void runAutorunScripts() {
        if (!autorunFolder.exists())
            return;

        File[] files = autorunFolder.listFiles((dir, name) -> name.endsWith(".lua"));
        if (files == null)
            return;

        for (File file : files) {
            LuaLogger.LOGGER.info("[LuaCraft] Running autorun script: " + file.getName());
            runScript(file);
        }
    }

    public static void runScript(File file) {
        runScript(file, null);
    }

    public static void runScript(String code) {
        if (globals == null) {
            LuaLogger.LOGGER.error("[LuaCraft] LuaManager not initialized!");
            return;
        }

        try {
            LuaValue chunk = globals.load(code, "script");
            chunk.call();
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Lua error while running code:\n" + code);
            e.printStackTrace();
        }
    }

    public static void runScript(String code, EntityPlayerMP sender) {
        Globals context = globals;
        context.set("mc", new LuaMc(sender != null ? new LuaPlayer(sender) : null));

        try {
            LuaValue chunk = context.load(code, "inline");
            chunk.call();
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Error executing inline script:");
            e.printStackTrace();
        }
    }

    public static void runScript(File file, EntityPlayerMP sender) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            Globals context = globals;
            context.set("mc", new LuaMc(sender != null ? new LuaPlayer(sender) : null));

            LuaValue chunk = context.load(builder.toString(), file.getName());
            chunk.call();
        } catch (IOException e) {
            LuaLogger.LOGGER.error("[LuaCraft] Failed to run script '" + file.getName() + "': " + e.getMessage());
        } catch (Exception e) {
            LuaLogger.LOGGER.error("[LuaCraft] Error executing script file: " + file.getName(), e);
        }
    }

    public static void unloadPlayerScripts(EntityPlayerMP player) {
        // Unload player-specific Lua context
        LuaLogger.LOGGER.info("[LuaCraft] Unloaded Lua scripts for player: " + player.getName());
    }

    // New method to unload all Lua scripts tied to a world
    public static void unloadWorldScripts(World world) {
        // Here you can iterate through all active scripts related to this world and unload them
        // Example: remove any player-specific contexts or unload global scripts
        activeWorlds.remove(world);
        LuaLogger.LOGGER.info("[LuaCraft] Unloaded Lua scripts for world: " + world.getWorldInfo().getWorldName());
    }

    public static Globals getGlobals() {
        return globals;
    }
}
