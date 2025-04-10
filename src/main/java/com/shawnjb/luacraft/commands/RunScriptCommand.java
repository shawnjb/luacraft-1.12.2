package com.shawnjb.luacraft.commands;

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
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Usage: /runscript <code>"));
            return;
        }

        String code = String.join(" ", args);
        if (sender instanceof EntityPlayerMP) {
            LuaManager.runScript(code, (EntityPlayerMP) sender);
        } else {
            LuaManager.runScript(code);
        }

        sender.sendMessage(new TextComponentString("[LuaCraft] Lua code executed."));
    }
}
