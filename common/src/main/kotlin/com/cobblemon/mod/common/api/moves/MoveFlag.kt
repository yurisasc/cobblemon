/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import net.minecraft.util.StringRepresentable

enum class MoveFlag(private val showdownId: String) : StringRepresentable, ShowdownIdentifiable {

    /**
     * The move plays its animation when used on an ally.
     */
    ALLY_ANIMATION("allyanim"),

    /**
     * Ignores a target's substitute.
     */
    BYPASS_SUBSTITUTE("bypasssub"),

    /**
     * Power is multiplied by 1.5 when used by a Pokemon with the Ability Strong Jaw.
     */
    BITE("bite"),

    /**
     * Has no effect on Pokemon with the Ability Bulletproof.
     */
    BULLET("bullet"),

    /**
     * The user cannot select this move after a previous successful use.
     */
    CANT_USE_TWICE("cantusetwice"),

    /**
     * The user is unable to make a move between turns.
     */
    CHARGE("charge"),

    /**
     * Makes contact.
     */
    CONTACT("contact"),

    /**
     * When used by a Pokemon, other Pokemon with the Ability Dancer can attempt to execute the same move.
     */
    DANCE("dance"),

    /**
     * Thaws the user if executed successfully while the user is frozen.
     */
    DEFROST("defrost"),

    /**
     * Can target a Pokemon positioned anywhere in a Triple Battle.
     */
    DISTANCE("distance"),

    /**
     * Cannot be selected by Copycat.
     */
    FAIL_COPYCAT("failcopycat"),

    /**
     * Encore fails if target used this move.
     */
    FAIL_ENCORE("failencore"),

    /**
     * Cannot be repeated by Instruct.
     */
    FAIL_INSTRUCT("failinstruct"),

    /**
     * Cannot be selected by Me First.
     */
    FAIL_ME_FIRST("failmefirst"),

    /**
     * Cannot be copied by Mimic.
     */
    FAIL_MIMIC("failmimic"),

    /**
     * Targets a slot, and in 2 turns damages that slot.
     */
    FUTURE_MOVE("futuremove"),

    /**
     * Prevented from being executed or selected during Gravity's effect.
     */
    GRAVITY("gravity"),

    /**
     * Prevented from being executed or selected during Heal Block's effect.
     */
    HEAL("heal"),

    /**
     * Can be selected by Metronome.
     */
    METRONOME("metronome"),

    /**
     * Can be copied by Mirror Move.
     */
    MIRROR("mirror"),

    /**
     * Additional PP is deducted due to Pressure when it ordinarily would not.
     */
    MUST_PRESSURE("mustpressure"),

    /**
     * Cannot be selected by Assist.
     */
    NO_ASSIST("noassist"),

    /**
     * Prevented from being executed or selected in a Sky Battle.
     */
    NON_SKY("nonsky"),

    /**
     * Cannot be made to hit twice via Parental Bond.
     */
    NO_PARENTAL_BOND("noparentalbond"),

    /**
     * Cannot be selected by Sleep Talk.
     */
    NO_SLEEP_TALK("nosleeptalk"),

    /**
     * Gems will not activate. Cannot be redirected by Storm Drain / Lightning Rod.
     */
    PLEDGE_COMBO("pledgecombo"),

    /**
     * Has no effect on Pokemon which are Grass-type, have the Ability Overcoat, or hold Safety Goggles.
     */
    POWDER("powder"),

    /**
     * Blocked by Detect, Protect, Spiky Shield, and if not a Status move, King's Shield.
     */
    PROTECT("protect"),

    /**
     * Power is multiplied by 1.5 when used by a Pokemon with the Ability Mega Launcher.
     */
    PULSE("pulse"),

    /**
     * Power is multiplied by 1.2 when used by a Pokemon with the Ability Iron Fist.
     */
    PUNCH("punch"),

    /**
     * If this move is successful, the user must recharge on the following turn and cannot make a move.
     */
    RECHARGE("recharge"),

    /**
     * Bounced back to the original user by Magic Coat or the Ability Magic Bounce.
     */
    REFLECTABLE("reflectable"),

    /**
     * Power is multiplied by 1.5 when used by a Pokemon with the Ability Sharpness.
     */
    SLICING("slicing"),

    /**
     * Can be stolen from the original user and instead used by another Pokemon using Snatch.
     */
    SNATCH("snatch"),

    /**
     * Has no effect on Pokemon with the Ability Soundproof.
     */
    SOUND("sound"),

    /**
     * Activates the Wind Power and Wind Rider Abilities.
     */
    WIND("wind");
    override fun getSerializedName(): String = this.name

    override fun showdownId(): String = this.showdownId

    companion object {
        @JvmStatic
        val CODEC = StringRepresentable.fromEnum(MoveFlag::values)
    }

}