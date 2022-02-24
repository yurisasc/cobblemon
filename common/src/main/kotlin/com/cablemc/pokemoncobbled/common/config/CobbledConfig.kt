package com.cablemc.pokemoncobbled.common.config

import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint

class CobbledConfig {

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 1, max = 1000)
    var maxPokemonLevel = 100

}