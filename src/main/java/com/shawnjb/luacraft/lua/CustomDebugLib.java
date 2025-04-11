package com.shawnjb.luacraft.lua;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

public class CustomDebugLib extends DebugLib {
    public boolean interrupted = false;

    @Override
    public void onInstruction(int pc, Varargs v, int top) {
        if (interrupted) {
            throw new ScriptInterruptException();
        }
        super.onInstruction(pc, v, top);
    }

    public static class ScriptInterruptException extends RuntimeException {}
}
