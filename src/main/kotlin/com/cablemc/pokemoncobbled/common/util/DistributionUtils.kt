package com.cablemc.pokemoncobbled.common.util

import net.minecraft.util.thread.BlockableEventLoop
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fmllegacy.LogicalSidedProvider
import java.util.concurrent.CompletableFuture

/** Runs the given [Runnable] if the caller is on the CLIENT side. */
fun ifClient(runnable: Runnable) {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { runnable }
}

/*
 * Schedules the given block of code to run on the main thread and returns a [CompletableFuture] that completes with the result of the block when the code has executed.
 */
fun <T> runOnServer(block: () -> T) = runOnSide(side = LogicalSide.SERVER, block)

/** If you are not Pokémon Cobbled, don't touch this. If you end up doing client side stuff, you'll probably break stuff. */
internal fun <T> runOnSide(side: LogicalSide, block: () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    LogicalSidedProvider.WORKQUEUE.get<BlockableEventLoop<*>>(side).submit { future.complete(block()) }
    return future
}