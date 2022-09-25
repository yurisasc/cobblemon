/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.permission

import com.cablemc.pokemoncobbled.common.PokemonCobbled

object CobbledPermissions {

    private const val COMMAND_PREFIX = "${PokemonCobbled.MODID}.command."

    const val CHANGE_SCALE_AND_SIZE = "${COMMAND_PREFIX}changescaleandsize"
    const val CHECKSPAWNS = "${COMMAND_PREFIX}checkspawns"
    const val GET_NBT = "${COMMAND_PREFIX}getnbt"
    const val GIVE_POKEMON = "${COMMAND_PREFIX}givepokemon"
    const val HEAL_POKEMON = "${COMMAND_PREFIX}healpokemon"
    const val HEAL_POKEMON_OTHER = "$HEAL_POKEMON.other"
    const val LEVEL_UP = "${COMMAND_PREFIX}levelup"
    const val LEVEL_UP_OTHER = "$LEVEL_UP.other"
    const val OPEN_STARTER_SCREEN = "${COMMAND_PREFIX}openstarterscreen"
    const val POKEMON_EDIT = "${COMMAND_PREFIX}pokemonedit"
    const val POKEMON_EDIT_OTHER = "$POKEMON_EDIT.other"
    const val SPAWN_ALL_POKEMON = "${COMMAND_PREFIX}spawnallpokemon"
    const val SPAWN_POKEMON = "${COMMAND_PREFIX}spawnpokemon"
    const val STOP_BATTLE = "${COMMAND_PREFIX}stopbattle"
    const val TAKE_POKEMON = "${COMMAND_PREFIX}takepokemon"
    const val TEACH = "${COMMAND_PREFIX}teach"

}