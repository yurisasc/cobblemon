package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

interface PreEvolutionAdapter : JsonDeserializer<PreEvolution>, JsonSerializer<PreEvolution>