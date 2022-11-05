/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.permission

import com.cobblemon.mod.common.Cobblemon

object CobblemonPermissions {

    private const val COMMAND_PREFIX = "${Cobblemon.MODID}.command."

    const val CHANGE_SCALE_AND_SIZE = "${COMMAND_PREFIX}changescaleandsize"

    const val CHECKSPAWNS = "${COMMAND_PREFIX}checkspawns"

    const val GET_NBT = "${COMMAND_PREFIX}getnbt"

    private const val GIVE_POKEMON_BASE = "${COMMAND_PREFIX}givepokemon"
    const val GIVE_POKEMON_SELF = "$GIVE_POKEMON_BASE.self"
    const val GIVE_POKEMON_OTHER = "$GIVE_POKEMON_BASE.other"

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

    const val FRIENDSHIP = "${COMMAND_PREFIX}friendship"

}