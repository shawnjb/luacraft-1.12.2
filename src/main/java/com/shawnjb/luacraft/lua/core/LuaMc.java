package com.shawnjb.luacraft.lua.core;

import java.util.Arrays;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.shawnjb.luacraft.docs.LuaDocRegistry;
import com.shawnjb.luacraft.lua.LuaEventManager;
import com.shawnjb.luacraft.lua.LuaPlayer;
import com.shawnjb.luacraft.lua.api.LuaVector3;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class LuaMc extends LuaTable {

    public LuaMc(LuaPlayer sender) {
        set("broadcast", new BroadcastFunction());
        set("execute", new ExecuteFunction());
        set("bindToEvent", new BindToEventFunction());
        set("getOnlinePlayers", new GetOnlinePlayersFunction());
        set("getPlayer", new GetPlayerFunction());
        set("getVersion", new GetVersionFunction());
        set("getLuaJVersion", new GetLuaJVersionFunction());
        set("summonEntity", new SummonEntityFunction());
        set("createItemStack", new CreateItemStackFunction());
        LuaVector3.registerGlobal(this);
        if (sender != null) {
            set("sender", sender);
        }
    }

    private static class CreateItemStackFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue idVal, LuaValue countVal) {
            String id = idVal.checkjstring();
            int count = countVal.checkint();
            LuaValue itemStack = com.shawnjb.luacraft.lua.api.LuaItemStack.of(id, count);
            return itemStack != null ? itemStack : LuaValue.NIL;
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

    private static class SummonEntityFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue entityId, LuaValue posTable) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null || server.getWorld(0) == null) {
                return LuaValue.error("World not available");
            }

            World world = server.getWorld(0);
            LuaVector3 vec = LuaVector3.fromLuaTable(posTable.checktable());
            ResourceLocation entityRes = new ResourceLocation(entityId.checkjstring());

            Entity entity = EntityList.createEntityByIDFromName(entityRes, world);
            if (entity == null) {
                return LuaValue.error("Unknown entity ID: " + entityId.tojstring());
            }

            entity.setPosition(vec.x, vec.y, vec.z);
            world.spawnEntity(entity);
            return LuaValue.TRUE;
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
                Arrays.asList(new LuaDocRegistry.Return("LuaPlayer[]", "The list of players currently online")),
                false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "getPlayer",
                "Gets a player by name, or nil if they are not online.",
                Arrays.asList(new LuaDocRegistry.Param("name", "string", "The name of the player")),
                Arrays.asList(new LuaDocRegistry.Return("LuaPlayer", "The player object, or nil if not found")),
                false));

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

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "summonEntity",
                "Summons an entity at a specific position.",
                Arrays.asList(
                        new LuaDocRegistry.Param("entityId", "string", "The entity ID (e.g. 'minecraft:zombie')"),
                        new LuaDocRegistry.Param("pos", "Vector3", "The spawn position")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if the entity was spawned")), false));

        LuaDocRegistry.addFunction("mc", new LuaDocRegistry.FunctionDoc(
                "createItemStack",
                "Creates a LuaItemStack from a registry ID and count.",
                Arrays.asList(
                        new LuaDocRegistry.Param("itemId", "string", "The item registry ID (e.g. 'minecraft:stone')"),
                        new LuaDocRegistry.Param("count", "number", "The number of items in the stack")),
                Arrays.asList(new LuaDocRegistry.Return("LuaItemStack", "The created item stack or nil if invalid")),
                false));

        LuaDocRegistry.addGlobalField("sender", "LuaPlayer",
                "The player who triggered the current command or event. This global field is set only when a sender is available.");
    }
}