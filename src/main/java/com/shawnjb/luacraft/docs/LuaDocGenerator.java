package com.shawnjb.luacraft.docs;

import com.shawnjb.luacraft.docs.LuaDocRegistry.FieldDoc;
import com.shawnjb.luacraft.LuaLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LuaDocGenerator {
    public static void main(String[] args) {
        Path outputPath = args.length > 0
                ? Paths.get(args[0])
                : Paths.get("src/main/resources/docs/docs.lua");

        generate(outputPath);
    }

    public static void generate(Path outputPath) {
        try {
            Path parentDir = outputPath.toAbsolutePath().getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
        } catch (IOException e) {
            System.err.println("[LuaDocGenerator] Failed to create docs directory:");
            e.printStackTrace();
            return;
        }

        try {
            LuaLogger.LOGGER.info("[LuaDocGenerator] Running LuaDocBootstrap...");
            LuaDocBootstrap.registerAll();

            Set<String> classNames = LuaDocRegistry.getAllClasses();
            Map<String, List<LuaDocRegistry.FunctionDoc>> docs = LuaDocRegistry.getAllFunctions();

            LuaLogger.LOGGER.info("[LuaDocGenerator] Classes registered: " + classNames.size());
            LuaLogger.LOGGER.info(
                    "[LuaDocGenerator] Functions registered: " + docs.values().stream().mapToInt(List::size).sum());

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                writer.write("---@meta\n");
                writer.write("-- Auto-generated LuaCATS documentation\n");
                writer.write("-- Generated by LuaDocGenerator for LuaCraft\n\n");

                for (String className : classNames) {
                    writer.write(String.format("---@class %s\n", className));

                    List<FieldDoc> fields = LuaDocRegistry.getAllFields().get(className);
                    if (fields != null) {
                        for (FieldDoc field : fields) {
                            if (field.description != null && !field.description.isEmpty()) {
                                writer.write(String.format("---@field %s %s @%s\n", field.name, field.type,
                                        field.description));
                            } else {
                                writer.write(String.format("---@field %s %s\n", field.name, field.type));
                            }
                        }
                    }

                    if (LuaDocRegistry.isGlobalClass(className)) {
                        writer.write(String.format("%s = {}\n\n", className));
                    } else {
                        writer.write(String.format("local %s = {}\n\n", className));
                    }
                }

                for (Map.Entry<String, List<LuaDocRegistry.FunctionDoc>> entry : docs.entrySet()) {
                    String category = entry.getKey();
                    boolean isGlobal = category.equals("core");

                    if (!isGlobal && !classNames.contains(category)) {
                        writer.write(String.format("---@class %s\n", category));
                        writer.write(String.format("local %s = {}\n\n", category));
                    }

                    for (LuaDocRegistry.FunctionDoc func : entry.getValue()) {
                        writer.write(String.format("---%s\n", func.description));
                        for (LuaDocRegistry.Param param : func.params) {
                            if (func.isMethod && param.name.equals("self"))
                                continue;

                            String rawType = param.type.replace("?", "|nil");
                            String normalizedType = rawType.equals("fun") ? "function" : rawType;

                            writer.write(
                                    String.format("---@param %s %s\n", param.name.replace("?", ""), normalizedType));
                        }

                        for (LuaDocRegistry.Return ret : func.returns) {
                            if (ret.description != null && !ret.description.isEmpty()) {
                                writer.write(String.format("---@return %s @%s\n", ret.type, ret.description));
                            } else {
                                writer.write(String.format("---@return %s\n", ret.type));
                            }
                        }

                        String prefix = isGlobal
                                ? "function " + func.name
                                : "function " + category + (func.isMethod ? ":" : ".") + func.name;

                        writer.write(prefix + "(");
                        List<LuaDocRegistry.Param> params = func.params;
                        for (int i = 0; i < params.size(); i++) {
                            if (func.isMethod && params.get(i).name.equals("self"))
                                continue;
                            writer.write(params.get(i).name);
                            if (i < params.size() - 1 && !(func.isMethod && params.get(i + 1).name.equals("self"))) {
                                writer.write(", ");
                            }
                        }

                        writer.write(") end\n\n");
                    }
                }

                for (LuaDocRegistry.FieldDoc field : LuaDocRegistry.getGlobalFields()) {
                    writer.write(String.format("---@type %s\n", field.type));
                    writer.write(String.format("%s = nil\n\n", field.name));
                }

                LuaLogger.LOGGER.info("[LuaDocGenerator] docs.lua generated at: " + outputPath.toAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("[LuaDocGenerator] Failed to generate docs.lua");
            e.printStackTrace();
        }
    }
}
