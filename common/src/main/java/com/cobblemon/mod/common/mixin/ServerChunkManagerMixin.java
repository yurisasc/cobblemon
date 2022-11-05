/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.api.spawning.mixins.CachedOnlyChunkAccessor;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager implements CachedOnlyChunkAccessor {

    @Shadow @Final private Chunk[] chunkCache;
    @Shadow @Final private long[] chunkPosCache;
    @Shadow @Final private ChunkStatus[] chunkStatusCache;

    @Override
    public Chunk Cobblemon$request(int x, int z, @NotNull ChunkStatus status) {
        long l = ChunkPos.toLong(x, z);

        for (int i = 0; i < 4; ++i) {
            if (l == this.chunkPosCache[i] && status == this.chunkStatusCache[i]) {
                Chunk chunk = this.chunkCache[i];
                if (chunk != null) {
                    return chunk;
                }
            }
        }

        return null;
    }
}
