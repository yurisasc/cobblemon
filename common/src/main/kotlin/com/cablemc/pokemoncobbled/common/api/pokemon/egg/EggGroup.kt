/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokemon.egg

/**
 * These represent categories which determine which Pokémon are able to interbreed.
 *
 * See the [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/Egg_Group) article for more information.
 *
 * @property pokeApiID used for deserialization of egg groups from the PokeAPI data format.
 * @property showdownID used for data synchronization with Showdown data format.
 */
enum class EggGroup(internal val pokeApiID: String, internal val showdownID: String) {

    MONSTER("monster", "Monster"),
    WATER_1("water1", "Water 1"),
    BUG("bug", "Bug"),
    FLYING("flying", "Flying"),
    FIELD("ground", "Field"),
    FAIRY("fairy", "Fairy"),
    GRASS("plant", "Grass"),
    HUMAN_LIKE("humanshape", "Human-Like"),
    WATER_3("water3", "Water 3"),
    MINERAL("mineral", "Mineral"),
    AMORPHOUS("indeterminate", "Amorphous"),
    WATER_2("water2", "Water 2"),
    DITTO("ditto", "Ditto"),
    DRAGON("dragon", "Dragon"),
    UNDISCOVERED("no-eggs", "Undiscovered")

}