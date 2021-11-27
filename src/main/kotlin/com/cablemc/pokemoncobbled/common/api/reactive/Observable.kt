package com.cablemc.pokemoncobbled.common.api.reactive

import com.cablemc.pokemoncobbled.common.api.reactive.pipes.FilterTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.MapTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.TakeFirstTransform
import com.cablemc.pokemoncobbled.common.api.reactive.pipes.TakeWhileTransform
import java.lang.Thread.sleep

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
    fun <O> pipe(transform: Transform<T, O>): Observable<O> = TransformObservable(this, transform)
    fun <O1, O2> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>) = TransformObservable<T, O2>(this, map { t2(t1(it)) })
    fun <O1, O2, O3> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>) =
        TransformObservable<T, O3>(this, map { t3(t2(t1(it))) })
    fun <O1, O2, O3, O4> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>) =
        TransformObservable<T, O4>(this, map { t4(t3(t2(t1(it)))) })
    fun <O1, O2, O3, O4, O5> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>, t5: Transform<O4, O5>) =
        TransformObservable<T, O5>(this, map { t5(t4(t3(t2(t1(it))))) })
    fun <O1, O2, O3, O4, O5, O6> pipe(t1: Transform<T, O1>, t2: Transform<O1, O2>, t3: Transform<O2, O3>, t4: Transform<O3, O4>, t5: Transform<O4, O5>, t6: Transform<O5, O6>) =
        TransformObservable<T, O6>(this, map { t6(t5(t4(t3(t2(t1(it)))))) })

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
        fun <T> just(vararg values: T): Observable<T> {
            return SingularObservable<T>().also { it.emit(*values) }
        }
        fun <T> takeFirst(amount: Int = 1) = TakeFirstTransform<T>(amount)
        fun <T> filter(predicate: (T) -> Boolean) = FilterTransform(predicate)
        fun <T, O> map(mapping: (T) -> O) = MapTransform(mapping)
        fun <T> takeWhile(predicate: (T) -> Boolean) = TakeWhileTransform(predicate)
        fun <T> takeUntil(predicate: (T) -> Boolean) = TakeWhileTransform<T> { !predicate(it) }
        fun <T> tap(handler: (T) -> Unit) = map<T, T> { handler(it); return@map it }
    }
}