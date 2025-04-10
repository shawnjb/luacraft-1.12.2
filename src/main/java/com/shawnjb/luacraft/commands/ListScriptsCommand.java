package com.shawnjb.luacraft.commands;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ListScriptsCommand extends CommandBase {

    @Override
    public String getName() {
        return "listscripts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/listscripts";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        File[] files = LuaManager.getScriptsFolder().listFiles((dir, name) -> name.endsWith(".lua"));
        if (files == null || files.length == 0) {
            sender.sendMessage(new TextComponentString("[LuaCraft] No scripts found."));
        } else {
            String list = Arrays.stream(files)
                    .map(File::getName)
                    .collect(Collectors.joining(", "));
            sender.sendMessage(new TextComponentString("[LuaCraft] Scripts: " + list));
        }
    }
}
