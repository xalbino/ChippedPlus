package net.xalbino.chippedplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FixedSlabBlock extends SlabBlock {
    private static final VoxelShape BOTTOM = box(0, 0, 0, 16, 8, 16);
    private static final VoxelShape TOP    = box(0, 8, 0, 16, 16, 16);

    public FixedSlabBlock(BlockBehaviour.Properties props) { super(props); }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        SlabType t = state.getValue(TYPE);
        return switch (t) {
            case DOUBLE -> Shapes.block();
            case TOP    -> TOP;
            case BOTTOM -> BOTTOM;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getCollisionShape(state, level, pos, ctx);
    }
}
