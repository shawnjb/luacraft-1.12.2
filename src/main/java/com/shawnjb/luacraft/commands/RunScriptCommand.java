package com.shawnjb.luacraft.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class RunScriptCommand extends CommandBase {

    @Override
    public String getName() {
        return "runscript";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/runscript <scriptName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Usage: /runscript <scriptName>"));
            return;
        }

        String scriptName = args[0];
        File file = new File(LuaManager.getScriptsFolder(), scriptName);

        if (file.exists() && file.isFile()) {
            StringBuilder scriptContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    scriptContent.append(line).append("\n");
                }
            } catch (IOException e) {
                sender.sendMessage(new TextComponentString("[LuaCraft] Error reading script file: " + e.getMessage()));
                return;
            }

            if (sender instanceof EntityPlayerMP) {
                LuaManager.runScriptForPlayer(scriptContent.toString(), (EntityPlayerMP) sender);
            } else {
                LuaManager.runScript(scriptContent.toString());
            }

            sender.sendMessage(new TextComponentString("[LuaCraft] Loaded and executed script: " + scriptName));
        } else {
            sender.sendMessage(new TextComponentString("[LuaCraft] Script not found: " + scriptName));
        }
    }
}
