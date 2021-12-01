package com.cablemc.pokemoncobbled.common.api.reactive

import com.cablemc.pokemoncobbled.common.api.reactive.pipes.FilterTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.IgnoreFirstTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.MapTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.TakeFirstTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.TakeWhileTransform
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

/**
 * An object that emits values of the generic type. This can be subscribed to and unsubscribed from, as well as
 * translated into different views using pipes. It's important to note that subscriptions should be cleaned up when
 * possible either by using a takeWhile pipe or manually unsubscribing when you don't need to observe it anymore.
 *
 * This is built in a very similar manner to the Observable interface of RxJs.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
interface Observable<T> {
    fun subscribe(handler: (T) -> Unit): ObservableSubscription<T>
    fun unsubscribe(subscription: ObservableSubscription<T>)

    // This isn't pretty, ik
    /**
     * Runs a transformation to this [Observable] to create a new [TransformObservable] that reflects the original.
     * Overloads exist to maintain type safety.
     */
    fun <O> pipe(transform: Transform<T, O>): Observable<O> = TransformObservable(this, transform)
    fun <O1, O2> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>) = TransformObservable(this, map { t2(t1(it)) })
    fun <O1, O2, O3> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>) = TransformObservable(this, map { t3(t2(t1(it))) })
    fun <O1, O2, O3, O4> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>) = TransformObservable(this, map { t4(t3(t2(t1(it)))) })
    fun <O1, O2, O3, O4, O5> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>, t5: Transform<O4, O5>) = TransformObservable(this, map { t5(t4(t3(t2(t1(it))))) })
    fun <O1, O2, O3, O4, O5, O6> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>, t5: Transform<O4, O5>, t6: Transform<O5, O6>) = TransformObservable(this, map { t6(t5(t4(t3(t2(t1(it)))))) })

    /** Wildly unsafe as it blocks the thread. Only use this when you absolutely must get the value before your current thread continues. */
    fun await(): T {
        var result: T? = null
        subscribe { result = it }
        while (result == null) {
            sleep(1L)
        }
        return result!!
    }

    companion object {
        /** Creates a completed [Observable] with the given values. This [Observable] cannot receive new values. */
        fun <T> just(vararg values: T): Observable<T> {
            return SingularObservable<T>().also { it.emit(*values) }
        }

        fun <T> of(future: CompletableFuture<T>): Observable<T> {
            val observable = SingularObservable<T>()
            future.thenAccept { observable.emit(it) }
            return observable
        }

        /** Gets a [TakeFirstTransform] */
        fun <T> takeFirst(amount: Int = 1) = TakeFirstTransform<T>(amount)
        /** Gets a [IgnoreFirstTransform] */
        fun <T> ignoreFirst(amount: Int = 1) = IgnoreFirstTransform<T>(amount)
        /** Gets a [FilterTransform] */
        fun <T> filter(predicate: (T) -> Boolean) = FilterTransform(predicate)
        /** Gets a [MapTransform] */
        fun <T, O> map(mapping: (T) -> O) = MapTransform(mapping)
        /** Gets a [TakeWhileTransform] */
        fun <T> takeWhile(predicate: (T) -> Boolean) = TakeWhileTransform(predicate)
        /** Gets a transform which will take values until the given predicate is met. */
        fun <T> takeUntil(predicate: (T) -> Boolean) = TakeWhileTransform<T> { !predicate(it) }
        /** Gets a transform which does not change the value but lets you access the value at this stage of the pipe. */
        fun <T> tap(handler: (T) -> Unit) = map<T, T> { handler(it); return@map it }
    }
}