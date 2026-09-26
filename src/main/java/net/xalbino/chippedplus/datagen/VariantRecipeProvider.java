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

public class VariantRecipeProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final DataGenerator generator;
    private final ChippedDiscoveryProvider discover;

    public VariantRecipeProvider(DataGenerator generator, ChippedDiscoveryProvider d) {
        this.generator = generator;
        this.discover = d;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        for (var e : discover.discovered()) {
            if (e.base() == null) continue;

            ResourceLocation base = ResourceLocation.tryParse(e.base());
            if (base == null) continue;

            ResourceLocation slab   = e.slab()   != null ? ResourceLocation.tryParse(e.slab())   : null;
            ResourceLocation stairs = e.stairs() != null ? ResourceLocation.tryParse(e.stairs()) : null;
            ResourceLocation wall   = e.wall()   != null ? ResourceLocation.tryParse(e.wall())   : null;

            // Prüfen, ob der Block aus Holz ist
            boolean isWood = base.getPath().contains("planks") || base.getPath().contains("plank");

            if (isWood) {
                // HOLZ: Crafts an der Werkbank (Crafting Table)
                if (stairs != null) {
                    saveRecipe(cache, "crafting/" + stairs.getPath(), shapedStairs(base, stairs));
                }
                if (slab != null) {
                    saveRecipe(cache, "crafting/" + slab.getPath(), shapedSlab(base, slab));
                }
            } else {
                // STEIN/BETON: Stonecutter (1->1; slab 1->2)
                if (stairs != null) saveRecipe(cache, "stonecutting/" + stairs.getPath(), stonecut(base, stairs, 1));
                if (wall   != null) saveRecipe(cache, "stonecutting/" + wall.getPath(),   stonecut(base, wall,   1));
                if (slab   != null) saveRecipe(cache, "stonecutting/" + slab.getPath(),   stonecut(base, slab,   2));
            }
        }
    }

    @Override
    public String getName() {
        return "ChippedPlus Recipes";
    }

    private void saveRecipe(HashCache cache, String rel, Map<String, Object> json) throws IOException {
        Path p = generator.getOutputFolder().resolve("data/" + ChippedPlus.MODID + "/recipes/" + rel + ".json");
        DataProvider.save(GSON, cache, GSON.toJsonTree(json), p);
    }

    // JSON for stonecutter
    private static Map<String, Object> stonecut(ResourceLocation ingredientItem, ResourceLocation resultItem, int count) {
        return map(
                "type", "minecraft:stonecutting",
                "ingredient", map("item", ingredientItem.toString()),
                "result", resultItem.toString(),
                "count", count
        );
    }

    // JSON for stairs
    private static Map<String, Object> shapedStairs(ResourceLocation ingredientItem, ResourceLocation resultItem) {
        return map(
                "type", "minecraft:crafting_shaped",
                "pattern", List.of(
                        "#  ",
                        "## ",
                        "###"
                ),
                "key", map("#", map("item", ingredientItem.toString())),
                "result", map(
                        "item", resultItem.toString(),
                        "count", 4
                )
        );
    }

    // JSON for slab
    private static Map<String, Object> shapedSlab(ResourceLocation ingredientItem, ResourceLocation resultItem) {
        return map(
                "type", "minecraft:crafting_shaped",
                "pattern", List.of(
                        "###"
                ),
                "key", map("#", map("item", ingredientItem.toString())),
                "result", map(
                        "item", resultItem.toString(),
                        "count", 6
                )
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