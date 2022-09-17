/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.reactive

import com.cablemc.pokemoncobbled.common.api.scheduling.taskBuilder

class ObservableSubscription<T>(
    private val observable: Observable<T>,
    private val handler: (T) -> Unit
) {
    var alive = true
    fun handle(value: T) = handler(value)
    fun unsubscribe() {
        observable.unsubscribe(this)
        alive = false
    }

    /**
     * Schedules a task for every [checkInterval] seconds which checks the given [condition], and if it is
     * true it unsubscribes this [ObservableSubscription]. This exists predominantly for performance reasons,
     * as releasing subscriptions when they are no longer necessary should be done as soon as possible to allow
     * objects to be released.
     */
    fun unsubscribeWhen(checkInterval: Float = 5F, condition: () -> Boolean): ObservableSubscription<T> {
        taskBuilder()
            .interval(checkInterval)
            .execute {
                if (!alive || condition()) {
                    unsubscribe()
                    it.expire()
                }
            }
            .build()
        return this
    }
}