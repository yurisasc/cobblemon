/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex

import net.minecraft.text.Text

object PokedexGUIConstants {
    const val BASE_WIDTH = 350
    const val BASE_HEIGHT = 200
    const val HEADER_HEIGHT = 15
    const val SPACER = 5
    const val MARGIN = 2
    const val SCROLL_HEIGHT = 180
    const val SCROLL_WIDTH = 90
    const val POKEMON_PORTRAIT_HEIGHT = 105
    const val POKEMON_PORTRAIT_WIDTH = 245
    const val POKEMON_DESCRIPTION_WIDTH = 160
    const val POKEMON_DESCRIPTION_HEIGHT = 70
    const val POKEMON_FORMS_WIDTH = 80
    const val POKEMON_FORMS_HEIGHT = 70
    const val PORTRAIT_SIZE = 66
    const val SCALE = 0.5F
    const val SCROLL_SLOT_COUNT = 9
    const val SCROLL_SLOT_HEIGHT = SCROLL_HEIGHT / SCROLL_SLOT_COUNT
    const val SCROLL_BAR_WIDTH = 5

    //PokemonInfoWidget
    const val TEXT_HEIGHT = 10
    const val HEIGHT_Y_POSITION = POKEMON_PORTRAIT_HEIGHT - TEXT_HEIGHT * 3
    const val WEIGHT_Y_POSITION = POKEMON_PORTRAIT_HEIGHT - TEXT_HEIGHT * 2
    const val ANIMATION_BUTTON_Y_POSITION = POKEMON_PORTRAIT_HEIGHT - TEXT_HEIGHT
}