package com.shawnjb.luacraft;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ChatCommandHandler {
    private static final String PREFIX = ":";

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(PREFIX)) return;

        event.setCanceled(true); // prevent chat broadcast

        String[] parts = message.substring(1).split(" ", 2); // remove ":" prefix
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "runscript":
                LuaManager.runScript(arg);
                event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] Script executed."));
                break;

            case "loadscript":
                File file = new File(LuaManager.getScriptsFolder(), arg);
                if (file.exists() && file.isFile()) {
                    LuaManager.runScript(file);
                    event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] Loaded script: " + arg));
                } else {
                    event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] Script not found: " + arg));
                }
                break;

            case "listscripts":
                File[] files = LuaManager.getScriptsFolder().listFiles((dir, name) -> name.endsWith(".lua"));
                if (files == null || files.length == 0) {
                    event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] No scripts found."));
                } else {
                    String list = Arrays.stream(files)
                            .map(File::getName)
                            .collect(Collectors.joining(", "));
                    event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] Scripts: " + list));
                }
                break;

            default:
                event.getPlayer().sendMessage(new TextComponentString("[LuaCraft] Unknown command: " + cmd));
                break;
        }
    }
}
