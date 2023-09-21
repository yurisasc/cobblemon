/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.adapters

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.*
import java.lang.reflect.Type

object ElementalTypeAdapter: JsonSerializer<ElementalType>, JsonDeserializer<ElementalType> {

    override fun serialize(src: ElementalType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(if (src.id.namespace == Cobblemon.MODID) src.id.path else src.id.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ElementalType {
        return CobblemonRegistries.ELEMENTAL_TYPE.get(json.asString.asIdentifierDefaultingNamespace())!!
    }

}