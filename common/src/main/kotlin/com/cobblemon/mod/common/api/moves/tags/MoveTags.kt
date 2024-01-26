/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.tags

import com.cobblemon.mod.common.api.moves.Moves

object MoveTags {

    private val moves = Moves.all()

    val PRIORITY_ZERO = MoveTag(moves.filter { it.priority == 0 })

    /* Showdown flags */
    val PROTECT = MoveTag(moves.filter { it.flags.contains("protect") })
    val CONTACT = MoveTag(moves.filter { it.flags.contains("contact") })
    val DISTANCE = MoveTag(moves.filter { it.flags.contains("distance") })
    val ALLY_ANIM = MoveTag(moves.filter { it.flags.contains("allyanim") })
    val FAIL_ENCORE = MoveTag(moves.filter { it.flags.contains("failencore") })
    val FAIL_COPYCAT = MoveTag(moves.filter { it.flags.contains("failcopycat") })
    val REFLECTABLE = MoveTag(moves.filter { it.flags.contains("reflectable") })
    val BITE = MoveTag(moves.filter { it.flags.contains("bite") })
    val NON_SKY = MoveTag(moves.filter { it.flags.contains("nonsky") })
    val GRAVITY = MoveTag(moves.filter { it.flags.contains("gravity") })
    val POWDER = MoveTag(moves.filter { it.flags.contains("powder") })
    val PLEDGE_COMBO = MoveTag(moves.filter { it.flags.contains("pledgecombo") })
    val MIRROR = MoveTag(moves.filter { it.flags.contains("mirror") })
    val SNATCH = MoveTag(moves.filter { it.flags.contains("snatch") })
    val SLICING = MoveTag(moves.filter { it.flags.contains("slicing") })
    val WIND = MoveTag(moves.filter { it.flags.contains("wind") })
    val NO_SLEEP_TALK = MoveTag(moves.filter { it.flags.contains("nosleeptalk") })
    val FAIL_INSTRUCT = MoveTag(moves.filter { it.flags.contains("failinstruct") })
    val PULSE = MoveTag(moves.filter { it.flags.contains("pulse") })
    val RECHARGE = MoveTag(moves.filter { it.flags.contains("recharge") })
    val SOUND = MoveTag(moves.filter { it.flags.contains("sound") })
    val PUNCH = MoveTag(moves.filter { it.flags.contains("punch") })
    val FUTURE_MOVE = MoveTag(moves.filter { it.flags.contains("futuremove") })
    val MUST_PRESSURE = MoveTag(moves.filter { it.flags.contains("mustpressure") })
    val HEAL = MoveTag(moves.filter { it.flags.contains("heal") })
    val BULLET = MoveTag(moves.filter { it.flags.contains("bullet") })
    val BYPASS_SUB = MoveTag(moves.filter { it.flags.contains("bypasssub") })
    val DANCE = MoveTag(moves.filter { it.flags.contains("dance") })
    val NO_ASSIST = MoveTag(moves.filter { it.flags.contains("noassist") })
    val FAIL_MIMIC = MoveTag(moves.filter { it.flags.contains("failmimic") })
    val FAIL_ME_FIRST = MoveTag(moves.filter { it.flags.contains("failmefirst") })
    val CANT_USE_TWICE = MoveTag(moves.filter { it.flags.contains("cantusetwice") })
    val CHARGE = MoveTag(moves.filter { it.flags.contains("charge") })
    val DEFROST = MoveTag(moves.filter { it.flags.contains("defrost") })
    val NO_PARENTAL_BOND = MoveTag(moves.filter { it.flags.contains("noparentalbond") })
}