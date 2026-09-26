package net.xalbino.chippedplus.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;


public class ChippedVariantBlockItem extends BlockItem {
    private final String chippedPath;
    private final String shape;

    public ChippedVariantBlockItem(Block block, Item.Properties properties, String chippedPath, String shape) {
        super(block, properties);
        this.chippedPath = chippedPath;
        this.shape = shape;
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent(
                "chippedextras.variant." + shape,
                new TranslatableComponent(
                        "block.chipped." + chippedPath
                )
        );
    }
}
