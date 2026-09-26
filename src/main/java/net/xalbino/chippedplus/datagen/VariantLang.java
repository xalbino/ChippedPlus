package net.xalbino.chippedplus.datagen;

import net.xalbino.chippedplus.ChippedPlus;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class VariantLang extends LanguageProvider {
    public VariantLang(DataGenerator gen, ChippedDiscoveryProvider d) {
        super(gen, ChippedPlus.MODID, "en_us");
        // discover is unused now — name resolution happens at runtime in
        // ChippedVariantBlockItem via nested TranslatableComponents, not here.
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ChippedPlus.MODID, "Chipped Plus");

        // Format strings consumed by ChippedVariantBlockItem#getName().
        // "%s" is filled in at runtime with Chipped's own translated name
        // for the base block (via a nested TranslatableComponent), so this
        // works even if Chipped renames/retranslates things later.
        add("chippedextras.variant.slab", "%s Slab");
        add("chippedextras.variant.stairs", "%s Stairs");
        add("chippedextras.variant.wall", "%s Wall");
    }
}
