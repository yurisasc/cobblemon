package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategories
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.battles.MoveTarget
import com.cablemc.pokemoncobbled.common.util.lang
import com.google.gson.annotations.SerializedName
import net.minecraft.network.chat.TranslatableText

/**
 * This class represents the base of a Move.
 * To build a Move you need to use its template
 *
 * @param name The English name used to load / find it (spaces -> _)
 * @param elementalType The Type of the Move (e.g. ElementalTypes.FIRE)
 * @param damageCategory The Damage Category of the move (e.g. DamageCategories.SPECIAL)
 * @param power The power of the Move (loaded from its JSON)
 * @param accuracy The accuracy of the Move (loaded from its JSON)
 * @param effectChance The effect chance of the Move (loaded from its JSON) (-1 if not applicable)
 * @param maxPp The maximum PP the Move can have (loaded from its JSON)
 */
open class MoveTemplate(
    val name: String,
    @SerializedName("type")
    val elementalType: ElementalType,
    val damageCategory: DamageCategory,
    val power: Double,
    val target: MoveTarget,
    val accuracy: Double,
    val pp: Int,
    val priority: Int,
    val critRatio: Double,
    val effectChance: Double,
    val effectStatus: String
) {
    val displayName: TranslatableText
        get() = lang("move.$name")
    val description: TranslatableText
        get() = lang("move.$name.desc")
    val maxPp: Int
        get() = 8 * pp / 5
    class Dummy(name: String) : MoveTemplate(
        name = name,
        elementalType = ElementalTypes.NORMAL,
        damageCategory = DamageCategories.STATUS,
        power = 0.0,
        target = MoveTarget.all,
        accuracy = 100.0,
        pp = 5,
        priority = 0,
        critRatio = 0.0,
        effectChance = 0.0,
        effectStatus = ""
    )

    companion object {
        fun dummy(name: String) = Dummy(name)
    }

    /**
     * Creates the Move with full PP
     */
    fun create() = create(pp)

    /**
     * Creates the Move with given PP out of the normal maximum
     */
    fun create(currentPp: Int) = create(currentPp, 0)

    /**
     * Creates the Move with given current PP and the given raised PP stages.
     */
    fun create(currentPp: Int, raisedPpStages: Int): Move {
        return Move(
            currentPp = currentPp,
            raisedPpStages = raisedPpStages,
            template = this
        )
    }
}
