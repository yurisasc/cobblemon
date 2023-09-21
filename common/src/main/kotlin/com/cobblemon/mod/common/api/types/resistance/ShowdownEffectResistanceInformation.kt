/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

/**
 * An implementation of [ResistanceInformation] for Showdown effects.
 *
 * @property showdownEffectId The Showdown ID of the effect being checked.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShowdownEffectResistanceInformation(val showdownEffectId: String, override val resistance: Resistance) : ResistanceInformation {

    override fun showdownId(): String = this.showdownEffectId

    override fun toRaw(): RawResistanceInformation = RawResistanceInformation(this.showdownEffectId, this.resistance, ResistanceVariant.EFFECT_ID)

}