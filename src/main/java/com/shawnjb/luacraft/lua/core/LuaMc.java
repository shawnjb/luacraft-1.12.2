package com.shawnjb.luacraft.lua.core;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.LuaEventManager;
import com.shawnjb.luacraft.lua.LuaPlayer;
import com.shawnjb.luacraft.lua.api.LuaVector3;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import java.util.Arrays;

public class LuaMc extends LuaTable {

    public LuaMc(LuaPlayer sender) {
        set("broadcast", new BroadcastFunction());
        set("execute", new ExecuteFunction());
        set("bindToEvent", new BindToEventFunction());
        set("getOnlinePlayers", new GetOnlinePlayersFunction());
        set("getPlayer", new GetPlayerFunction());
        set("getVersion", new GetVersionFunction());
        set("getLuaJVersion", new GetLuaJVersionFunction());

        // Register global Vector3 class
        LuaVector3.registerGlobal(this);

        if (sender != null) {
            set("sender", sender);
        }
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

    private static class GetOnlinePlayersFunction extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            LuaTable players = new LuaTable();
            if (server != null && server.getPlayerList() != null) {
                int index = 1;
                for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                    players.set(index++, new LuaPlayer(player));
                }
            }
            return players;
        }
    }

    private static class GetPlayerFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue name) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(name.tojstring());
                if (player != null) {
                    return new LuaPlayer(player);
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class GetVersionFunction extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf("1.0.0-alpha");
        }
    }

    private static class GetLuaJVersionFunction extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Lua._VERSION);
        }
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("mc");

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "broadcast",
                "Broadcasts a message to all players.",
                Arrays.asList(new LuaDocRegistry.Param("message", "string", "The message to send")),
                Arrays.asList(), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "execute",
                "Executes a server-side command.",
                Arrays.asList(new LuaDocRegistry.Param("command", "string", "The command to run")),
                Arrays.asList(), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "bindToEvent",
                "Binds a Lua function to a named event.",
                Arrays.asList(
                        new LuaDocRegistry.Param("eventName", "string", "The name of the event (e.g. 'PlayerJoin')"),
                        new LuaDocRegistry.Param("callback", "fun", "The Lua function to call when the event fires")),
                Arrays.asList(new LuaDocRegistry.Return("LuaEvent", "The event binding handle")), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getOnlinePlayers",
                "Returns a list of all currently online players.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("LuaPlayer[]", "The list of players currently online")), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getPlayer",
                "Gets a player by name, or nil if they are not online.",
                Arrays.asList(new LuaDocRegistry.Param("name", "string", "The name of the player")),
                Arrays.asList(new LuaDocRegistry.Return("LuaPlayer", "The player object, or nil if not found")), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getVersion",
                "Returns the current version of LuaCraft.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The version string")), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getLuaJVersion",
                "Returns the LuaJ engine version.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The LuaJ version")), false));
    }
}