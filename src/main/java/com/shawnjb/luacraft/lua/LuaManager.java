package com.shawnjb.luacraft.lua;

import com.shawnjb.luacraft.lua.core.LuaMc;

import net.minecraft.entity.player.EntityPlayerMP;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.net.URL;
import java.net.URLDecoder;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LuaManager {
    private static Globals globals;
    private static File scriptsFolder;
    private static File autorunFolder;

    public static File getScriptsFolder() {
        return scriptsFolder;
    }

    public static void init(File configDir) {
        globals = JsePlatform.standardGlobals();

        scriptsFolder = new File(configDir, "luacraft/scripts");
        autorunFolder = new File(scriptsFolder, "autorun");

        System.out.println("[LuaCraft] Scripts Folder: " + scriptsFolder.getAbsolutePath());
        System.out.println("[LuaCraft] Autorun Folder: " + autorunFolder.getAbsolutePath());

        if (!scriptsFolder.exists()) {
            System.out.println("[LuaCraft] Copying default scripts...");
            copyEmbeddedScriptFolder("scripts/", scriptsFolder);
        } else {
            System.out.println("[LuaCraft] Script directory exists, skipping default copy.");
        }

        runAutorunScripts();
    }

    private static void copyEmbeddedScriptFolder(String resourcePath, File destination) {
        try {
            URL root = LuaManager.class.getClassLoader().getResource(resourcePath);
            if (root == null) {
                System.err.println("[LuaCraft] Cannot find resource path: " + resourcePath);
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
                            System.err.println("[LuaCraft] Failed to copy " + path + ": " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[LuaCraft] Failed to copy embedded scripts: " + e.getMessage());
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
            System.out.println("[LuaCraft] Running autorun script: " + file.getName());
            runScript(file);
        }
    }

    public static void runScript(File file) {
        runScript(file, null);
    }    

    public static void runScript(String code) {
        if (globals == null) {
            System.err.println("[LuaCraft] LuaManager not initialized!");
            return;
        }

        try {
            LuaValue chunk = globals.load(code, "script");
            chunk.call();
        } catch (Exception e) {
            System.err.println("[LuaCraft] Lua error while running code:\n" + code);
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

            Globals context = JsePlatform.standardGlobals();
            context.set("mc", new LuaMc(sender != null ? new LuaPlayer(sender) : null));

            LuaValue chunk = context.load(builder.toString(), file.getName());
            chunk.call();
        } catch (IOException e) {
            System.err.println("[LuaCraft] Failed to run script '" + file.getName() + "': " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[LuaCraft] Error executing script file: " + file.getName());
            e.printStackTrace();
        }
    }

    public static Globals getGlobals() {
        return globals;
    }
}
