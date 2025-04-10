package com.shawnjb.luacraft.commands;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

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
            if (sender instanceof EntityPlayerMP) {
                LuaManager.runScript(file, (EntityPlayerMP) sender);
            } else {
                LuaManager.runScript(file);
            }
            sender.sendMessage(new TextComponentString("[LuaCraft] Script executed: " + scriptName));
        } else {
            sender.sendMessage(new TextComponentString("[LuaCraft] Script not found: " + scriptName));
        }
    }
}
