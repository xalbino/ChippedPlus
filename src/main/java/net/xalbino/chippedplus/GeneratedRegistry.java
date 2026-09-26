package net.xalbino.chippedplus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.xalbino.chippedplus.block.FixedSlabBlock;
import net.xalbino.chippedplus.block.FixedStairBlock;
import net.xalbino.chippedplus.block.FixedWallBlock;
import net.xalbino.chippedplus.item.ChippedVariantBlockItem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GeneratedRegistry {

    public static final List<RegistryObject<Item>> VARIANT_ITEMS =
            new ArrayList<>();

    public static class Entry {
        public String base;
        public String slab;
        public String stairs;
        public String wall;
    }

    public static void bootstrapFromJson(
            DeferredRegister<Block> blocks,
            DeferredRegister<Item> items
    ) {
        final List<Entry> entries = loadEntries();

        if (entries.isEmpty()) {
            final boolean isDataGen = Boolean.getBoolean("chippedplus.datagen");

            if (isDataGen) {
                return;
            }

            System.out.println(
                    "[chippedplus] WARNING: registry.json "
                            + "missing/empty; skipping dynamic registration."
            );

            return;
        }


        System.out.println(
                "[chippedplus] registering "
                        + entries.size()
                        + " variants"
        );

        for (Entry e : entries) {
            if (e == null || e.base == null) {
                continue;
            }

            final ResourceLocation baseRL =
                    ResourceLocation.tryParse(e.base);

            if (baseRL == null) {
                System.out.println(
                        "[chippedplus] WARNING: invalid base block: "
                                + e.base
                );

                continue;
            }

            final Block baseBlock =
                    ForgeRegistries.BLOCKS.getValue(baseRL);

            final SoundType sound =
                    baseBlock != null
                            ? baseBlock.defaultBlockState().getSoundType()
                            : SoundType.STONE;

            final Block parent =
                    baseBlock != null
                            ? baseBlock
                            : Blocks.STONE;

            final BlockBehaviour.Properties props =
                    BlockBehaviour.Properties
                            .of(Material.STONE, MaterialColor.STONE)
                            .strength(1.5F, 6.0F)
                            .sound(sound);

            final Item.Properties itemProps =
                    new Item.Properties().tab(ChippedPlus.CHIPPEDEXTRAS_TAB);

            final String displayPath = baseRL.getPath();

            /*
             * Keep this order aligned with registry.json:
             * slab, stairs, wall.
             */

            if (e.slab != null && !e.slab.isEmpty()) {
                final String id =
                        Objects.requireNonNull(
                                ResourceLocation.tryParse(e.slab)
                        ).getPath();

                final RegistryObject<Block> roB =
                        blocks.register(
                                id,
                                () -> new FixedSlabBlock(props)
                        );

                final RegistryObject<Item> roI =
                        items.register(
                                id,
                                () -> new ChippedVariantBlockItem(
                                        roB.get(),
                                        itemProps,
                                        displayPath,
                                        "slab"
                                )
                        );

                VARIANT_ITEMS.add(roI);
            }

            if (e.stairs != null && !e.stairs.isEmpty()) {
                final String id =
                        Objects.requireNonNull(
                                ResourceLocation.tryParse(e.stairs)
                        ).getPath();

                final RegistryObject<Block> roB =
                        blocks.register(
                                id,
                                () -> new FixedStairBlock(
                                        () -> parent.defaultBlockState(),
                                        props
                                )
                        );

                final RegistryObject<Item> roI =
                        items.register(
                                id,() -> new ChippedVariantBlockItem(
                                        roB.get(),
                                        itemProps,
                                        displayPath,
                                        "stairs"
                                )
                        );

                VARIANT_ITEMS.add(roI);
            }

            if (e.wall != null && !e.wall.isEmpty()) {
                final String id =
                        Objects.requireNonNull(
                                ResourceLocation.tryParse(e.wall)
                        ).getPath();

                final RegistryObject<Block> roB =
                        blocks.register(
                                id,
                                () -> new FixedWallBlock(props)
                        );

                final RegistryObject<Item> roI =
                        items.register(
                                id,
                                () -> new ChippedVariantBlockItem(
                                        roB.get(),
                                        itemProps,
                                        displayPath,
                                        "wall"
                                )
                        );

                VARIANT_ITEMS.add(roI);
            }
        }
    }

    private static List<Entry> loadEntries() {
        try (
                InputStream in =
                        GeneratedRegistry.class.getResourceAsStream(
                                "/assets/"
                                        + ChippedPlus.MODID
                                        + "/generated/registry.json"
                        )
        ) {
            if (in == null) {
                return Collections.emptyList();
            }

            final Type listType =
                    new TypeToken<List<Entry>>() {
                    }.getType();

            final List<Entry> list = new Gson().fromJson(
                    new InputStreamReader(in),
                    listType
            );

            return list != null
                    ? list
                    : Collections.emptyList();

        } catch (Exception ex) {
            System.out.println(
                    "[chippedplus] ERROR reading registry.json: "
                            + ex
            );

            return Collections.emptyList();
        }
    }
}