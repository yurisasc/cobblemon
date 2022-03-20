package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.cablemc.pokemoncobbled.common.util.pokemonStatsOf
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.EntityDimensions

class Species {
    var name: String = "bulbasaur"
    val translatedName: MutableComponent
        get() = "pokemoncobbled.species.$name.name".asTranslated()
    var nationalPokedexNumber = 1

    val baseStats = pokemonStatsOf()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var hitbox = EntityDimensions(1F, 1F, false)
    val primaryType = ElementalTypes.GRASS
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    val secondaryType: ElementalType? = null
    val standardAbilities = listOf<AbilityTemplate>()
    val hiddenAbility: AbilityTemplate? = null
    val shoulderMountable: Boolean = false
    val shoulderEffects = mutableListOf<ShoulderEffect>()

    var forms = mutableListOf(FormData())

    val evolutions by lazy {
        this.evolutionContainers.map { container -> SpeciesLoader.GSON.fromJson<Evolution>(container) }
    }

    @SerializedName("evolutions")
    private val evolutionContainers = mutableListOf<JsonObject>()

    fun types(form: Int): Iterable<ElementalType> = forms[form].types

    /**
     * Queries for [Evolution]s of a specific type
     *
     * @param T The type of [Evolution].
     * @return The [Evolution]s of type [T] if any.
     */
    inline fun <reified T : Evolution> evolutionsOf() = this.evolutions.filterIsInstance<T>()

    fun create() = Pokemon().apply { species = this@Species }

}