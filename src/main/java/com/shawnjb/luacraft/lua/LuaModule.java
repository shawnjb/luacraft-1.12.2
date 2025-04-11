package com.shawnjb.luacraft.lua;

import org.luaj.vm2.LuaValue;
import net.minecraft.entity.player.EntityPlayerMP;

public class LuaModule {
    private final LuaValue module;
    @SuppressWarnings("unused")
    private final EntityPlayerMP sender;

    public LuaModule(LuaValue module, EntityPlayerMP sender) {
        this.module = module;
        this.sender = sender;
    }

    public String getName() {
        return module.toString();
    }

    public boolean hasOnWorldUnload() {
        return module.get("onWorldUnload").isfunction();
    }

    public void onWorldUnload() {
        LuaValue onWorldUnload = module.get("onWorldUnload");
        if (onWorldUnload.isfunction()) {
            onWorldUnload.call();
        }
    }

    public boolean hasOnScriptLoaded() {
        return module.get("onScriptLoaded").isfunction();
    }

    public void onScriptLoaded() {
        LuaValue onScriptLoaded = module.get("onScriptLoaded");
        if (onScriptLoaded.isfunction()) {
            onScriptLoaded.call();
        }
    }

    public void onScriptLoaded(LuaPlayer player) {
        LuaValue onScriptLoaded = module.get("onScriptLoaded");
        if (onScriptLoaded.isfunction()) {
            onScriptLoaded.call(player);
        }
    }

    public boolean hasGarbageCollect() {
        return module.get("collectGarbage").isfunction();
    }

    public void garbageCollect() {
        LuaValue collectGarbage = module.get("collectGarbage");
        if (collectGarbage.isfunction()) {
            collectGarbage.call();
        }
    }
}
