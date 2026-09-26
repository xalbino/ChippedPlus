package net.xalbino.chippedplus.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.xalbino.chippedplus.ChippedPlus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ChippedDiscoveryProvider implements DataProvider {
    private final DataGenerator generator;
    private final List<Entry> entries = new ArrayList<>();

    private static final Set<String> KEEP_EXACT = Set.of(
            // Stone & Similar
            "brick", "bricks", "tile", "tiles", "stone", "stones",
            "cobblestone", "mossy", "basalt", "deepslate", "blackstone",
            "granite", "diorite", "andesite", "calcite", "tuff",
            "prismarine", "quartz", "purpur", "obsidian",
            "endstone", "sandstone",

            // Wood
            "planks", "plank"
    );

    private static final Set<String> KEEP_ENDS_WITH_STONE_EXCEPT = Set.of("redstone", "glowstone", "dripstone");

    private static final Set<String> DISALLOWED_TOKENS = Set.of(
            "dripstone", "dripstones",
            "bars",
            "lamp", "lamps",
            "lantern", "lanterns",
            "torch", "torches",
            "table", "tables",
            "workbench", "workbenches",
            "glassblower", "glassblowing",
            "glowstone", "terracotta", "wool", "glass", "concrete"
    );

    public ChippedDiscoveryProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        entries.clear();

        // --- TEMP DIAGNOSTIC: how many chipped: blocks actually have a registered Item? ---
        int totalChippedBlocks = 0;
        int chippedBlocksWithItem = 0;
        for (ResourceLocation dbg : ForgeRegistries.BLOCKS.getKeys()) {
            if (!"chipped".equals(dbg.getNamespace())) continue;
            totalChippedBlocks++;
            if (ForgeRegistries.ITEMS.getValue(dbg) != null) chippedBlocksWithItem++;
        }
        System.out.println("[" + ChippedPlus.MODID + "] DIAGNOSTIC: " + totalChippedBlocks
                + " total chipped: blocks, " + chippedBlocksWithItem + " of them have a registered Item");
        // --- END TEMP DIAGNOSTIC ---

        ForgeRegistries.BLOCKS.getKeys().forEach(rl -> {
            if (!"chipped".equals(rl.getNamespace())) return;

            // Chipped registers a lot of connected-texture (CTM) block-state variants
            // that were never meant to be obtainable on their own (e.g. acacia_planks_1
            // through acacia_planks_41) — only the "canonical" variant of each material
            // gets a real BlockItem and a real name from Chipped. Skip anything that
            // doesn't have an item: it's an internal render variant, not a real material.
            if (ForgeRegistries.ITEMS.getValue(rl) == null) return;

            final String path = rl.getPath().toLowerCase(Locale.ROOT);

            if (path.endsWith("_slab") || path.endsWith("_stairs") || path.endsWith("_wall")) return;

            final String[] tokens = path.split("[_\\-]");

            if (containsAny(tokens, DISALLOWED_TOKENS)) return;

            if (!isBrickStoneOrTile(tokens)) return;

            final String base = rl.toString();
            final String slab = new ResourceLocation(ChippedPlus.MODID, path + "_slab").toString();
            final String stairs = new ResourceLocation(ChippedPlus.MODID, path + "_stairs").toString();

            // Wall filter for certain blocks
            final boolean allowWall = !containsAny(tokens, Set.of("planks", "plank", "prismarine"));
            final String wall = allowWall ? new ResourceLocation(ChippedPlus.MODID, path + "_wall").toString() : null;

            entries.add(new Entry(base, slab, stairs, wall));
        });

        final Path outPath = generator.getOutputFolder().resolve("assets/" + ChippedPlus.MODID + "/generated/registry.json");
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        System.out.println("[" + ChippedPlus.MODID + "] discovered " + entries.size() + " chipped base blocks");

        DataProvider.save(gson, cache, gson.toJsonTree(entries), outPath);
    }

    public List<Entry> discovered() { return entries; }

    @Override
    public String getName() { return "ChippedPlus Discovery & Registry List"; }

    public static record Entry(String base, String slab, String stairs, String wall) {}

    private static boolean containsAny(String[] tokens, Set<String> set) {
        for (String t : tokens) if (set.contains(t)) return true;
        return false;
    }

    private static boolean isBrickStoneOrTile(String[] tokens) {
        for (String t : tokens) {
            if (KEEP_EXACT.contains(t)) return true;
            if (t.endsWith("stone") && !KEEP_ENDS_WITH_STONE_EXCEPT.contains(t)) return true;
        }
        return false;
    }
}