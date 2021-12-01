package com.cablemc.pokemoncobbled.common.pokemon

import net.minecraft.world.entity.EntityDimensions

class PokemonForm {
    var name = "normal"
    var baseScale = 1.0f
    var hitbox = EntityDimensions(1f, 1f, false)
}

val NORMAL_FORM = PokemonForm()