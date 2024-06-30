/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

/**
 * Class representing a type of Pokémon or Move
 *
 * @param name: The English name used to load / find it (spaces -> _)
 * @param displayName: A Component used to display the name, normally a TranslatableText
 * @param textureXMultiplier: The multiplier by which the TypeWidget shall move the display
 * @param resourceLocation: The location of the resource used in the TypeWidget
 * @param primaryColor: The primary color of this type used to color [TechnicalMachineItem].
 * @param secondaryColor: The secondary color of this type used to color [TechnicalMachineItem]
 * @param typeGem: The Type Gem item used to craft a [TechnicalMachineItem] of this type.
 */
class ElementalType(
    val name: String,
    val displayName: MutableText,
    val hue: Int,
    val textureXMultiplier: Int,
    val resourceLocation: Identifier = Identifier.of(Cobblemon.MODID, "ui/types.png"),
    val primaryColor: Int,
    val secondaryColor: Int,
    val typeGem: Identifier
)