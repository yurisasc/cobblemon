package com.cablemc.pokemoncobbled.forge.common.api.pokemon

import com.cablemc.pokemoncobbled.common.api.item.Flavor
import com.cablemc.pokemoncobbled.forge.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.forge.common.pokemon.Nature
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

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
        Nature(cobbledResource("hardy"),
        null, null, null, null)
    )

    val LONELY = registerNature(
        Nature(cobbledResource("lonely"),
        Stats.ATTACK, Stats.DEFENCE, Flavor.SPICY, Flavor.SOUR)
    )

    val BRAVE = registerNature(
        Nature(cobbledResource("brave"),
        Stats.ATTACK, Stats.SPEED, Flavor.SPICY, Flavor.SWEET)
    )

    val ADAMANT = registerNature(
        Nature(cobbledResource("adamant"),
        Stats.ATTACK, Stats.SPECIAL_ATTACK, Flavor.SPICY, Flavor.DRY)
    )

    val NAUGHTY = registerNature(
        Nature(cobbledResource("naughty"),
        Stats.ATTACK, Stats.SPECIAL_DEFENCE, Flavor.SPICY, Flavor.BITTER)
    )

    val BOLD = registerNature(
        Nature(cobbledResource("bold"),
        Stats.DEFENCE, Stats.ATTACK, Flavor.SOUR, Flavor.SPICY)
    )

    val DOCILE = registerNature(
        Nature(cobbledResource("docile"),
        null, null, null, null)
    )

    val RELAXED = registerNature(
        Nature(cobbledResource("relaxed"),
        Stats.DEFENCE, Stats.SPEED, Flavor.SOUR, Flavor.SWEET)
    )

    val IMPISH = registerNature(
        Nature(cobbledResource("impish"),
        Stats.DEFENCE, Stats.SPECIAL_ATTACK, Flavor.SOUR, Flavor.DRY)
    )

    val LAX = registerNature(
        Nature(cobbledResource("lax"),
        Stats.DEFENCE, Stats.SPECIAL_DEFENCE, Flavor.SOUR, Flavor.BITTER)
    )

    val TIMID = registerNature(
        Nature(cobbledResource("timid"),
        Stats.SPEED, Stats.ATTACK, Flavor.SWEET, Flavor.SPICY)
    )

    val HASTY = registerNature(
        Nature(cobbledResource("hasty"),
        Stats.SPEED, Stats.DEFENCE, Flavor.SWEET, Flavor.SOUR)
    )

    val SERIOUS = registerNature(
        Nature(cobbledResource("serious"),
        null, null, null, null)
    )

    val JOLLY = registerNature(
        Nature(cobbledResource("jolly"),
        Stats.SPEED, Stats.SPECIAL_ATTACK, Flavor.SWEET, Flavor.DRY)
    )

    val NAIVE = registerNature(
        Nature(cobbledResource("naive"),
        Stats.SPEED, Stats.SPECIAL_DEFENCE, Flavor.SWEET, Flavor.BITTER)
    )

    val MODEST = registerNature(
        Nature(cobbledResource("modest"),
        Stats.SPECIAL_ATTACK, Stats.ATTACK, null, null)
    )

    val MILD = registerNature(
        Nature(cobbledResource("mild"),
        Stats.SPECIAL_ATTACK, Stats.DEFENCE, Flavor.DRY, Flavor.SOUR)
    )

    val QUIET = registerNature(
        Nature(cobbledResource("quiet"),
        Stats.SPECIAL_ATTACK, Stats.SPEED, Flavor.DRY, Flavor.SWEET)
    )

    val BASHFUL = registerNature(
        Nature(cobbledResource("bashful"),
        null, null, null, null)
    )

    val RASH = registerNature(
        Nature(cobbledResource("rash"),
        Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Flavor.DRY, Flavor.BITTER)
    )

    val CALM = registerNature(
        Nature(cobbledResource("calm"),
        Stats.SPECIAL_DEFENCE, Stats.ATTACK, Flavor.BITTER, Flavor.SPICY)
    )

    val GENTLE = registerNature(
        Nature(cobbledResource("gentle"),
        Stats.SPECIAL_DEFENCE, Stats.DEFENCE, Flavor.BITTER, Flavor.SOUR)
    )

    val SASSY = registerNature(
        Nature(cobbledResource("sassy"),
        Stats.SPECIAL_DEFENCE, Stats.SPEED, Flavor.BITTER, Flavor.SWEET)
    )

    val CAREFUL = registerNature(
        Nature(cobbledResource("careful"),
        Stats.SPECIAL_DEFENCE, Stats.SPECIAL_ATTACK, Flavor.BITTER, Flavor.DRY)
    )

    val QUIRKY = registerNature(
        Nature(cobbledResource("quirky"),
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
    fun getNature(name: ResourceLocation): Nature? {
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