package net.xalbino.chippedplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class FixedStairBlock extends StairBlock {
    public FixedStairBlock(Supplier<BlockState> baseState, BlockBehaviour.Properties props) {
        super(baseState, props);
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape s = super.getCollisionShape(state, level, pos, ctx);
        if (s.isEmpty()) s = super.getShape(state, level, pos, ctx);
        return s.isEmpty() ? Shapes.block() : s;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape s = super.getShape(state, level, pos, ctx);
        if (s.isEmpty()) s = super.getCollisionShape(state, level, pos, ctx);
        return s.isEmpty() ? Shapes.block() : s;
    }
}
