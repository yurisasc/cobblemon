/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokemon.experience

import com.cablemc.pokemoncobbled.common.api.CachedLevelThresholds
import com.cablemc.pokemoncobbled.common.api.LevelCurve
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.math.pow
import com.google.gson.*
import net.minecraft.text.MutableText
import java.lang.reflect.Type
import kotlin.math.max

/**
 * A collection of all the [ExperienceGroup]s a Pokémon species can have.
 *
 * This can be edited but you should really only edit this early in the lifecycle.
 *
 * @author Hiroku
 * @since March 21st, 2022
 */
object ExperienceGroups : Iterable<ExperienceGroup> {
    private val groups = mutableListOf<ExperienceGroup>()
    override fun iterator() = groups.iterator()

    fun findByName(name: String) = find { it.name.equals(name, ignoreCase = true) }
    fun register(experienceGroup: ExperienceGroup): ExperienceGroup = experienceGroup.also { groups.add(it) }
    fun unregister(experienceGroup: ExperienceGroup) = groups.remove(experienceGroup)

    fun registerDefaults() {
        register(Erratic)
        register(Fast)
        register(MediumFast)
        register(MediumSlow)
        register(Slow)
        register(Fluctuating)
    }
}

object ExperienceGroupAdapter : JsonSerializer<ExperienceGroup>, JsonDeserializer<ExperienceGroup> {
    override fun serialize(experienceGroup: ExperienceGroup, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(experienceGroup.name)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): ExperienceGroup {
        return ExperienceGroups.findByName(json.asString) ?: ExperienceGroup.dummy(json.asString)
    }
}

/**
 * A Pokémon's experience group, as an implementation of [LevelCurve]. Complicated experience groups
 * should extend [CachedExperienceGroup] to save you from having to invert the level-to-experience
 * equation.
 *
 * @author Hiroku
 * @since March 21st, 2022
 */
interface ExperienceGroup : LevelCurve {
    val name: String
    val translatedName: MutableText
        get() = lang("experience_group.${name.lowercase()}")

    companion object {
        fun dummy(name: String) = object : ExperienceGroup {
            override val name = name
            override fun getExperience(level: Int) = 0
            override fun getLevel(experience: Int) = 1
        }
    }
}

/**
 * An experience group which uses [CachedLevelThresholds] to answer how
 * to get a level from an experience value, given only the opposite equation.
 *
 * Mainly because my maths skills have deteriorated over the years and I don't
 * trust myself to invert these equations manually.
 *
 * @author Hiroku
 * @since March 21st, 2022
 */
abstract class CachedExperienceGroup : ExperienceGroup {
    private val thresholds = CachedLevelThresholds(experienceToLevel = this::getExperience)
    override fun getLevel(experience: Int) = thresholds.getLevel(experience)
}

object Erratic : CachedExperienceGroup() {
    override val name = "erratic"
    override fun getExperience(level: Int): Int {
        return when {
            level == 1 -> 0
            level < 50 -> level.pow(3) * (100 - level) / 50
            level < 68 -> level.pow(3) * (150 - level) / 100
            level < 98 -> level.pow(3) * (1911 - 10 * level) / 3 / 500
            else -> level.pow(3) * (160 - level) / 100
        }
    }
}

object Fast : CachedExperienceGroup() {
    override val name = "fast"
    override fun getExperience(level: Int) = if (level == 1) 0 else 4 * level.pow(3) / 5
}

object MediumFast : CachedExperienceGroup() {
    override val name = "mediumfast"
    override fun getExperience(level: Int) = if (level == 1) 0 else level.pow(3)
}

object MediumSlow : CachedExperienceGroup() {
    override val name = "mediumslow"
    override fun getExperience(level: Int) = max(0, level.pow(3) * 6 / 5 - 15 * level.pow(2) + 100 * level - 140)
}

object Slow : CachedExperienceGroup() {
    override val name = "slow"
    override fun getExperience(level: Int) = if (level == 1) 0 else 5 * level.pow(3) / 4
}

object Fluctuating : CachedExperienceGroup() {
    override val name = "fluctuating"
    override fun getExperience(level: Int): Int {
        return when {
            level == 1 -> 0
            level < 15 -> level.pow(3) * ((level + 1) / 3 + 24) / 50
            level < 36 -> level.pow(3) * (level + 14) / 50
            else -> level.pow(3) * (level / 2 + 32) / 50
        }
    }
}