/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.battles.model.actor

import net.minecraft.entity.LivingEntity

/**
 * Allows a [BattleActor] to attach a [LivingEntity] to itself.
 *
 * @param T The type of [LivingEntity].
 */
interface EntityBackedBattleActor<T : LivingEntity> {

    /**
     * The [LivingEntity] attached to the [BattleActor].
     */
    val entity: T?

}