package com.shawnjb.luacraft.lua.core;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.LuaEventManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
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

    // 🧠 LuaCATS Documentation
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
    }
}
