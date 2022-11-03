/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon

import com.cablemc.pokemod.common.api.item.Flavor
import com.cablemc.pokemod.common.api.pokemon.stats.Stats
import com.cablemc.pokemod.common.pokemon.Nature
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.util.Identifier

/**
 * Registry for all Nature types
 * Get or register nature types
 *
 * @author Deltric
 * @since January 13th, 2022
 */
object Natures {
    private val allNatures = mutableListOf<Nature>()

    val HARDY = registerNature(
        Nature(pokemodResource("hardy"), lang("nature.hardy"),
        null, null, null, null)
    )

    val LONELY = registerNature(
        Nature(pokemodResource("lonely"), lang("nature.lonely"),
        Stats.ATTACK, Stats.DEFENCE, Flavor.SPICY, Flavor.SOUR)
    )

    val BRAVE = registerNature(
        Nature(pokemodResource("brave"), lang("nature.brave"),
        Stats.ATTACK, Stats.SPEED, Flavor.SPICY, Flavor.SWEET)
    )

    val ADAMANT = registerNature(
        Nature(pokemodResource("adamant"), lang("nature.adamant"),
        Stats.ATTACK, Stats.SPECIAL_ATTACK, Flavor.SPICY, Flavor.DRY)
    )

    val NAUGHTY = registerNature(
        Nature(pokemodResource("naughty"), lang("nature.naughty"),
        Stats.ATTACK, Stats.SPECIAL_DEFENCE, Flavor.SPICY, Flavor.BITTER)
    )

    val BOLD = registerNature(
        Nature(pokemodResource("bold"), lang("nature.bold"),
        Stats.DEFENCE, Stats.ATTACK, Flavor.SOUR, Flavor.SPICY)
    )

    val DOCILE = registerNature(
        Nature(pokemodResource("docile"), lang("nature.docile"),
        null, null, null, null)
    )

    val RELAXED = registerNature(
        Nature(pokemodResource("relaxed"), lang("nature.relaxed"),
        Stats.DEFENCE, Stats.SPEED, Flavor.SOUR, Flavor.SWEET)
    )

    val IMPISH = registerNature(
        Nature(pokemodResource("impish"), lang("nature.impish"),
        Stats.DEFENCE, Stats.SPECIAL_ATTACK, Flavor.SOUR, Flavor.DRY)
    )

    val LAX = registerNature(
        Nature(pokemodResource("lax"), lang("nature.lax"),
        Stats.DEFENCE, Stats.SPECIAL_DEFENCE, Flavor.SOUR, Flavor.BITTER)
    )

    val TIMID = registerNature(
        Nature(pokemodResource("timid"), lang("nature.timid"),
        Stats.SPEED, Stats.ATTACK, Flavor.SWEET, Flavor.SPICY)
    )

    val HASTY = registerNature(
        Nature(pokemodResource("hasty"), lang("nature.hasty"),
        Stats.SPEED, Stats.DEFENCE, Flavor.SWEET, Flavor.SOUR)
    )

    val SERIOUS = registerNature(
        Nature(pokemodResource("serious"), lang("nature.serious"),
        null, null, null, null)
    )

    val JOLLY = registerNature(
        Nature(pokemodResource("jolly"), lang("nature.jolly"),
        Stats.SPEED, Stats.SPECIAL_ATTACK, Flavor.SWEET, Flavor.DRY)
    )

    val NAIVE = registerNature(
        Nature(pokemodResource("naive"), lang("nature.naive"),
        Stats.SPEED, Stats.SPECIAL_DEFENCE, Flavor.SWEET, Flavor.BITTER)
    )

    val MODEST = registerNature(
        Nature(pokemodResource("modest"), lang("nature.modest"),
        Stats.SPECIAL_ATTACK, Stats.ATTACK, null, null)
    )

    val MILD = registerNature(
        Nature(pokemodResource("mild"), lang("nature.mild"),
        Stats.SPECIAL_ATTACK, Stats.DEFENCE, Flavor.DRY, Flavor.SOUR)
    )

    val QUIET = registerNature(
        Nature(pokemodResource("quiet"), lang("nature.quiet"),
        Stats.SPECIAL_ATTACK, Stats.SPEED, Flavor.DRY, Flavor.SWEET)
    )

    val BASHFUL = registerNature(
        Nature(pokemodResource("bashful"), lang("nature.bashful"),
        null, null, null, null)
    )

    val RASH = registerNature(
        Nature(pokemodResource("rash"), lang("nature.rash"),
        Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Flavor.DRY, Flavor.BITTER)
    )

    val CALM = registerNature(
        Nature(pokemodResource("calm"), lang("nature.calm"),
        Stats.SPECIAL_DEFENCE, Stats.ATTACK, Flavor.BITTER, Flavor.SPICY)
    )

    val GENTLE = registerNature(
        Nature(pokemodResource("gentle"), lang("nature.gentle"),
        Stats.SPECIAL_DEFENCE, Stats.DEFENCE, Flavor.BITTER, Flavor.SOUR)
    )

    val SASSY = registerNature(
        Nature(pokemodResource("sassy"), lang("nature.sassy"),
        Stats.SPECIAL_DEFENCE, Stats.SPEED, Flavor.BITTER, Flavor.SWEET)
    )

    val CAREFUL = registerNature(
        Nature(pokemodResource("careful"), lang("nature.careful"),
        Stats.SPECIAL_DEFENCE, Stats.SPECIAL_ATTACK, Flavor.BITTER, Flavor.DRY)
    )

    val QUIRKY = registerNature(
        Nature(pokemodResource("quirky"), lang("nature.quirky"),
        null, null, null, null)
    )


    /**
     * Registers a new nature type
     */
    fun registerNature(nature: Nature): Nature {
        allNatures.add(nature)
        return nature
    }

    /**
     * Gets a nature by registry name
     * @return a nature type or null
     */
    fun getNature(name: Identifier): Nature? {
        return allNatures.find { nature -> nature.name == name }
    }

    /**
     * Helper function for a random Nature
     * @return a random nature type
     */
    fun getRandomNature(): Nature {
        return allNatures.random()
    }
}