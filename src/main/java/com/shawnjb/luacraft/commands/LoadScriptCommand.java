package com.shawnjb.luacraft.commands;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class LoadScriptCommand extends CommandBase {

    @Override
    public String getName() {
        return "loadscript";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/loadscript <scriptName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Usage: /loadscript <scriptName>"));
            return;
        }

        String scriptName = args[0];
        File file = new File(LuaManager.getScriptsFolder(), scriptName);
        
        if (file.exists() && file.isFile()) {
            if (sender instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) sender;
                LuaManager.loadScriptForPlayer(file, player);
            } else {
                LuaManager.loadScript(file);
            }
            sender.sendMessage(new TextComponentString("[LuaCraft] Loaded script: " + scriptName));
        } else {
            sender.sendMessage(new TextComponentString("[LuaCraft] Script not found: " + scriptName));
        }
    }
}
