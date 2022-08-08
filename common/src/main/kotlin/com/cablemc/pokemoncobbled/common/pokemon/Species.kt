package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityPool
import com.cablemc.pokemoncobbled.common.api.drop.DropTable
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.ai.PokemonBehaviour
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.entity.EntityDimensions
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class Species {
    var name: String = "bulbasaur"
    val translatedName: MutableText
        get() = lang("species.$name.name")
    val description: MutableText
        get() = lang("species.$name.desc")
    var nationalPokedexNumber = 1

    val baseStats = mapOf<Stat, Int>()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio: Float? = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var baseExperienceYield = 10
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    val primaryType = ElementalTypes.GRASS
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    val secondaryType: ElementalType? = null
    val abilities = AbilityPool()
    val shoulderMountable: Boolean = false
    val shoulderEffects = mutableListOf<ShoulderEffect>()
    val levelUpMoves = LevelUpMoves()
    val features = mutableSetOf<String>()
    private val standingEyeHeight: Float? = null
    private val swimmingEyeHeight: Float? = null
    private val flyingEyeHeight: Float? = null
    val behaviour = PokemonBehaviour()
    val drops = DropTable()

    var forms = mutableListOf(FormData())
    internal val tags = emptySet<String>()

    // Only exists for use of the field in Pokémon do not expose to end user due to how the species/form data is structured
    internal val evolutions: MutableSet<Evolution> = hashSetOf()

    internal val preEvolution: PreEvolution? = null

    @Transient
    lateinit var resourceIdentifier: Identifier

    fun types(form: Int): Iterable<ElementalType> = forms[form].types

    fun create(level: Int = 5) = Pokemon().apply {
        species = this@Species
        this.level = level
        initialize()
    }

    fun getForm(aspects: Set<String>) = forms.firstOrNull { it.aspects.all { it in aspects } }

    fun eyeHeight(entity: PokemonEntity): Float {
        val multiplier = this.resolveEyeHeight(entity) ?: VANILLA_DEFAULT_EYE_HEIGHT
        return entity.height * multiplier
    }

    fun hasTags(vararg tags: String): Boolean = tags.all { tag -> this.tags.any { it.equals(tag, true) } }

    private fun resolveEyeHeight(entity: PokemonEntity): Float? = when {
        entity.isSwimming || entity.isSubmergedInWater -> this.swimmingEyeHeight
        entity.isFallFlying -> this.flyingEyeHeight
        else -> this.standingEyeHeight
    }

    override fun toString() = name

    companion object {
        private const val VANILLA_DEFAULT_EYE_HEIGHT = .85F
    }
}