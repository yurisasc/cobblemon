package com.cablemc.pokemoncobbled.common.api.battles.model

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class BattlePokemon {

    lateinit var name: String
    lateinit var species: String
    lateinit var gender: String
    lateinit var nature: String
    lateinit var ability: String
    lateinit var item: String
    lateinit var evs: HashMap<String, Int>
    lateinit var ivs: HashMap<String, Int>
    lateinit var moves: Array<String>
    var shiny: Boolean = false
    var happiness: Int = 0

    constructor(pokemon: Pokemon) {
        this.name = "Nickname"
        this.species = pokemon.species.name
        this.gender = "M" // TODO: Implement pokemon species
        this.nature = "Timid" // TODO: Implement pokemon nature
        this.ability = "" // TODO: Implement pokemon ability
        this.item = "" // TODO: Implement pokemon item
    }

}