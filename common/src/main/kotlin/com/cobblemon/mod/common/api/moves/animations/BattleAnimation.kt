package com.cobblemon.mod.common.api.moves.animations

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import net.minecraft.util.Identifier

class BattleAnimation {
    
    var pokemon: PokemonProperties? = null
    var move: MoveTemplate? = null
    var timeline: Map<Float, MoveAnimationKeyframe> = mapOf()
}

class MoveAnimationKeyframe {
    val sound: Identifier? = null
    val
}
