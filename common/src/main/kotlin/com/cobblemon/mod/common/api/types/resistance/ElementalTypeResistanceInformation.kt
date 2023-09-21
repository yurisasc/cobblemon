/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

import com.cobblemon.mod.common.api.types.ElementalType

/**
 * An implementation of [ResistanceInformation] for [ElementalType]s.
 *
 * @property elementalType The [ElementalType] that would be attacking the type containing this object.
 */
class ElementalTypeResistanceInformation(val elementalType: ElementalType, override val resistance: Resistance) : ResistanceInformation {

    override fun showdownId(): String = this.elementalType.showdownId()

    override fun toRaw(): RawResistanceInformation = RawResistanceInformation(this.elementalType.id().toString(), this.resistance, ResistanceVariant.ELEMENTAL_TYPE)
}