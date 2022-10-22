/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.scheduling

object ScheduledTaskTracker {
    private val tasks = mutableListOf<ScheduledTask>()

    fun clear() {
        tasks.clear()
    }

    fun update() {
        for (task in tasks.toList()) {
            task.update()
            if (task.expired) {
                tasks.remove(task)
            }
        }
    }

    fun addTask(task: ScheduledTask) {
        tasks.add(task)
    }
}