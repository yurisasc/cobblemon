package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import dev.architectury.utils.Env
import dev.architectury.utils.EnvExecutor
import dev.architectury.utils.GameInstance
import net.fabricmc.api.EnvType
import net.minecraft.world.level.Level

/** Runs the given [Runnable] if the caller is on the CLIENT side. */
fun ifClient(runnable: Runnable) {
    EnvExecutor.runInEnv(Env.CLIENT) { runnable }
}

/** Runs the given [Runnable] if the caller is on the SERVER side. */
fun ifServer(runnable: Runnable) {
    EnvExecutor.runInEnv(Env.SERVER) { runnable }
}

/** Runs the given [Runnable] if the caller is a dedicated server. */
fun ifDedicatedServer(action: Runnable) {
    EnvExecutor.runInEnv(EnvType.SERVER) { action }
}

/*
 * Schedules the given block of code to run on the main thread and returns a [CompletableFuture] that completes with the result of the block when the code has executed.
 */
fun <T> runOnServer(block: () -> T) = getServer()?.addTickable { block() }
fun <T> Observable<T>.subscribeOnServer(priority: Priority = Priority.NORMAL, block: () -> Unit) = subscribe(priority) { runOnServer(block) }

fun getServer() = GameInstance.getServer()

///** If you are not Pokémon Cobbled, don't touch this. If you end up doing client side stuff, you'll probably break stuff. */
//internal fun <T> runOnSide(side: LogicalSide, block: () -> T): CompletableFuture<T> {
//    val future = CompletableFuture<T>()
//    LogicalSidedProvider.WORKQUEUE.get(side).submit { future.complete(block()) }
//    return future
//}

fun Level.isServerSide() = !isClientSide