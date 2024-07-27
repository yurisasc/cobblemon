/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Characteristic
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

/**
 * Registry for all characteristics
 * Get or register characteristics
 *
 * @author AlabasterAlibi
 * @since July 10th, 2024
 */
object Characteristics {
    private val allCharacteristics = mutableListOf<Characteristic>()

    val LOVES_TO_EAT = registerCharacteristic(
        Characteristic(
            cobblemonResource("loves_to_eat"),
            "cobblemon.characteristic.loves_to_eat",
            Stats.HP, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val PROUD_OF_ITS_POWER = registerCharacteristic(
        Characteristic(
            cobblemonResource("proud_of_its_power"),
            "cobblemon.characteristic.proud_of_its_power",
            Stats.ATTACK, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val STURDY_BODY = registerCharacteristic(
        Characteristic(
            cobblemonResource("sturdy_body"),
            "cobblemon.characteristic.sturdy_body",
            Stats.DEFENCE, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val LIKES_TO_RUN = registerCharacteristic(
        Characteristic(
            cobblemonResource("likes_to_run"),
            "cobblemon.characteristic.likes_to_run",
            Stats.SPEED, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val HIGHLY_CURIOUS = registerCharacteristic(
        Characteristic(
            cobblemonResource("highly_curious"),
            "cobblemon.characteristic.highly_curious",
            Stats.SPECIAL_ATTACK, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val STRONG_WILLED = registerCharacteristic(
        Characteristic(
            cobblemonResource("strong_willed"),
            "cobblemon.characteristic.strong_willed",
            Stats.SPECIAL_DEFENCE, setOf(0, 5, 10, 15, 20, 25, 30)
        )
    )


    val TAKES_PLENTY_OF_SIESTAS = registerCharacteristic(
        Characteristic(
            cobblemonResource("takes_plenty_of_siestas"),
            "cobblemon.characteristic.takes_plenty_of_siestas",
            Stats.HP, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val LIKES_TO_THRASH_ABOUT = registerCharacteristic(
        Characteristic(
            cobblemonResource("likes_to_thrash_about"),
            "cobblemon.characteristic.likes_to_thrash_about",
            Stats.ATTACK, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val CAPABLE_OF_TAKING_HITS = registerCharacteristic(
        Characteristic(
            cobblemonResource("capable_of_taking_hits"),
            "cobblemon.characteristic.capable_of_taking_hits",
            Stats.DEFENCE, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val ALERT_TO_SOUNDS = registerCharacteristic(
        Characteristic(
            cobblemonResource("alert_to_sounds"),
            "cobblemon.characteristic.alert_to_sounds",
            Stats.SPEED, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val MISCHIEVOUS = registerCharacteristic(
        Characteristic(
            cobblemonResource("mischievous"),
            "cobblemon.characteristic.mischievous",
            Stats.SPECIAL_ATTACK, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val SOMEWHAT_VAIN = registerCharacteristic(
        Characteristic(
            cobblemonResource("somewhat_vain"),
            "cobblemon.characteristic.somewhat_vain",
            Stats.SPECIAL_DEFENCE, setOf(1, 6, 11, 16, 21, 26, 31)
        )
    )


    val NODS_OFF_A_LOT = registerCharacteristic(
        Characteristic(
            cobblemonResource("nods_off_a_lot"),
            "cobblemon.characteristic.nods_off_a_lot",
            Stats.HP, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val A_LITTLE_QUICK_TEMPERED = registerCharacteristic(
        Characteristic(
            cobblemonResource("a_little_quick_tempered"),
            "cobblemon.characteristic.a_little_quick_tempered",
            Stats.ATTACK, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val HIGHLY_PERSISTENT = registerCharacteristic(
        Characteristic(
            cobblemonResource("highly_persistent"),
            "cobblemon.characteristic.highly_persistent",
            Stats.DEFENCE, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val IMPETUOUS_AND_SILLY = registerCharacteristic(
        Characteristic(
            cobblemonResource("impetuous_and_silly"),
            "cobblemon.characteristic.impetuous_and_silly",
            Stats.SPEED, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val THOROUGHLY_CUNNING = registerCharacteristic(
        Characteristic(
            cobblemonResource("thoroughly_cunning"),
            "cobblemon.characteristic.thoroughly_cunning",
            Stats.SPECIAL_ATTACK, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val STRONGLY_DEFIANT = registerCharacteristic(
        Characteristic(
            cobblemonResource("strongly_defiant"),
            "cobblemon.characteristic.strongly_defiant",
            Stats.SPECIAL_DEFENCE, setOf(2, 7, 12, 17, 22, 27)
        )
    )


    val SCATTERS_THINGS_OFTEN = registerCharacteristic(
        Characteristic(
            cobblemonResource("scatters_things_often"),
            "cobblemon.characteristic.scatters_things_often",
            Stats.HP, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val LIKES_TO_FIGHT = registerCharacteristic(
        Characteristic(
            cobblemonResource("likes_to_fight"),
            "cobblemon.characteristic.likes_to_fight",
            Stats.ATTACK, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val GOOD_ENDURANCE = registerCharacteristic(
        Characteristic(
            cobblemonResource("good_endurance"),
            "cobblemon.characteristic.good_endurance",
            Stats.DEFENCE, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val SOMEWHAT_OF_A_CLOWN = registerCharacteristic(
        Characteristic(
            cobblemonResource("somewhat_of_a_clown"),
            "cobblemon.characteristic.somewhat_of_a_clown",
            Stats.SPEED, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val OFTEN_LOST_IN_THOUGHT = registerCharacteristic(
        Characteristic(
            cobblemonResource("often_lost_in_thought"),
            "cobblemon.characteristic.often_lost_in_thought",
            Stats.SPECIAL_ATTACK, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val HATES_TO_LOSE = registerCharacteristic(
        Characteristic(
            cobblemonResource("hates_to_lose"),
            "cobblemon.characteristic.hates_to_lose",
            Stats.SPECIAL_DEFENCE, setOf(3, 8, 13, 18, 23, 28)
        )
    )


    val LIKES_TO_RELAX = registerCharacteristic(
        Characteristic(
            cobblemonResource("likes_to_relax"),
            "cobblemon.characteristic.likes_to_relax",
            Stats.HP, setOf(4, 9, 14, 19, 24, 29)
        )
    )


    val QUICK_TEMPERED = registerCharacteristic(
        Characteristic(
            cobblemonResource("quick_tempered"),
            "cobblemon.characteristic.quick_tempered",
            Stats.ATTACK, setOf(4, 9, 14, 19, 24, 29)
        )
    )


    val GOOD_PERSEVERANCE = registerCharacteristic(
        Characteristic(
            cobblemonResource("good_perseverance"),
            "cobblemon.characteristic.good_perseverance",
            Stats.DEFENCE, setOf(4, 9, 14, 19, 24, 29)
        )
    )


    val QUICK_TO_FLEE = registerCharacteristic(
        Characteristic(
            cobblemonResource("quick_to_flee"),
            "cobblemon.characteristic.quick_to_flee",
            Stats.SPEED, setOf(4, 9, 14, 19, 24, 29)
        )
    )


    val VERY_FINICKY = registerCharacteristic(
        Characteristic(
            cobblemonResource("very_finicky"),
            "cobblemon.characteristic.very_finicky",
            Stats.SPECIAL_ATTACK, setOf(4, 9, 14, 19, 24, 29)
        )
    )


    val SOMEWHAT_STUBBORN = registerCharacteristic(
        Characteristic(
            cobblemonResource("somewhat_stubborn"),
            "cobblemon.characteristic.somewhat_stubborn",
            Stats.SPECIAL_DEFENCE, setOf(4, 9, 14, 19, 24, 29)
        )
    )

    /**
     * Registers a new characteristic
     */
    private fun registerCharacteristic(characteristic: Characteristic): Characteristic {
        allCharacteristics.add(characteristic)
        return characteristic
    }

    /**
     * Gets a characteristic by registry name
     * @return a characteristic type or null
     */
    fun getCharacteristic(name: Identifier): Characteristic? {
        return allCharacteristics.find { characteristic -> characteristic.name == name }
    }

    fun all(): Collection<Characteristic> = this.allCharacteristics.toList()
}