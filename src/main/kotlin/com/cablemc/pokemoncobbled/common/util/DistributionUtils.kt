package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import net.minecraft.util.thread.BlockableEventLoop
import net.minecraft.world.level.Level
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.LogicalSidedProvider
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.util.thread.EffectiveSide
import net.minecraftforge.server.ServerLifecycleHooks

import java.util.concurrent.CompletableFuture

/** Runs the given [Runnable] if the caller is on the CLIENT side. */
fun ifClient(runnable: Runnable) {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { runnable }
}

/** Runs the given [Runnable] if the caller is on the SERVER side. */
fun ifServer(runnable: Runnable) {
    DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER) { runnable }
}

fun ifLogicallyServer(runnable: Runnable) = ifLogically(LogicalSide.SERVER, runnable)
fun ifLogicallyClient(runnable: Runnable) = ifLogically(LogicalSide.CLIENT, runnable)

fun ifLogically(side: LogicalSide, runnable: Runnable) {
    if (EffectiveSide.get() == side) {
        runnable.run()
    }
}

/*
 * Schedules the given block of code to run on the main thread and returns a [CompletableFuture] that completes with the result of the block when the code has executed.
 */
fun <T> runOnServer(block: () -> T) = runOnSide(side = LogicalSide.SERVER, block)
fun <T> Observable<T>.subscribeOnServer(block: () -> Unit) = subscribe { runOnServer(block) }

fun getServer() = ServerLifecycleHooks.getCurrentServer()

/** If you are not Pokémon Cobbled, don't touch this. If you end up doing client side stuff, you'll probably break stuff. */
internal fun <T> runOnSide(side: LogicalSide, block: () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    LogicalSidedProvider.WORKQUEUE.get(side).submit { future.complete(block()) }
    return future
}

fun Level.isServerSide() = !isClientSide