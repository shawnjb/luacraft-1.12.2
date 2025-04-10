package com.shawnjb.luacraft.lua.core;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.LuaEventManager;
import com.shawnjb.luacraft.lua.LuaPlayer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Arrays;

public class LuaMc extends LuaTable {

    public LuaMc() {
        set("broadcast", new BroadcastFunction());
        set("execute", new ExecuteFunction());
        set("bindToEvent", new BindToEventFunction());
        set("getCommandSender", new GetCommandSenderFunction());
    }

    private static class BroadcastFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue message) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null && server.getPlayerList() != null) {
                server.getPlayerList().sendMessage(new TextComponentString("[Lua] " + message.tojstring()));
            }
            return LuaValue.NIL;
        }
    }

    private static class ExecuteFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue command) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                try {
                    server.getCommandManager().executeCommand(server, command.tojstring());
                } catch (Exception e) {
                    System.err.println("[LuaCraft] Command execution failed: " + e.getMessage());
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class BindToEventFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue eventName, LuaValue callback) {
            if (!callback.isfunction()) {
                return LuaValue.error("Second argument must be a function");
            }
            return LuaEventManager.register(eventName.tojstring(), callback.checkfunction());
        }
    }

    private static class GetCommandSenderFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue playerName) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName.tojstring());
                if (player != null) {
                    return new LuaCommandSender(player);
                }
            }
            return LuaValue.NIL;
        }
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("mc");

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "broadcast",
                "Broadcasts a message to all players.",
                Arrays.asList(new LuaDocRegistry.Param("message", "string", "The message to send")),
                Arrays.asList(),
                false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "execute",
                "Executes a server-side command.",
                Arrays.asList(new LuaDocRegistry.Param("command", "string", "The command to run")),
                Arrays.asList(),
                false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "bindToEvent",
                "Binds a Lua function to a named event.",
                Arrays.asList(
                        new LuaDocRegistry.Param("eventName", "string", "The name of the event (e.g. 'PlayerJoin')"),
                        new LuaDocRegistry.Param("callback", "fun", "The Lua function to call when the event fires")),
                Arrays.asList(new LuaDocRegistry.Return("LuaEvent", "The event binding handle")),
                false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getCommandSender",
                "Gets the command sender for a player by name.",
                Arrays.asList(new LuaDocRegistry.Param("playerName", "string", "The name of the player to get the command sender for")),
                Arrays.asList(new LuaDocRegistry.Return("LuaCommandSender", "The command sender object for the player, or nil if not found")),
                false));
    }
}

class LuaCommandSender extends LuaTable {
    private final EntityPlayerMP player;

    public LuaCommandSender(EntityPlayerMP player) {
        this.player = player;
    }

    public LuaValue getPlayer() {
        return new LuaPlayer(player);
    }
}
