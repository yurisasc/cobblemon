package com.cobblemon.mod.common.api.pokemon.stats

import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

/**
 * An enumeration of the default implemented [Stat]s.
 * Contains all the traditional stats in official Pokémon games.
 */
enum class Stats(override val identifier: Identifier, override val displayName: Text, override val type: Stat.Type) : Stat {

    HP(cobblemonResource("stat.hp"), lang("stat.hp.name"), Stat.Type.PERMANENT),
    ATTACK(cobblemonResource("stat.attack"), lang("stat.attack.name"), Stat.Type.PERMANENT),
    DEFENCE(cobblemonResource("stat.defence"), lang("stat.defence.name"), Stat.Type.PERMANENT),
    SPECIAL_ATTACK(cobblemonResource("stat.special_attack"), lang("stat.special_attack.name"), Stat.Type.PERMANENT),
    SPECIAL_DEFENCE(cobblemonResource("stat.special_defence"), lang("stat.special_defence.name"), Stat.Type.PERMANENT),
    SPEED(cobblemonResource("stat.speed"), lang("stat.speed.name"), Stat.Type.PERMANENT),
    EVASION(cobblemonResource("stat.evasion"), lang("stat.evasion.name"), Stat.Type.BATTLE_ONLY),
    ACCURACY(cobblemonResource("stat.accuracy"), lang("stat.accuracy.name"), Stat.Type.BATTLE_ONLY);

    companion object {

        /**
         * All the stats with type of [Stat.Type.PERMANENT].
         * Using [StatProvider.ofType] with type [Stat.Type.PERMANENT] is recommended instead for maximum addon compatibility.
         */
        val PERMANENT: Set<Stat> = EnumSet.of(HP, ATTACK, DEFENCE, SPECIAL_ATTACK, SPECIAL_DEFENCE, SPEED)

        /**
         * All the stats with type of [Stat.Type.BATTLE_ONLY].
         * Using [StatProvider.ofType] with type [Stat.Type.BATTLE_ONLY] is recommended instead for maximum addon compatibility.
         */
        val BATTLE_ONLY: Set<Stat> = EnumSet.of(EVASION, ACCURACY)

    }

}