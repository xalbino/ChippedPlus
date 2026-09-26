package net.xalbino.chippedplus.datagen;

import net.xalbino.chippedplus.ChippedPlus;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Iterator;

public class VariantBlockTags extends BlockTagsProvider {
    private final ChippedDiscoveryProvider d;

    public VariantBlockTags(DataGenerator generator, ChippedDiscoveryProvider d, ExistingFileHelper efh) {
        super(generator, ChippedPlus.MODID, efh);
        this.d = d;
    }

    @Override
    protected void addTags() {
        Iterator<ChippedDiscoveryProvider.Entry> iterator = this.d.discovered().iterator();

        while (iterator.hasNext()) {
            ChippedDiscoveryProvider.Entry e = iterator.next();
            if (e == null) continue;

            this.addAll(e.slab(), BlockTags.SLABS);
            this.addAll(e.stairs(), BlockTags.STAIRS);
            this.addAll(e.wall(), BlockTags.WALLS);

            this.addAll(e.slab(), BlockTags.MINEABLE_WITH_PICKAXE);
            this.addAll(e.stairs(), BlockTags.MINEABLE_WITH_PICKAXE);
            this.addAll(e.wall(), BlockTags.MINEABLE_WITH_PICKAXE);
        }
    }

    private void addAll(String rl, TagKey<Block> tag) {
        if (rl == null) return;

        ResourceLocation loc = ResourceLocation.tryParse(rl);
        if (loc != null) {
            this.tag(tag).addOptional(loc);
        }
    }

    @Override
    public String getName() {
        return "ChippedExtras Block Tags";
    }
}