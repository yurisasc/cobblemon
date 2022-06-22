package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroup
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.google.gson.annotations.SerializedName
import net.minecraft.entity.EntityDimensions

class FormData(
    @SerializedName("name")
    val name: String = "normal",
    @SerializedName("baseStats")
    private val _baseStats: Map<Stat, Int>? = null,
    @SerializedName("maleRatio")
    private val _maleRatio: Float? = null,
    @SerializedName("baseScale")
    private var _baseScale: Float? = null,
    @SerializedName("hitbox")
    private var _hitbox: EntityDimensions? = null,
    @SerializedName("catchRate")
    private var _catchRate: Int? = null,
    @SerializedName("experienceGroup")
    private var _experienceGroup: ExperienceGroup? = null,
    @SerializedName("baseExperienceYield")
    private var _baseExperienceYield: Int? = null,
    @SerializedName("primaryType")
    private val _primaryType: ElementalType? = null,
    @SerializedName("secondaryType")
    private val _secondaryType: ElementalType? = null,
    @SerializedName("standardAbilities")
    private val _standardAbilities: List<AbilityTemplate>? = null,
    @SerializedName("hiddenAbility")
    private val _hiddenAbility: AbilityTemplate? = null,
    @SerializedName("shoulderMountable")
    private val _shoulderMountable: Boolean? = null,
    @SerializedName("shoulderEffects")
    private val _shoulderEffects: MutableList<ShoulderEffect>? = null,
    @SerializedName("levelUpMoves")
    private val _levelUpMoves: LevelUpMoves? = null,
    private val eyeHeight: Float? = null,
    private val standingEyeHeight: Float? = null,
    private val swimmingEyeHeight: Float? = null,
    private val flyingEyeHeight: Float? = null
) {
    val baseStats: Map<Stat, Int>
        get() = _baseStats ?: species.baseStats

    val maleRatio: Float?
        get() = _maleRatio ?: species.maleRatio
    val baseScale: Float
        get() = _baseScale ?: species.baseScale
    val hitbox: EntityDimensions
        get() = _hitbox ?: species.hitbox
    val catchRate: Int
        get() = _catchRate ?: species.catchRate
    val experienceGroup: ExperienceGroup
        get() = _experienceGroup ?: species.experienceGroup
    val baseExperienceYield: Int
        get() = _baseExperienceYield ?: species.baseExperienceYield
    val primaryType: ElementalType
        get() = _primaryType ?: species.primaryType

    val secondaryType: ElementalType?
        get() = _secondaryType ?: species.secondaryType

    val standardAbilities: List<AbilityTemplate>
        get() = _standardAbilities ?: species.standardAbilities

    val hiddenAbility: AbilityTemplate?
        get() = _hiddenAbility

    val shoulderMountable: Boolean
        get() = _shoulderMountable ?: species.shoulderMountable

    val shoulderEffects: MutableList<ShoulderEffect>
        get() = _shoulderEffects ?: species.shoulderEffects

    val levelUpMoves: LevelUpMoves
        get() = _levelUpMoves ?: species.levelUpMoves

    val types: Iterable<ElementalType>
        get() = secondaryType?.let { listOf(primaryType, it) } ?: listOf(primaryType)

    var aspects = mutableListOf<String>()

    fun eyeHeight(entity: PokemonEntity): Float {
        val multiplier = this.resolveEyeHeight(entity) ?: return this.species.eyeHeight(entity)
        return entity.height * multiplier
    }

    private fun resolveEyeHeight(entity: PokemonEntity): Float? = when {
        entity.isSwimming || entity.isSubmergedInWater -> this.swimmingEyeHeight
        entity.isFallFlying -> this.flyingEyeHeight
        else -> this.standingEyeHeight
    }

    @Transient
    lateinit var species: Species

    override fun equals(other: Any?): Boolean {
        return other is FormData
                && other.species.name.equals(this.species.name, true)
                && other.name.equals(this.name, true)
    }

}
