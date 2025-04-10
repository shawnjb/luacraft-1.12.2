package com.shawnjb.luacraft.commands;

import com.shawnjb.luacraft.lua.LuaManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
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
        LuaManager.runScript(scriptName);
        sender.sendMessage(new TextComponentString("[LuaCraft] Script executed: " + scriptName));
    }
}
