package com.shawnjb.luacraft.lua;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.LuaTable;

import java.util.*;

public class LuaEventManager {
    private static final Map<String, List<LuaFunction>> listeners = new HashMap<>();
    private static boolean isRegisteredToForgeBus = false;

    private static final Object dynamicListener = new Object() {
        @SubscribeEvent
        public void onAnyEvent(Object event) {
            String name = event.getClass().getSimpleName().replace("Event", "");
            List<LuaFunction> funcs = listeners.get(name);
            if (funcs != null) {
                for (LuaFunction func : funcs) {
                    try {
                        LuaValue wrappedEvent = wrapEvent(event);
                        func.call(wrappedEvent);
                    } catch (Exception e) {
                        System.err.println("[LuaCraft] Lua listener error: " + e.getMessage());
                    }
                }
            }
        }

        private LuaValue wrapEvent(Object event) {
            if (event instanceof EntityEvent) {
                EntityEvent entityEvent = (EntityEvent) event;
                return new LuaEntity(entityEvent.getEntity());
            }
            else {
                LuaTable luaEvent = new LuaTable();
                luaEvent.set("eventName", LuaValue.valueOf(event.getClass().getSimpleName()));
                luaEvent.set("eventObject", LuaValue.valueOf(event.toString()));
                return luaEvent;
            }
        }
    };

    /**
     * Registers a Lua callback to a named event.
     *
     * @param eventName Name of the event (e.g., "PlayerTick")
     * @param callback  Lua function to call when the event fires
     * @return LuaEvent handle, can be disconnected
     */
    public static LuaValue register(String eventName, LuaFunction callback) {
        final String trimmedEventName = eventName.trim();

        listeners.computeIfAbsent(trimmedEventName, k -> new ArrayList<>()).add(callback);

        if (!isRegisteredToForgeBus) {
            MinecraftForge.EVENT_BUS.register(dynamicListener);
            isRegisteredToForgeBus = true;
        }

        return new LuaEvent(() -> {
            unregister(trimmedEventName, callback);
        });
    }

    /**
     * Unregisters a specific callback from an event.
     */
    public static void unregister(String eventName, LuaFunction callback) {
        List<LuaFunction> funcs = listeners.get(eventName);
        if (funcs != null) {
            funcs.remove(callback);
            if (funcs.isEmpty()) {
                listeners.remove(eventName);
            }
        }
    }
}
