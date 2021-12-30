package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import com.google.gson.annotations.SerializedName
import net.minecraft.world.entity.EntityDimensions

data class FormData(
    @SerializedName("name")
    val name: String = "normal",
    @SerializedName("baseStats")
    private val _baseStats: MutableMap<Stat, Int>? = null,
    @SerializedName("maleRatio")
    private val _maleRatio: Float? = null,
    @SerializedName("baseScale")
    private var _baseScale: Float? = null,
    @SerializedName("hitbox")
    private var _hitbox: EntityDimensions? = null,
    @SerializedName("catchRate")
    private var _catchRate: Int? = null
) {
    val baseStats: MutableMap<Stat, Int>
        get() = _baseStats ?: species.baseStats

    val maleRatio: Float
        get() = _maleRatio ?: species.maleRatio
    val baseScale: Float
        get() = _baseScale ?: species.baseScale
    val hitbox: EntityDimensions
        get() = _hitbox ?: species.hitbox
    val catchRate: Int
        get() = _catchRate ?: species.catchRate

    @Transient
    lateinit var species: Species
}