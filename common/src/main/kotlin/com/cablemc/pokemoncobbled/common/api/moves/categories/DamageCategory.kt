/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.moves.categories

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Representing a DamageCategory from the Pokemon Game
 *
 * @param name: The English name used to load / find it (spaces -> _)
 * @param displayName: A Component used to display the name, normally a TranslatableText
 * @param resourceLocation: The location of the resource used in the GUI
 */
class DamageCategory(
    val name: String,
    val displayName: Text,
    val textureXMultiplier: Int,
    val resourceLocation: Identifier = cobbledResource("ui/categories.png")
) {
}