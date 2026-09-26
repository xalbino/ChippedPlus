package net.xalbino.chippedplus.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.xalbino.chippedplus.ChippedPlus;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class VariantLootProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;
    private final ChippedDiscoveryProvider discover;

    public VariantLootProvider(DataGenerator generator, ChippedDiscoveryProvider d) {
        this.generator = generator;
        this.discover = d;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        for (var e : discover.discovered()) {
            saveBlockLoot(cache, e.stairs(), dropSelf(e.stairs()));
            saveBlockLoot(cache, e.wall(),   dropSelf(e.wall()));
            saveBlockLoot(cache, e.slab(),   slabDrops(e.slab()));
        }
    }

    @Override
    public String getName() {
        return "ChippedPlus Loot (raw JSON)";
    }

    private void saveBlockLoot(HashCache cache, String blockRL, Map<String, Object> json) throws IOException {
        if (blockRL == null) {
            return;
        }

        ResourceLocation loc = ResourceLocation.tryParse(blockRL);
        if (loc == null) {
            return;
        }

        String path = loc.getPath();
        Path outPath = generator.getOutputFolder().resolve("data/" + ChippedPlus.MODID + "/loot_tables/blocks/" + path + ".json");
        DataProvider.save(GSON, cache, GSON.toJsonTree(json), outPath);
    }

    private static Map<String, Object> dropSelf(String blockRL) {
        return map(
                "type", "minecraft:block",
                "pools", List.of(map(
                        "rolls", 1,
                        "entries", List.of(map(
                                "type", "minecraft:item",
                                "name", blockRL
                        )),
                        "conditions", List.of(map("condition", "minecraft:survives_explosion"))
                ))
        );
    }

    private static Map<String, Object> slabDrops(String blockRL) {
        return map(
                "type", "minecraft:block",
                "pools", List.of(map(
                        "rolls", 1,
                        "entries", List.of(map(
                                "type", "minecraft:alternatives",
                                "children", List.of(
                                        map(
                                                "type", "minecraft:item",
                                                "name", blockRL,
                                                "conditions", List.of(map(
                                                        "condition", "minecraft:block_state_property",
                                                        "block", blockRL,
                                                        "properties", map("type", "double")
                                                )),
                                                "functions", List.of(map(
                                                        "function", "minecraft:set_count",
                                                        "count", 2
                                                ))
                                        ),
                                        map(
                                                "type", "minecraft:item",
                                                "name", blockRL
                                        )
                                )
                        )),
                        "conditions", List.of(map("condition", "minecraft:survives_explosion"))
                ))
        );
    }

    @SafeVarargs
    private static <K, V> Map<K, V> map(Object... kv) {
        Map<K, V> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            @SuppressWarnings("unchecked") K k = (K) kv[i];
            @SuppressWarnings("unchecked") V v = (V) kv[i + 1];
            m.put(k, v);
        }
        return m;
    }
}