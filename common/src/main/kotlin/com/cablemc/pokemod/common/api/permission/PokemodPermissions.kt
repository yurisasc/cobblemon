/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.permission

import com.cablemc.pokemod.common.Pokemod

object PokemodPermissions {

    private const val COMMAND_PREFIX = "${Pokemod.MODID}.command."

    const val CHANGE_SCALE_AND_SIZE = "${COMMAND_PREFIX}changescaleandsize"

    const val CHECKSPAWNS = "${COMMAND_PREFIX}checkspawns"

    const val GET_NBT = "${COMMAND_PREFIX}getnbt"

    const val GIVE_POKEMON = "${COMMAND_PREFIX}givepokemon"

    private const val HEAL_POKEMON_BASE = "${COMMAND_PREFIX}healpokemon"
    const val HEAL_POKEMON_SELF = "$HEAL_POKEMON_BASE.self"
    const val HEAL_POKEMON_OTHER = "$HEAL_POKEMON_BASE.other"

    private const val LEVEL_UP_BASE = "${COMMAND_PREFIX}levelup"
    const val LEVEL_UP_SELF = "$LEVEL_UP_BASE.self"
    const val LEVEL_UP_OTHER = "$LEVEL_UP_BASE.other"

    const val OPEN_STARTER_SCREEN = "${COMMAND_PREFIX}openstarterscreen"

    private const val POKEMON_EDIT_BASE = "${COMMAND_PREFIX}pokemonedit"
    const val POKEMON_EDIT_SELF = "$POKEMON_EDIT_BASE.self"
    const val POKEMON_EDIT_OTHER = "$POKEMON_EDIT_BASE.other"

    const val SPAWN_ALL_POKEMON = "${COMMAND_PREFIX}spawnallpokemon"

    const val SPAWN_POKEMON = "${COMMAND_PREFIX}spawnpokemon"

    const val STOP_BATTLE = "${COMMAND_PREFIX}stopbattle"

    const val TAKE_POKEMON = "${COMMAND_PREFIX}takepokemon"

    const val TEACH = "${COMMAND_PREFIX}teach"

}