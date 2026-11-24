package com.blakebr0.mysticalagriculture.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

/**
 * Evaluates vanilla/datapack cooking-fuel burn durations in Minecraft 26.3.
 * Uses the current machine's block state, block entity, and position.
 *
 * The SimpleContainer exposes the queried fuel stack. If a datapack's
 * fuel provider examines other inventory slots, those providers will need
 * a machine-specific full-container adapter in a later pass.
 */
public final class CookingFuelHelper {
    private CookingFuelHelper() {}

    public static int getBurnTime(Level level, BlockPos pos, BlockEntity machine, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel) || stack.isEmpty())
            return 0;

        var cookingFuel = stack.get(DataComponents.COOKING_FUEL);
        if (cookingFuel == null)
            return 0;

        var params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.CONTAINER, new SimpleContainer(stack.copy()))
                .withParameter(LootContextParams.BLOCK_ENTITY, machine)
                .withParameter(LootContextParams.BLOCK_STATE, machine.getBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.CONTAINER_PROCESS);
        var context = new LootContext.Builder(params).create(Optional.empty());
        return Math.max(0, cookingFuel.burnTime().get(context, 0));
    }
}
