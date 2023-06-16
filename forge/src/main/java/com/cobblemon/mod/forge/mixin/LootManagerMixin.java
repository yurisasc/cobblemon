/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.mixin;

import com.cobblemon.mod.common.loot.LootInjector;
import com.cobblemon.mod.forge.mixin.accessor.LootTableAccessor;
import com.cobblemon.mod.forge.mixin.accessor.LootTableBuilderAccessor;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import kotlin.Unit;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Shadow private Map<Identifier, LootTable> tables;

    // Credit to https://github.com/FabricMC/fabric/blob/1.19.4/fabric-loot-api-v2/src/main/java/net/fabricmc/fabric/mixin/loot/LootManagerMixin.java#L49-L86
    // This is very much a copy pasta as it does exactly what we needed Forge has an event but it gets fired as each individual table loads, we need to know all the loaded ones.
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
    private void cobblemon$injectLootTables(Map<Identifier, JsonElement> map, ResourceManager arg, Profiler arg2, CallbackInfo ci) {
        ImmutableMap.Builder<Identifier, LootTable> modifiedTables = ImmutableMap.builder();
        this.tables.forEach((id, table) -> {
            if (id.equals(LootTables.EMPTY)) {
                return;
            }
            LootManager lootManager = (LootManager) (Object) this;
            LootTableAccessor lootTableAccessor = (LootTableAccessor) table;
            LootTable.Builder builder = LootTable.builder().type(table.getType());
            LootTableBuilderAccessor lootTableBuilderAccessor = (LootTableBuilderAccessor) builder;
            lootTableBuilderAccessor.getPools().addAll(lootTableAccessor.getPools());
            lootTableBuilderAccessor.getFunctions().addAll(List.of(lootTableAccessor.getFunctions()));
            LootInjector.INSTANCE.attemptInjection(id, lootManager, resultingBuilder -> {
                builder.pool(resultingBuilder);
                return Unit.INSTANCE;
            });
            modifiedTables.put(id, builder.build());
        });
        this.tables = modifiedTables.build();
    }

}
