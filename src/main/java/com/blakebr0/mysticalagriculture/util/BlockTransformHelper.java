package com.blakebr0.mysticalagriculture.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Resolves MC 26.3 block-transformer state without executing an item use.
 * AOE augments manage their own position, game event, and durability effects.
 */
public final class BlockTransformHelper {
    private BlockTransformHelper() { }

    public static @Nullable BlockState getNewState(UseOnContext context, BlockPos pos, ResourceKey<BlockTransformer> transformerKey) {
        var level = context.getLevel();
        var transformer = level.registryAccess().lookupOrThrow(Registries.BLOCK_TRANSFORMER).get(transformerKey);
        if (transformer.isEmpty()) {
            return null;
        }

        for (var rule : transformer.get().value().transforms()) {
            if (rule.disallowedFaces().contains(context.getClickedFace())) {
                continue;
            }
            var newState = rule.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos);
            if (newState != null) {
                return rule.updateFromNeighbors() ? Block.updateFromNeighbourShapes(newState, level, pos) : newState;
            }
        }
        return null;
    }
}
