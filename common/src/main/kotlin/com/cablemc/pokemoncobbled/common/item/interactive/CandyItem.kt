package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.interaction.ExperienceCandyUseEvent
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.item.CobbledItemGroups
import com.cablemc.pokemoncobbled.common.item.interactive.CandyItem.Calculator
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/**
 * An experience candy item.
 * See Bulbapedias [RareCandy](https://bulbapedia.bulbagarden.net/wiki/Rare_Candy) & [ExperienceCandy](https://bulbapedia.bulbagarden.net/wiki/Exp._Candy) articles.
 *
 * @property calculator The [Calculator] that will resolve the amount of experience to give.
 *
 * @author Licious
 * @since May 5th, 2022
 */
class CandyItem(
    val calculator: Calculator
) : PokemonInteractiveItem(Settings().group(CobbledItemGroups.MEDICINE_ITEM_GROUP), Ownership.OWNER) {

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack) {
        val pokemon = entity.pokemon
        val experience = this.calculator.calculate(player, pokemon)
        CobbledEvents.EXPERIENCE_CANDY_USE_PRE.postThen(
            event = ExperienceCandyUseEvent.Pre(player, pokemon, this, experience, experience),
            ifSucceeded = { preEvent ->
                val finalExperience = preEvent.experienceYield
                CobbledEvents.EXPERIENCE_CANDY_USE_POST.post(ExperienceCandyUseEvent.Post(player, pokemon, this, finalExperience))
                pokemon.addExperienceWithPlayer(player, finalExperience)
                this.consumeItem(player, stack)
            }
        )
    }

    /**
     * Functional interface responsible for resolving the experience a candy will yield.
     *
     * @author Licious
     * @since March 5th, 2022
     */
    fun interface Calculator {

        /**
         * Resolves the experience the [CandyItem] will give.
         *
         * @param player The [ServerPlayerEntity] using the candy.
         * @param pokemon The [Pokemon] receiving experience.
         * @return The experience that will be received
         */
        fun calculate(player: ServerPlayerEntity, pokemon: Pokemon): Int

    }

    companion object {

        const val DEFAULT_XS_CANDY_YIELD = 100
        const val DEFAULT_S_CANDY_YIELD = 800
        const val DEFAULT_M_CANDY_YIELD = 3000
        const val DEFAULT_L_CANDY_YIELD = 10000
        const val DEFAULT_XL_CANDY_YIELD = 30000

    }

}